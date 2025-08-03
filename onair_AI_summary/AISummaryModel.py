import pandas as pd
from datasets import Dataset
from transformers import PreTrainedTokenizerFast
import torch
from transformers import BartForConditionalGeneration, Trainer, TrainingArguments

training_path = '/content/drive/MyDrive/문서요약 텍스트/Training/train_original.json'
validation_path = '/content/drive/MyDrive/문서요약 텍스트/Validation/valid_original.json'

df_train = pd.read_json(training_path)
df_valid = pd.read_json(validation_path)

def preprocess_dataframe(df):
    sources = []
    targets = []
    
    for doc in df['documents']:
        try:
            # 문장 리스트 추출
            sentences = [s['sentence'] for para in doc['text'] for s in para]
            source_text = ' '.join(sentences).strip()

            # 요약문 추출 (가장 첫 번째 항목)
            summary = doc['abstractive'][0].strip() if doc['abstractive'] else ''

            if source_text and summary:
                sources.append(source_text)
                targets.append(summary)
        except Exception as e:
            print(f"Error processing document ID {doc.get('id', 'N/A')}: {e}")
    
    return pd.DataFrame({'source': sources, 'target': targets})


# 전처리
train_ready = preprocess_dataframe(df_train)
valid_ready = preprocess_dataframe(df_valid)

# 저장
train_ready.to_csv('/content/drive/MyDrive/문서요약 텍스트/Training/train.tsv', sep='\t', encoding='utf-8', index=False)
valid_ready.to_csv('/content/drive/MyDrive/문서요약 텍스트/Validation/valid.tsv', sep='\t', encoding='utf-8', index=False)

training_path = '/content/drive/MyDrive/문서요약 텍스트/Training/train.tsv'
validation_path = '/content/drive/MyDrive/문서요약 텍스트/Validation/valid.tsv'

df_train = pd.read_csv(training_path, sep='\t')
df_valid = pd.read_csv(validation_path, sep='\t')

# 필요한 컬럼만 사용
dataset_train = Dataset.from_pandas(df_train[['source', 'target']])
dataset_valid = Dataset.from_pandas(df_valid[['source', 'target']])

# 컬럼명 변경
dataset_train = dataset_train.rename_columns({'source': 'text', 'target': 'summary'})
dataset_valid = dataset_valid.rename_columns({'source': 'text', 'target': 'summary'})

# 셔플
dataset_train = dataset_train.shuffle(seed=42)
dataset_valid = dataset_valid.shuffle(seed=42)

tokenizer = PreTrainedTokenizerFast.from_pretrained("gogamza/kobart-base-v2")

max_input_length = 512
max_target_length = 128

def preprocess_function(examples):
    inputs = tokenizer(
        examples["text"],
        max_length=max_input_length,
        padding="max_length",
        truncation=True,
    )

    with tokenizer.as_target_tokenizer():
        labels = tokenizer(
            examples["summary"],
            max_length=max_target_length,
            padding="max_length",
            truncation=True,
        )

    inputs["labels"] = labels["input_ids"]
    return inputs


model = BartForConditionalGeneration.from_pretrained('gogamza/kobart-base-v2')


# training_args = TrainingArguments(
#     output_dir='./kobart-news-summarization',
#     num_train_epochs=3,
#     per_device_train_batch_size=4,
#     per_device_eval_batch_size=4,
#     eval_strategy='steps',
#     eval_steps=500,
#     save_steps=1000,
#     save_total_limit=2,
#     logging_dir='./logs',
#     logging_steps=100,
#     learning_rate=5e-5,
#     weight_decay=0.01,
#     save_strategy='steps',
#     load_best_model_at_end=True,
#     metric_for_best_model='loss',
#     greater_is_better=False,
#     report_to='none',
# )

training_args = TrainingArguments(
    output_dir="./kobart-summary-fast",
    num_train_epochs=1,                          
    per_device_train_batch_size=2,               
    per_device_eval_batch_size=2,
    gradient_accumulation_steps=2,               
    eval_strategy="steps",                 
    eval_steps=100,
    save_steps=200,
    logging_steps=50,
    save_total_limit=1,                          
    fp16=True,                                   
    report_to="none",                            
    load_best_model_at_end=True,
    metric_for_best_model="loss"
)


trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=tokenized_train,
    eval_dataset=tokenized_valid,
    tokenizer=tokenizer,
)

trainer.train()
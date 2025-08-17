import pandas as pd
from datasets import Dataset
from transformers import BartForConditionalGeneration, Trainer, TrainingArguments, PreTrainedTokenizerFast
import torch
import gc
import os

os.makedirs("C:/Users/Hyuk/Desktop/summary", exist_ok=True)

# 1) GPU 확인
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
print("Using device:", device)

# 2) JSON 파일 불러오기
training_path = 'C:/Users/Hyuk/Desktop/문서요약 텍스트/Training/train_original.json'
validation_path = 'C:/Users/Hyuk/Desktop/문서요약 텍스트/Validation/valid_original.json'

df_train = pd.read_json(training_path)
df_valid = pd.read_json(validation_path)

# 3) 데이터 샘플링 (학습 시간 단축)
df_train = df_train.sample(n=100000, random_state=42)
df_valid = df_valid.sample(n=10000, random_state=42)

# 4) 전처리 함수
def preprocess_dataframe(df):
    sources = []
    targets = []
    for doc in df['documents']:
        try:
            sentences = [s['sentence'] for para in doc['text'] for s in para]
            source_text = ' '.join(sentences).strip()
            summary = doc['abstractive'][0].strip() if doc['abstractive'] else ''
            if source_text and summary:
                sources.append(source_text)
                targets.append(summary)
        except Exception as e:
            print(f"Error processing document ID {doc.get('id', 'N/A')}: {e}")
    return pd.DataFrame({'source': sources, 'target': targets})

train_ready = preprocess_dataframe(df_train)
valid_ready = preprocess_dataframe(df_valid)

# 5) Dataset 생성
dataset_train = Dataset.from_pandas(train_ready)
dataset_valid = Dataset.from_pandas(valid_ready)

dataset_train = dataset_train.rename_columns({'source': 'text', 'target': 'summary'})
dataset_valid = dataset_valid.rename_columns({'source': 'text', 'target': 'summary'})

dataset_train = dataset_train.shuffle(seed=42)
dataset_valid = dataset_valid.shuffle(seed=42)

# 6) 토크나이저
tokenizer = PreTrainedTokenizerFast.from_pretrained("gogamza/kobart-summarization")

max_input_length = 192
max_target_length = 64

def preprocess_function(examples):
    inputs = tokenizer(
        examples["text"],
        max_length=max_input_length,
        padding="max_length",
        truncation=True,
    )
    labels = tokenizer(
        examples["summary"],
        max_length=max_target_length,
        padding="max_length",
        truncation=True,
    )
    inputs["labels"] = labels["input_ids"]
    return inputs



def clear_memory():
    gc.collect()
    torch.cuda.empty_cache()

tokenized_train = dataset_train.map(preprocess_function, batched=True, remove_columns=['text', 'summary'])
tokenized_valid = dataset_valid.map(preprocess_function, batched=True, remove_columns=['text', 'summary'])

# 7) 모델 로드
model = BartForConditionalGeneration.from_pretrained('gogamza/kobart-summarization').to(device)

# 8) TrainingArguments
training_args = TrainingArguments(
    output_dir="C:/Users/Hyuk/Desktop/summary",
    num_train_epochs=2,
    per_device_train_batch_size=8,
    per_device_eval_batch_size=8,
    gradient_accumulation_steps=2,
    evaluation_strategy="steps",
    eval_steps=5000,
    logging_steps=500,
    fp16=True,
    load_best_model_at_end=False,
    metric_for_best_model="loss",
    report_to="none",
    save_strategy="no"
)

# 9) Trainer
trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=tokenized_train,
    eval_dataset=tokenized_valid,
    tokenizer=tokenizer,
)

# # 10) 학습 시작
trainer.train()
model.save_pretrained("C:/Users/Hyuk/Desktop/summary/final_model")
tokenizer.save_pretrained("C:/Users/Hyuk/Desktop/summary/final_model")
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from transformers import PreTrainedTokenizerFast, BartForConditionalGeneration

class SummarizeRequest(BaseModel):
    text: str

class SummarizeResponse(BaseModel):
    summary: str

app = FastAPI()

# 학습 완료 모델 및 토크나이저 로드
MODEL_DIR = "./kobart-summary-fast"  

tokenizer = PreTrainedTokenizerFast.from_pretrained(MODEL_DIR)
model = BartForConditionalGeneration.from_pretrained(MODEL_DIR)
model.eval()

max_input_length = 512
max_target_length = 128

def generate_summary(text: str) -> str:
    # 토크나이징 (입력 전처리)
    inputs = tokenizer(
        text,
        max_length=max_input_length,
        padding="max_length",
        truncation=True,
        return_tensors="pt"
    )
    # 모델 추론
    summary_ids = model.generate(
        inputs["input_ids"], 
        attention_mask=inputs["attention_mask"],
        max_length=max_target_length,
        min_length=30,
        length_penalty=2.0,
        num_beams=4,
        early_stopping=True,
        no_repeat_ngram_size=3
    )
    # 토큰을 텍스트로 디코딩 (후처리)
    summary = tokenizer.decode(summary_ids[0], skip_special_tokens=True)
    return summary

@app.post("/summarize", response_model=SummarizeResponse)
def summarize(req: SummarizeRequest):
    try:
        summary = generate_summary(req.text)
        return SummarizeResponse(summary=summary)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
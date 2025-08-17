from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from transformers import PreTrainedTokenizerFast, BartForConditionalGeneration
from fastapi.middleware.cors import CORSMiddleware
import torch

class SummarizeRequest(BaseModel):
    text: str

class SummarizeResponse(BaseModel):
    summary: str

app = FastAPI()

# CORS 설정
origins = [
    "http://localhost:5173",  # React 개발 서버
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,  # 허용할 origin
    allow_credentials=True,
    allow_methods=["*"],    # GET, POST 등 모든 메서드 허용
    allow_headers=["*"],    # 모든 헤더 허용
)

# 학습 완료 모델 및 토크나이저 로드
MODEL_DIR = "./summary/final_model"  

tokenizer = PreTrainedTokenizerFast.from_pretrained(MODEL_DIR)
model = BartForConditionalGeneration.from_pretrained(MODEL_DIR)
model.eval()

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model.to(device)

max_input_length = 512
max_target_length = 300

def generate_summary(text: str) -> str:
    inputs = tokenizer(
        [text], 
        max_length=192,
        truncation=True,
        padding="max_length",
        return_tensors="pt").to(device)
    
    # 모델 추론
    summary_ids = model.generate(
        inputs["input_ids"],
        attention_mask=inputs["attention_mask"],
        max_length=128,       # 출력 요약 최대 길이
        num_beams=4,         # 빔 서치
        early_stopping=True
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
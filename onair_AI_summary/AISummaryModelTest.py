import torch
from transformers import BartForConditionalGeneration, PreTrainedTokenizerFast

# 1) 저장된 모델과 토크나이저 불러오기
model_path = "./summary/final_model" 
tokenizer = PreTrainedTokenizerFast.from_pretrained(model_path)
model = BartForConditionalGeneration.from_pretrained(model_path)

# GPU 사용 여부 확인
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model.to(device)

# 2) 테스트할 입력 문서
text = """
국내 연구팀이 별도의 이미지 처리 기술 없이도 극단적 밝기 변화에 자동으로 적응할 수 있는 차세대 이미지 센서를 개발해 주목받고 있다. 해당 기술은 자율주행 자동차, 스마트 로봇, 보안 시스템 등 다양한 분야에 적용될 수 있을 것으로 기대된다.

한국연구재단(이사장 홍원화)은 송영민 교수(KAIST)와 강동호 교수(GIST) 공동연구팀이 뇌의 신경 구조에서 착안한 강유전체 기반의 광소자를 개발해 빛의 감지부터 기록, 처리까지 소자 내에서 구현할 수 있는 차세대 이미지 센서를 개발했다고 18일 발표했다.

‘보는 인공지능(AI)’에 대한 수요가 높아지면서 다양한 환경에서도 안정적으로 작동할 수 있는 고성능 시각 센서 개발이 시급한 과제로 떠오르고 있다.
"""

# 3) 토크나이징
inputs = tokenizer([text], max_length=192, truncation=True, padding="max_length", return_tensors="pt").to(device)

# 4) 요약 생성
summary_ids = model.generate(
    inputs["input_ids"],
    attention_mask=inputs["attention_mask"],
    max_length=128,       # 출력 요약 최대 길이
    num_beams=4,         # 빔 서치
    early_stopping=True
)

# 5) 디코딩
summary = tokenizer.decode(summary_ids[0], skip_special_tokens=True)
print("요약 결과:", summary)
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
전국 대부분 지역에 폭염특보가 내려진 가운데, 최고 체감온도가 35도 안팎까지 오르는 찜통더위가 기승을 부리겠다. 경기·인천·강원 지역에는 비가 내리겠다.

17일 기상청에 따르면 한반도는 제주 부근 해상에서 동쪽으로 이동하는 고기압의 영향을 받겠다. 이에 따라 중부지방은 대체로 흐리고 남부지방과 제주는 대체로 맑겠다.

수도권과 강원권을 중심으로는 비 소식이 있다. 예상 강수량은 △경기 북부·인천 10∼60㎜ △강원 중·북부 내륙·산지 10∼60㎜ △서해5도 5∼20㎜다. 서해 중부 먼바다에는 돌풍과 함께 천둥·번개가 치는 곳도 있겠으니 해상 안전에 유의해야 한다.

전국에 발효된 폭염특보 속에 무더위는 계속되겠다. 이날 낮 최고기온은 28∼35도로 예보됐으며, 당분간 최고 체감온도가 33도 안팎으로 오르겠다. 특히 남부지방과 제주를 중심으로는 최고 체감온도가 35도 안팎까지 치솟아 매우 무덥겠다.

밤사이에도 더위가 식지 않아 도심과 해안을 중심으로 최저기온이 25도 이상으로 유지되는 열대야 현상이 나타나는 곳이 많겠다.

주요 지역 아침 최저기온은 서울 25도, 인천 25도, 수원 24도, 춘천 23도, 강릉 26도, 청주 25도, 대전 24도, 광주 24도, 대구 24도, 부산 26도, 제주 26도 등이다.

낮 최고기온은 서울 30도, 인천 29도, 수원 31도, 춘천 30도, 강릉 34도, 청주 33도, 대전 33도, 전주 34도, 광주 34도, 대구 34도, 부산 33도, 제주 33도로 예상된다.

오전까지 강원 영동을 중심으로는 바람이 초속 15∼20m로 매우 강하게 불겠으니 시설물 관리에 주의가 필요하다.

미세먼지 농도는 원활한 대기 확산으로 전 권역이 '좋음'∼'보통' 수준을 보이겠다.
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
import json
from kafka import KafkaConsumer, KafkaProducer
from datetime import datetime
import requests

KAFKA_BROKER = 'localhost:9092'
REQUEST_TOPIC = 'onair-article-summary-request'
RESPONSE_TOPIC = 'onair-article-summary-response'

def call_ai_model_api(text):
    AI_API_URL = 'http://localhost:8000/summarize'
    try:
        response = requests.post(AI_API_URL, json={'text': text})
        response.raise_for_status()
        result = response.json()
        return result.get('summary', '')
    except Exception as e:
        print(f"[AI API 호출 실패] {e}")
        return None

def main():
    consumer = KafkaConsumer(
        REQUEST_TOPIC,
        bootstrap_servers=[KAFKA_BROKER],
        auto_offset_reset='earliest',
        enable_auto_commit=True,
        group_id='ai-summary-service-group',
        value_deserializer=lambda m: m.decode('utf-8')
    )

    producer = KafkaProducer(
        bootstrap_servers=[KAFKA_BROKER],
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )

    print(f"Kafka Consumer started, listening to topic '{REQUEST_TOPIC}' ...")

    for message in consumer:
        try:
            print(f"Received message: {message.value}")
            event = json.loads(message.value)
            payload = event.get('payload', {})
            article_id = payload.get('articleId')
            content = payload.get('content')

            if not article_id or not content:
                print("Invalid message payload, skipping...")
                continue

            print(f"Calling AI model API for articleId={article_id} ...")
            summary = call_ai_model_api(content)
            if summary is None:
                print("AI summary failed, skipping publishing result")
                continue

            # Kafka에 다시 발행할 메시지 구조
            response_event = {
                'eventId': event.get('eventId'),
                'type': 'ARTICLE_SUMMARY_RESPONSE',
                'payload': {
                    'articleId': article_id,
                    'summary': summary,
                    'generatedAt': datetime.utcnow().isoformat() + 'Z'
                }
            }

            print(f"Publishing summary response for articleId={article_id} ...")
            producer.send(RESPONSE_TOPIC, value=response_event)
            producer.flush()

        except Exception as e:
            print(f"Error processing message: {e}")

if __name__ == '__main__':
    main()

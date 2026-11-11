"""Lambda 핸들러: 텍스트를 받아 광고/일반 판정 결과를 리턴."""
import json
import os

import numpy as np
import onnxruntime as ort
from transformers import AutoTokenizer

MODEL_DIR = os.path.dirname(os.path.abspath(__file__))  # 이미지 안에서 이 파일과 모델이 같은 경로에 있음
API_KEY = os.environ.get("API_KEY")  # Lambda 환경변수로 설정, 없으면 인증 체크 생략
LABELS = {0: "일반", 1: "광고"}

tokenizer = AutoTokenizer.from_pretrained(MODEL_DIR)  # 콜드스타트 때 한 번만 로드, 이후 웜 상태에서 재사용
session = ort.InferenceSession(f"{MODEL_DIR}/model.onnx")


def handler(event, context):
    headers = {k.lower(): v for k, v in (event.get("headers") or {}).items()}
    if API_KEY and headers.get("x-api-key") != API_KEY:
        return {"statusCode": 401, "body": json.dumps({"error": "unauthorized"})}

    body = json.loads(event.get("body") or "{}")
    text = body.get("text", "")

    inputs = tokenizer(text, truncation=True, max_length=256, return_tensors="np")
    logits = session.run(
        ["logits"],
        {"input_ids": inputs["input_ids"], "attention_mask": inputs["attention_mask"]},
    )[0][0]

    probs = np.exp(logits) / np.exp(logits).sum()  # softmax
    pred = int(np.argmax(logits))

    return {
        "statusCode": 200,
        "headers": {"Content-Type": "application/json"},
        "body": json.dumps({
            "label": LABELS[pred],
            "is_ad": pred == 1,
            "confidence": float(probs[pred]),
        }),
    }

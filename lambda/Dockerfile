FROM public.ecr.aws/lambda/python:3.12

COPY requirements.txt ${LAMBDA_TASK_ROOT}/
RUN pip install -r ${LAMBDA_TASK_ROOT}/requirements.txt --no-cache-dir

COPY handler.py ${LAMBDA_TASK_ROOT}/
COPY model.onnx tokenizer.json tokenizer_config.json config.json ${LAMBDA_TASK_ROOT}/

CMD ["handler.handler"]

# Use SHA to guarantee the exact version of the Docker image is used between runs
# python version is 3.12-slim
FROM python@sha256:85824326bc4ae27a1abb5bc0dd9e08847aa5fe73d8afb593b1b45b7cb4180f57

WORKDIR /app

COPY requirements.txt .
COPY app.py .
RUN pip install --no-cache-dir -r requirements.txt

EXPOSE 5000

CMD [ "python", "app.py" ]
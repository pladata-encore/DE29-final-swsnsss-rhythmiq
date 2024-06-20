# base image 설정
FROM python:3.11-slim

COPY /mrs_flask_server /app
# 작업 디렉토리 설정
WORKDIR /app

# Set environment variables
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

# Install dependencies
RUN apt update && apt install -y \
    default-jdk \
    && rm -rf /var/lib/apt/lists/*

# 패키지 설치
RUN pip install --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt


EXPOSE 5000

CMD ["python", "main.py", "--host=0.0.0.0"]

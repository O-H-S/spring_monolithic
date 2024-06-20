# Base image with specific JDK version for reproducibility
FROM openjdk:17-jdk-slim as builder

# Label the image
LABEL authors="OHS"

# Install necessary tools for Google Chrome installation
RUN apt-get update && apt-get install -y wget gnupg2 \
    && wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
    && apt-get install -y ./google-chrome-stable_current_amd64.deb \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* /var/cache/apt/* \
    && rm ./google-chrome-stable_current_amd64.deb

# Start a new stage to keep the final image clean and minimal
FROM openjdk:17-jdk-slim

# Install runtime dependencies for Google Chrome
RUN apt-get update && apt-get install -y \
    libglib2.0-0 \
    libnss3 \
    libxshmfence1 \
    libx11-xcb1 \
    libxcb1 \
    libxcomposite1 \
    libxcursor1 \
    libxdamage1 \
    libxi6 \
    libxtst6 \
    libcups2 \
    libxss1 \
    libxrandr2 \
    libasound2 \
    libpangocairo-1.0-0 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    libgbm1 \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* /var/cache/apt/*

# Set the working directory to /app
WORKDIR /app

# COPY . .는 로컬의 현재 디렉토리를 이미지의 /app 디렉토리로 복사합니다.
# 하지만 COPY --from=builder /opt/google/chrome /opt/google/chrome와 같은 절대 경로 명령어는 WORKDIR 설정의 영향을 받지 않습니다. 이 경우, 복사 대상 경로가 절대 경로로 명확히 지정되어 있기 때문입니다.
COPY --from=builder /opt/google/chrome /opt/google/chrome

# Add a symbolic link to ensure commands like 'google-chrome' work
RUN ln -s /opt/google/chrome/google-chrome /usr/bin/google-chrome

# Copy the application JAR file into the working directory
COPY app.jar .

# Set the entrypoint to launch the Java application
ENTRYPOINT ["java", "-jar", "app.jar"]
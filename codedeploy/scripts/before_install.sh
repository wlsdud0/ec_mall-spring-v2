#!/usr/bin/env bash

# 이미지 저장 디렉토리 설정 (변경 가능)
IMAGE_DIR="/home/ubuntu/images"

# Docker 볼륨 생성 (볼륨 이름은 app-images로 설정)
docker volume create --name app-images

# Docker 볼륨 마운트
docker run -d --rm \
  -v app-images:$IMAGE_DIR \
  wlsdud0/ec_mall-boot:latest echo "Volume mounted successfully!"

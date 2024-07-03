#!/usr/bin/env bash

# 새 버전을 가져온다.
docker pull wlsdud0/ec_mall-boot:latest

# 포트 설정을 진행한다.
# 다음에 실행할 포트를 나타내는 변수를 만들고
next_run=8081
# 이전에 실행한 포트 정보를 파일에서 회수한다.
last_run=$(cat "$HOME/last_run")

# 이전에 실행한 정보를 찾을 수 없는 경우
if [ $? -ne 0 ]; then
  echo "First Run"
  last_run=-1

# 8081에 실행했었던 경우
elif [ $last_run -eq 8081 ]; then
  echo "Before Run: 8081"
  next_run=8082

# 8082에 실행했었던 경우
elif [ $last_run -eq 8082 ]; then
  echo "Before Run 8082"
  next_run=8081
fi

# nest_run 기록
echo "$next_run" > "$HOME/last_run"

# 이미지 저장 디렉토리 설정 (변경 가능)
IMAGE_DIR="/home/ubuntu/images"

# next_run 포트에 새 버전을 실행한다. (볼륨 마운트 추가)
docker run --rm -d -p "$next_run:8080" --name "ec_mall-app-$next_run" \
  -v "$IMAGE_DIR:/app/images" \  # 이미지 저장 디렉토리 마운트
  wlsdud0/ec_mall-boot:latest

# 이전 설정이 있는 경우 지운다음
if [ $last_run -ne -1 ]; then
  sudo rm "/etc/nginx/sites-enabled/app_$last_run"
fi

# 새로운 설정을 적용한다.
sudo ln -s "/etc/nginx/sites-available/app_$next_run" "/etc/nginx/sites-enabled/app_$next_run"
sudo systemctl restart nginx.service

# 이전 애플리케이션을 종료한다.
if [ $last_run -ne -1 ]; then
  docker stop "ec_mall-app-$last_run"
fi

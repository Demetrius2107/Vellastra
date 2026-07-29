#!/bin/bash
set -e
SERVICE="auth"
PORT="8081"

case "${1:-up}" in
  build) docker compose build && echo "✅ $SERVICE 镜像构建完成" ;;
  up)    docker compose up -d --build && echo "✅ $SERVICE 已启动 → http://localhost:$PORT" ;;
  down)  docker compose down -v && echo "🛑 $SERVICE 已停止" ;;
  logs)  docker compose logs -f ;;
  ps)    docker compose ps ;;
  *) echo "用法: $0 {build|up|down|logs|ps}" ;;
esac

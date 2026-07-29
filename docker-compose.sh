#!/bin/bash
set -e

# ============================================================
# Vellastra Docker 一键部署脚本
# ============================================================
# 用法:
#   ./docker-compose.sh build    — 构建所有服务镜像
#   ./docker-compose.sh up       — 启动全部服务
#   ./docker-compose.sh down     — 停止并移除全部容器
#   ./docker-compose.sh restart  — 重启全部服务
#   ./docker-compose.sh logs     — 查看所有服务日志
# ============================================================

COLOR_GREEN='\033[0;32m'
COLOR_YELLOW='\033[1;33m'
COLOR_RED='\033[0;31m'
COLOR_CYAN='\033[0;36m'
COLOR_RESET='\033[0m'

log_info()  { echo -e "${COLOR_GREEN}[INFO]${COLOR_RESET} $1"; }
log_warn()  { echo -e "${COLOR_YELLOW}[WARN]${COLOR_RESET} $1"; }
log_error() { echo -e "${COLOR_RED}[ERROR]${COLOR_RESET} $1"; }
log_step()  { echo -e "\n${COLOR_CYAN}════════════════════════════════════════════${COLOR_RESET}"; }
log_title() { echo -e "${COLOR_CYAN}  $1${COLOR_RESET}"; }
log_step

case "${1:-up}" in
  build)
    log_title "🏗️  构建所有服务镜像"
    log_step
    log_info "开始构建，首次构建约 5-10 分钟..."
    docker compose build --parallel
    log_info "✅ 所有服务镜像构建完成"
    ;;

  up)
    log_title "🚀 启动全部服务"
    log_step
    log_info "创建网络和数据卷..."
    log_info "启动 MySQL + Redis + 8 个微服务..."
    docker compose up -d --build
    log_step
    log_info "✅ 全部服务已启动！"
    echo ""
    echo -e "  服务列表:"
    echo -e "  ┌─────────────┬──────────┬────────────────────────────┐"
    echo -e "  │ 服务名称    │ 端口     │ 地址                       │"
    echo -e "  ├─────────────┼──────────┼────────────────────────────┤"
    echo -e "  │ MySQL       │ 3306     │ localhost:3306             │"
    echo -e "  │ Redis       │ 6379     │ localhost:6379             │"
    echo -e "  │ Gateway     │ 8080     │ http://localhost:8080      │"
    echo -e "  │ Auth        │ 8081     │ http://localhost:8081      │"
    echo -e "  │ User        │ 8082     │ http://localhost:8082      │"
    echo -e "  │ Article     │ 8083     │ http://localhost:8083      │"
    echo -e "  │ Category    │ 8084     │ http://localhost:8084      │"
    echo -e "  │ Comment     │ 8085     │ http://localhost:8085      │"
    echo -e "  │ File        │ 8086     │ http://localhost:8086      │"
    echo -e "  │ Tag         │ 8087     │ http://localhost:8087      │"
    echo -e "  └─────────────┴──────────┴────────────────────────────┘"
    echo ""
    echo -e "  📖 接口文档: http://localhost:8080/doc.html"
    echo -e "  📝 查看日志: ./docker-compose.sh logs"
    ;;

  down)
    log_title "🛑 停止并移除全部容器"
    docker compose down -v
    log_info "✅ 所有容器已停止"
    ;;

  restart)
    log_title "🔄 重启全部服务"
    docker compose down
    docker compose up -d --build
    log_info "✅ 全部服务已重启"
    ;;

  logs)
    log_title "📋 查看所有服务日志"
    docker compose logs -f
    ;;

  status)
    log_title "📊 服务状态"
    docker compose ps
    ;;

  *)
    echo "用法: $0 {build|up|down|restart|logs|status}"
    echo ""
    echo "   build    — 构建所有服务镜像"
    echo "   up       — 启动全部服务（默认）"
    echo "   down     — 停止并移除全部容器"
    echo "   restart  — 重启全部服务"
    echo "   logs     — 查看所有服务日志"
    echo "   status   — 查看服务运行状态"
    exit 1
    ;;
esac

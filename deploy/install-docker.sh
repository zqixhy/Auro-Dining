#!/bin/bash

# EC2 Docker environment setup
# Run once on EC2 to install Docker and Docker Compose

set -e

echo "=========================================="
echo "Auro Dining - Docker environment setup"
echo "=========================================="

echo "[1/5] Updating system..."
sudo yum update -y

echo "[2/5] Installing Docker..."
sudo yum install -y docker
sudo systemctl enable docker
sudo systemctl start docker
sudo systemctl status docker --no-pager

echo "[3/5] Adding user to docker group..."
sudo usermod -aG docker "$(whoami)"

echo "[4/5] Installing Docker Compose..."
# Install Docker Compose plugin (v2+) - 插件形式
sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

# 验证插件形式是否工作
if sudo docker compose version >/dev/null 2>&1; then
  echo "✓ Docker Compose 插件安装成功"
  sudo docker compose version
else
  echo "⚠ 插件形式不可用，安装独立二进制作为备用..."
  # 安装独立二进制（备用）
  sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
  sudo chmod +x /usr/local/bin/docker-compose
  sudo docker-compose --version
fi

# 确保两种形式都可用（向后兼容）
if [ ! -f /usr/local/bin/docker-compose ] && [ -f /usr/local/lib/docker/cli-plugins/docker-compose ]; then
  sudo ln -sf /usr/local/lib/docker/cli-plugins/docker-compose /usr/local/bin/docker-compose
fi

echo "[4.5/5] Setting up Docker Buildx..."
sudo docker buildx install || echo "Buildx already installed or not needed"
sudo docker buildx version || echo "Buildx check completed"

echo "[5/5] Creating app directories..."
mkdir -p ~/auro-dining
mkdir -p ~/auro-dining/backups
mkdir -p ~/Pictures/auro_dining_images
mkdir -p ~/auro-dining/logs
chmod 755 ~/Pictures/auro_dining_images
chmod 755 ~/auro-dining/logs

echo "=========================================="
echo "Docker setup completed."
echo "=========================================="
echo ""
echo "You may need to log out and back in for group changes."
echo "Next: deploy app (JAR, Dockerfile, docker-compose, config) then:"
echo "  cd ~/auro-dining && docker compose build && docker compose up -d"
echo ""

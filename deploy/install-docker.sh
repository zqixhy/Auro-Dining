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
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
docker-compose --version

echo "[5/5] Creating app directory..."
mkdir -p ~/auro-dining
mkdir -p ~/auro-dining/backups

echo "=========================================="
echo "Docker setup completed."
echo "=========================================="
echo ""
echo "You may need to log out and back in for group changes."
echo "Next: deploy app (JAR, Dockerfile, docker-compose, config) then:"
echo "  cd ~/auro-dining && docker compose build && docker compose up -d"
echo ""

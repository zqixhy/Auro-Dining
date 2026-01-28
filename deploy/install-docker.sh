#!/bin/bash
set -e

echo "=== Auro Dining - Docker Environment Repair & Setup ==="

# 1. System Update and Docker Installation
sudo dnf update -y
sudo dnf install -y docker
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker ec2-user

# 2. Cleanup to prevent architecture mismatches
echo "Purging old plugin caches..."
sudo rm -rf /usr/local/lib/docker/cli-plugins/docker-buildx
sudo rm -rf /usr/lib/docker/cli-plugins/docker-buildx
rm -rf ~/.docker/cli-plugins/docker-buildx

# 3. Targeted Buildx Install (Ensures x86_64 version 0.17.1)
echo "Installing Docker Buildx (v0.17.1 x86_64)..."
BUILDX_URL="https://github.com/docker/buildx/releases/download/v0.17.1/buildx-v0.17.1.linux-amd64"
sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -L "$BUILDX_URL" -o /usr/local/lib/docker/cli-plugins/docker-buildx
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-buildx

# 4. Install Docker Compose V2
echo "Installing Docker Compose..."
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 5. Verification
echo "=== Environment Verification ==="
docker version --format 'Engine: {{.Server.Version}}'
docker buildx version
/usr/local/bin/docker-compose version

echo "Creating log directories..."
mkdir -p ~/auro-dining/backups ~/auro-dining/logs
chmod 755 ~/auro-dining/logs
echo "=== Setup Success ==="
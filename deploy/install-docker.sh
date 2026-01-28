#!/bin/bash
set -e

echo "=== Aura Dining - Docker Environment Repair & Setup ==="

# 1. Basic Docker Installation
sudo dnf update -y
sudo dnf install -y docker
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker ec2-user

# 2. Clean up old or corrupted Buildx plugins to prevent "exec format error"
echo "Cleaning up corrupted plugins..."
sudo rm -rf /usr/local/lib/docker/cli-plugins/docker-buildx
sudo rm -rf /usr/lib/docker/cli-plugins/docker-buildx
rm -rf ~/.docker/cli-plugins/docker-buildx

# 3. Install correct Buildx (Ensuring version >= 0.17.0 and architecture matches x86_64)
echo "Installing Docker Buildx (v0.17.1 x86_64)..."
# Direct download link for the verified x86_64 binary
BUILDX_URL="https://github.com/docker/buildx/releases/download/v0.17.1/buildx-v0.17.1.linux-amd64"

sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -L "$BUILDX_URL" -o /usr/local/lib/docker/cli-plugins/docker-buildx
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-buildx

# 4. Initialize Buildx
echo "Initializing Buildx..."
# Verify that the plugin is recognized by Docker
sudo docker buildx version || { echo "ERROR: Buildx still not recognized"; exit 1; }

# 5. Install Docker Compose V2
echo "Installing Docker Compose..."
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 6. Final verification
echo "=== Verification ==="
docker version --format 'Docker Engine: {{.Server.Version}}'
docker buildx version
/usr/local/bin/docker-compose version

# 7. Directory Preparation
mkdir -p ~/auro-dining/backups ~/auro-dining/logs
chmod 755 ~/auro-dining/logs

echo "=== Setup Successful ==="
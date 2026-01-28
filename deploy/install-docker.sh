#!/bin/bash
set -e

echo "=== Auro Dining - Docker environment setup ==="

# Update system and install Docker
sudo dnf update -y
sudo dnf install -y docker
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker ec2-user

# Install Docker Buildx (version 0.17.0 or later required)
echo "Installing Docker Buildx..."
DOCKER_CONFIG=${DOCKER_CONFIG:-$HOME/.docker}
mkdir -p $DOCKER_CONFIG/cli-plugins

# Get latest Buildx release (ensure version >= 0.17.0)
BUILDX_URL=$(curl -s https://api.github.com/repos/docker/buildx/releases/latest | grep "browser_download_url.*linux-x86_64" | cut -d '"' -f 4)
if [ -z "$BUILDX_URL" ]; then
  echo "Failed to get Buildx URL, using direct download..."
  BUILDX_URL="https://github.com/docker/buildx/releases/latest/download/buildx-v0.17.0.linux-amd64"
fi

echo "Downloading Buildx from: $BUILDX_URL"
curl -L "$BUILDX_URL" -o $DOCKER_CONFIG/cli-plugins/docker-buildx
chmod +x $DOCKER_CONFIG/cli-plugins/docker-buildx

# Remove old Buildx if exists and install to system-wide location
sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo rm -f /usr/local/lib/docker/cli-plugins/docker-buildx
sudo cp $DOCKER_CONFIG/cli-plugins/docker-buildx /usr/local/lib/docker/cli-plugins/docker-buildx
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-buildx

# Verify Buildx version
echo "Verifying Buildx version..."
BUILDX_VERSION=$(sudo docker buildx version 2>/dev/null | grep -oP 'github.com/docker/buildx \K[0-9.]+' || echo "0.0.0")
echo "Installed Buildx version: $BUILDX_VERSION"

# Create and use default builder instance
sudo docker buildx create --name default --use 2>/dev/null || sudo docker buildx use default 2>/dev/null || true
sudo docker buildx inspect --bootstrap || true

# Installing Docker Compose V2
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installation
echo "Verifying installations..."
docker version --format 'Docker Version: {{.Server.Version}}'
echo "Buildx version:"
sudo docker buildx version || docker buildx version
echo "Docker Compose version:"
/usr/local/bin/docker-compose version || docker compose version

echo "Creating application directories..."
mkdir -p ~/auro-dining/backups
mkdir -p ~/auro-dining/logs
chmod 755 ~/auro-dining/logs

echo "=========================================="
echo "Setup completed successfully!"
echo "=========================================="
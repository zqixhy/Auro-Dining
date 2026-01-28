#!/bin/bash
set -e

echo "=== Auro Dining - Docker environment setup ==="

# 1. Update system and install Docker
sudo dnf update -y
sudo dnf install -y docker
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker ec2-user

# 2. Install Docker Compose binary to standard path
# Use specific 2.x version for stability
DOCKER_CONFIG=${DOCKER_CONFIG:-$HOME/.docker}
mkdir -p $DOCKER_CONFIG/cli-plugins
curl -SL https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 -o $DOCKER_CONFIG/cli-plugins/docker-compose
chmod +x $DOCKER_CONFIG/cli-plugins/docker-compose

# Key: Also install to /usr/local/bin to fix sudo path issues
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 3. Verify installation
echo "Checking installations..."
/usr/local/bin/docker-compose version || echo "Binary link failed"
docker compose version || echo "Plugin failed"

# 4. Create necessary directories
mkdir -p ~/auro-dining/backups ~/auro-dining/logs
#!/bin/bash

# Rollback script for Docker deployment
# Usage: ./rollback-docker.sh [backup_file_name]

set -e

APP_DIR=~/auro-dining
BACKUP_DIR=$APP_DIR/backups

echo "=========================================="
echo "Auro Dining - Docker rollback"
echo "=========================================="

echo "Available backups:"
ls -lt "$BACKUP_DIR"/*.jar 2>/dev/null | head -10 || {
  echo "No backups found."
  exit 1
}

if [ -n "$1" ]; then
  BACKUP_FILE=$BACKUP_DIR/$1
  [ -f "$BACKUP_FILE" ] || { echo "Backup not found: $BACKUP_FILE"; exit 1; }
else
  BACKUP_FILE=$(ls -t "$BACKUP_DIR"/*.jar | head -1)
  echo "Using latest: $(basename "$BACKUP_FILE")"
fi

echo ""
echo "Roll back to: $(basename "$BACKUP_FILE")?"
read -p "Continue? (y/N): " -n 1 -r
echo ""
[[ $REPLY =~ ^[Yy]$ ]] || { echo "Cancelled."; exit 0; }

cd "$APP_DIR"
sudo docker-compose down
cp "$BACKUP_FILE" auro-dining-0.0.1-SNAPSHOT.jar
sudo docker-compose build --no-cache
sudo docker-compose up -d
sleep 5
sudo docker-compose ps
echo "Rollback done."

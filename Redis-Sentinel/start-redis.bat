@echo off
:: 确保脚本在当前文件夹运行
cd /d "%~dp0"
title Redis HA Cluster Orchestrator

echo Starting Redis Master (6379)...
start "Master-6379" redis-server.exe master.conf

echo Starting Redis Replica (6380)...
start "Replica-6380" redis-server.exe slave.conf

echo Starting Redis Sentinels...
start "Sentinel-26379" redis-server.exe sentinel_26379.conf --sentinel
start "Sentinel-26380" redis-server.exe sentinel_26380.conf --sentinel
start "Sentinel-26381" redis-server.exe sentinel_26381.conf --sentinel

echo.
echo ======================================================
echo   Auro-Dining: Redis HA Cluster is UP
echo ======================================================
pause
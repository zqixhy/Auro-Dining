@echo off
title Redis HA Cluster Debugger
color 0B

echo [STARTING] Redis Master (6379)...
start "Master-6379" cmd /k "redis-server.exe master.conf"
timeout /t 3

echo [STARTING] Redis Replica (6380)...
start "Replica-6380" cmd /k "redis-server.exe slave.conf"
timeout /t 3

echo [STARTING] Sentinel A (26379)...
start "Sentinel-26379" cmd /k "redis-server.exe --sentinel sentinel_26379.conf"
timeout /t 2

echo [STARTING] Sentinel B (26380)...
start "Sentinel-26380" cmd /k "redis-server.exe --sentinel sentinel_26380.conf"
timeout /t 2

echo [STARTING] Sentinel C (26381)...
start "Sentinel-26381" cmd /k "redis-server.exe --sentinel sentinel_26381.conf"

echo ==========================================
echo Check if 5 windows are open now.
echo If a window is blank or shows an error, read the message.
echo ==========================================
pause
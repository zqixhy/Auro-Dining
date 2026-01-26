@echo off
echo Shutting down all Redis and Sentinel instances...
taskkill /f /im redis-server.exe
echo Done.
pause
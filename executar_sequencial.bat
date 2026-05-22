@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo === Busca sem paralelismo ===
echo.
java -cp "busca sem paralelismo\out" Main
echo.
pause

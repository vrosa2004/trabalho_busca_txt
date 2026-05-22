@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo === Busca com Paralelismo ===
echo.
java -cp "busca com paralelismo\out" org.example.Main
echo.
pause

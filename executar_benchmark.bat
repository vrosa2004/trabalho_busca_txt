@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo === Benchmark ===
echo.
java -cp "benchmark\out" Benchmark
echo.
pause

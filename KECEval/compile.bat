@echo off
echo ========================================================
echo Compiling KEC Evaluation System (Regulation 2024)...
echo ========================================================
javac -cp ".;mysql-connector-j-8.4.0.jar" *.java
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Compilation completed without errors.
) else (
    echo [ERROR] Compilation failed.
)
pause


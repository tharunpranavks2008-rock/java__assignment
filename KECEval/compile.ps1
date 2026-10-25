# PowerShell compilation script
Write-Host "Compiling KEC Evaluation System (Regulation 2024)..." -ForegroundColor Cyan
javac -cp ".;mysql-connector-j-8.4.0.jar" *.java
if ($LASTEXITCODE -eq 0) {
    Write-Host "[SUCCESS] All Java source files compiled successfully!" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Compilation failed." -ForegroundColor Red
}


# Script tu dong kiem tra JDK 21, tai Gradle Wrapper va build file .jar cho Windows 11
$ErrorActionPreference = "Stop"

Write-Host "====================================================" -ForegroundColor Cyan
Write-Host "  FAKE SPAWNER MOD (KINGMC / KINGSMP) - BUILD TOOL  " -ForegroundColor Cyan
Write-Host "  Minecraft Java Edition 1.21.11 (Fabric)           " -ForegroundColor Cyan
Write-Host "====================================================" -ForegroundColor Cyan

$wrapperJar = Join-Path $PSScriptRoot "gradle\wrapper\gradle-wrapper.jar"
if (-not (Test-Path $wrapperJar)) {
    Write-Host "[1/3] Dang tai gradle-wrapper.jar..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Force -Path (Split-Path $wrapperJar) | Out-Null
    Invoke-WebRequest -Uri "https://raw.githubusercontent.com/gradle/gradle/v8.12.1/gradle/wrapper/gradle-wrapper.jar" -OutFile $wrapperJar
    Write-Host "      Da tai xong gradle-wrapper.jar!" -ForegroundColor Green
} else {
    Write-Host "[1/3] Da co san gradle-wrapper.jar." -ForegroundColor Green
}

Write-Host "[2/3] Dang kiem tra phien ban Java (yeu cau JDK 21+)..." -ForegroundColor Yellow
& java -version

Write-Host "[3/3] Dang bien dich du an sang file .jar..." -ForegroundColor Yellow
& "$PSScriptRoot\gradlew.bat" build --no-daemon

Write-Host ""
Write-Host "====================================================" -ForegroundColor Green
Write-Host "  HOAN TAT! File mod .jar nam tai:                  " -ForegroundColor Green
Write-Host "  build\libs\fake-spawner-mod-1.0.0.jar            " -ForegroundColor Yellow
Write-Host "====================================================" -ForegroundColor Green

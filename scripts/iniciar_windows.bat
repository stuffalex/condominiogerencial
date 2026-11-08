@echo off
rem ==============================================================================
rem Residencial Anhanduí - Sistema Portátil de Gestão Condominial
rem Script de Inicialização para Windows (Pendrive / Localhost)
rem ==============================================================================

cd /d "%~dp0\.."

echo ====================================================================
echo     Iniciando Residencial Anhandui - Gestao Condominial
echo ====================================================================

if not exist "data\documentos" mkdir "data\documentos"

java -version >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Java nao foi encontrado neste computador.
    echo Por favor, instale o Java 17 ou superior para executar o sistema.
    pause
    exit /b 1
)

set JAR_FILE=backend\target\anhandui-backend-1.0.0-SNAPSHOT.jar

if not exist "%JAR_FILE%" (
    echo [AVISO] Compilando JAR executavel...
    cd backend
    call mvn clean package -DskipTests
    cd ..
)

echo [INFO] Iniciando aplicacao...
start http://localhost:8080
java -jar "%JAR_FILE%"
pause

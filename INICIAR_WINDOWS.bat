@echo off
rem ==============================================================================
rem Residencial Anhanduí - Executável de 2 Cliques para Windows (Pendrive / Localhost)
rem ==============================================================================

cd /d "%~dp0"

rem Cria diretório de documentos se não existir
if not exist "data\documentos" mkdir "data\documentos"

rem Carrega variáveis de ambiente do .env se existir
if exist ".env" (
    for /f "usebackq tokens=1,2 delims==" %%A in (".env") do (
        set "%%A=%%B"
    )
)

if "%PORT%"=="" set PORT=8080
if "%CONDO_DB_PATH%"=="" set CONDO_DB_PATH=data/condominio.db

rem Verifica Java
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

echo ====================================================================
echo     Iniciando Residencial Anhandui - Gestao Condominial
echo     Acesse no navegador: http://localhost:%PORT%
echo ====================================================================

start http://localhost:%PORT%
java -jar "%JAR_FILE%"
pause

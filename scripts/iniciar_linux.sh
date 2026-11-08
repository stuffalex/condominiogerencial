#!/bin/bash
# ==============================================================================
# Residencial Anhanduí - Sistema Portátil de Gestão Condominial
# Script de Inicialização para Linux (Pendrive / Localhost)
# ==============================================================================

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$DIR"

echo "===================================================================="
echo "    Iniciando Residencial Anhanduí - Gestão Condominial"
echo "===================================================================="

# Cria pastas de dados caso não existam
mkdir -p data/documentos

# Verifica se o Java está instalado
if ! command -v java &> /dev/null; then
    echo "[ERRO] Java não foi encontrado no sistema."
    echo "Instale o Java 17+ (ex: sudo apt install openjdk-17-jre) para executar."
    read -p "Pressione ENTER para sair..."
    exit 1
fi

JAR_FILE="backend/target/anhandui-backend-1.0.0-SNAPSHOT.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "[AVISO] Arquivo JAR não encontrado. Compilando o projeto com Maven..."
    (cd backend && mvn clean package -DskipTests)
fi

echo "[INFO] Iniciando servidor local na porta 8080..."
echo "[INFO] O banco de dados SQLite será mantido em: data/condominio.db"

# Abre o navegador automaticamente após 3 segundos
(sleep 3 && (xdg-open "http://localhost:8080" || sensible-browser "http://localhost:8080" || echo "Acesse: http://localhost:8080")) &

# Executa o Spring Boot
java -jar "$JAR_FILE"

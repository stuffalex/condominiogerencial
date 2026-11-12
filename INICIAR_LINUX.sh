#!/usr/bin/env bash
# ==============================================================================
# Residencial Anhanduí - Executável de 2 Cliques para Linux
# ==============================================================================

# Entra na pasta do script (raiz do projeto/pendrive)
cd "$(dirname "$0")" || exit 1

# Carrega variáveis do arquivo .env se existir
if [ -f .env ]; then
    set -a
    source <(grep -v '^#' .env | sed -e 's/\r$//')
    set +a
fi

# Garante a existência dos diretórios locais
mkdir -p "${CONDO_STORAGE_PATH:-data/documentos}"
mkdir -p "data/backups"

# Verifica se o Java está disponível
if ! command -v java &> /dev/null; then
    if command -v zenity &> /dev/null; then
        zenity --error --text="Java não encontrado. Instale o OpenJDK 17+ para rodar o sistema."
    else
        echo "[ERRO] Java não foi encontrado no sistema."
        read -p "Pressione ENTER para sair..."
    fi
    exit 1
fi

JAR_FILE="backend/target/anhandui-backend-1.0.0-SNAPSHOT.jar"

# Se o JAR ainda não existir, compila
if [ ! -f "$JAR_FILE" ]; then
    echo "[AVISO] JAR inicial não encontrado. Compilando pacote portátil..."
    (cd backend && mvn clean package -DskipTests)
fi

PORT="${PORT:-8080}"
URL="http://localhost:${PORT}"

echo "===================================================================="
echo "    Iniciando Residencial Anhanduí - Gestão Condominial"
echo "    Acesso Web: $URL"
echo "    Banco SQLite: ${CONDO_DB_PATH:-data/condominio.db}"
echo "===================================================================="

# Dispara o navegador padrão após 2 segundos
(sleep 2 && (xdg-open "$URL" || sensible-browser "$URL" || x-www-browser "$URL" || true)) &

# Inicia o servidor Java
java -jar "$JAR_FILE"

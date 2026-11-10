package com.residencial.anhandui.infrastructure.adapters.outbound.backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GerenciadorBackupService {

    private static final Logger log = LoggerFactory.getLogger(GerenciadorBackupService.class);
    private static final int MAX_BACKUPS = 3;
    private static final Path PASTA_DATA = Paths.get("data");
    private static final Path PASTA_BACKUPS = Paths.get("data/backups");
    private static final Path ARQUIVO_BANCO = Paths.get("data/condominio.db");

    public static class InfoBackup {
        private String nomeArquivo;
        private long tamanhoBytes;
        private String dataCriacao;

        public InfoBackup(String nomeArquivo, long tamanhoBytes, String dataCriacao) {
            this.nomeArquivo = nomeArquivo;
            this.tamanhoBytes = tamanhoBytes;
            this.dataCriacao = dataCriacao;
        }

        public String getNomeArquivo() {
            return nomeArquivo;
        }

        public long getTamanhoBytes() {
            return tamanhoBytes;
        }

        public String getDataCriacao() {
            return dataCriacao;
        }
    }

    /**
     * Executa o backup inicial ao ligar o sistema.
     * Chamado estaticamente antes de inicializar o Spring Boot ou via serviço.
     */
    public static synchronized void realizarBackupRotativo() {
        try {
            if (!Files.exists(PASTA_BACKUPS)) {
                Files.createDirectories(PASTA_BACKUPS);
            }

            if (!Files.exists(ARQUIVO_BANCO) || Files.size(ARQUIVO_BANCO) == 0) {
                log.info("Banco SQLite condominio.db ainda não existe ou está vazio. Backup não requerido no momento.");
                return;
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            Path destino = PASTA_BACKUPS.resolve("condominio-backup-" + timestamp + ".db");

            Files.copy(ARQUIVO_BANCO, destino, StandardCopyOption.REPLACE_EXISTING);
            log.info("Backup rotativo do banco realizado com sucesso: {}", destino);

            expurgarBackupsAntigos();
        } catch (Exception e) {
            log.error("Erro ao realizar backup rotativo do banco: {}", e.getMessage(), e);
        }
    }

    private static synchronized void expurgarBackupsAntigos() {
        try (Stream<Path> stream = Files.list(PASTA_BACKUPS)) {
            List<Path> backups = stream
                    .filter(p -> p.getFileName().toString().startsWith("condominio-backup-") && p.getFileName().toString().endsWith(".db"))
                    .sorted(Comparator.comparingLong((Path p) -> {
                        try {
                            return Files.getLastModifiedTime(p).toMillis();
                        } catch (IOException e) {
                            return 0L;
                        }
                    }).reversed())
                    .collect(Collectors.toList());

            if (backups.size() > MAX_BACKUPS) {
                for (int i = MAX_BACKUPS; i < backups.size(); i++) {
                    Path antigo = backups.get(i);
                    Files.deleteIfExists(antigo);
                    log.info("Backup antigo removido conforme política de retenção (máx {}): {}", MAX_BACKUPS, antigo.getFileName());
                }
            }
        } catch (IOException e) {
            log.error("Erro ao expurgar backups antigos: {}", e.getMessage());
        }
    }

    public List<InfoBackup> listarBackups() {
        List<InfoBackup> lista = new ArrayList<>();
        if (!Files.exists(PASTA_BACKUPS)) {
            return lista;
        }

        try (Stream<Path> stream = Files.list(PASTA_BACKUPS)) {
            List<Path> backups = stream
                    .filter(p -> p.getFileName().toString().startsWith("condominio-backup-") && p.getFileName().toString().endsWith(".db"))
                    .sorted(Comparator.comparingLong((Path p) -> {
                        try {
                            return Files.getLastModifiedTime(p).toMillis();
                        } catch (IOException e) {
                            return 0L;
                        }
                    }).reversed())
                    .collect(Collectors.toList());

            for (Path p : backups) {
                long tamanho = Files.size(p);
                String data = Files.getLastModifiedTime(p).toString();
                lista.add(new InfoBackup(p.getFileName().toString(), tamanho, data));
            }
        } catch (IOException e) {
            log.error("Erro ao listar backups: {}", e.getMessage());
        }
        return lista;
    }

    public synchronized boolean restaurarBackup(String nomeArquivo) {
        try {
            Path arquivoOrigem = PASTA_BACKUPS.resolve(nomeArquivo);
            if (!Files.exists(arquivoOrigem)) {
                log.warn("Arquivo de backup solicitado para restauração não existe: {}", nomeArquivo);
                return false;
            }

            // Cria um backup emergencial do banco atual antes de sobrescrever
            if (Files.exists(ARQUIVO_BANCO)) {
                Path salvaguarda = PASTA_BACKUPS.resolve("condominio-pre-rollback.db");
                Files.copy(ARQUIVO_BANCO, salvaguarda, StandardCopyOption.REPLACE_EXISTING);
            }

            Files.copy(arquivoOrigem, ARQUIVO_BANCO, StandardCopyOption.REPLACE_EXISTING);
            log.info("Rollback executado com sucesso a partir de {}", nomeArquivo);
            return true;
        } catch (IOException e) {
            log.error("Falha ao restaurar backup {}: {}", nomeArquivo, e.getMessage(), e);
            return false;
        }
    }
}

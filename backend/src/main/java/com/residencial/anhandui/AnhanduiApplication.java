package com.residencial.anhandui;

import com.residencial.anhandui.infrastructure.adapters.outbound.backup.GerenciadorBackupService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class AnhanduiApplication {

    public static void main(String[] args) {
        garantirDiretoriosLocais();
        GerenciadorBackupService.realizarBackupRotativo();
        SpringApplication.run(AnhanduiApplication.class, args);
    }

    private static void garantirDiretoriosLocais() {
        try {
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            Path docsDir = Paths.get("data/documentos");
            if (!Files.exists(docsDir)) {
                Files.createDirectories(docsDir);
            }
            Path backupDir = Paths.get("data/backups");
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
            }
        } catch (IOException e) {
            System.err.println("Aviso: Não foi possível criar diretórios em data/: " + e.getMessage());
        }
    }
}


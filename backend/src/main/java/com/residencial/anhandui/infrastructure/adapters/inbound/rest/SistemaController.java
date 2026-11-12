package com.residencial.anhandui.infrastructure.adapters.inbound.rest;

import com.residencial.anhandui.infrastructure.adapters.outbound.backup.GerenciadorBackupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sistema")
@CrossOrigin(origins = "*")
public class SistemaController {

    private final GerenciadorBackupService backupService;

    public SistemaController(GerenciadorBackupService backupService) {
        this.backupService = backupService;
    }

    @GetMapping("/backups")
    public ResponseEntity<List<GerenciadorBackupService.InfoBackup>> listarBackups() {
        return ResponseEntity.ok(backupService.listarBackups());
    }

    @PostMapping("/backups/executar")
    public ResponseEntity<Map<String, Object>> dispararBackupManual() {
        GerenciadorBackupService.realizarBackupRotativo();
        return ResponseEntity.ok(Map.of(
                "status", "sucesso",
                "mensagem", "Snapshot do banco de dados salvo na pasta data/backups/."
        ));
    }

    @PostMapping("/backups/restaurar")
    public ResponseEntity<Map<String, Object>> restaurar(@RequestParam String nomeArquivo) {
        boolean sucesso = backupService.restaurarBackup(nomeArquivo);
        if (sucesso) {
            return ResponseEntity.ok(Map.of(
                    "status", "sucesso",
                    "mensagem", "Rollback concluído com sucesso a partir de " + nomeArquivo + ". Reinicie o sistema para aplicar totalmente."
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "erro",
                    "mensagem", "Não foi possível restaurar o backup especificado."
            ));
        }
    }
}

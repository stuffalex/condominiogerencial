package com.residencial.anhandui.infrastructure.adapters.outbound.storage;

import com.residencial.anhandui.domain.ports.output.IDocumentoStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalFileStorageAdapter implements IDocumentoStoragePort {

    private final Path rootLocation;

    public LocalFileStorageAdapter(@Value("${anhandui.storage.path:data/documentos}") String storagePath) {
        this.rootLocation = Paths.get(storagePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o diretório de armazenamento de documentos em: " + this.rootLocation, e);
        }
    }

    @Override
    public String salvarArquivo(String nomeOriginal, byte[] conteudo) {
        String extensao = "";
        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        }
        String nomeUnico = UUID.randomUUID() + extensao;
        Path destino = this.rootLocation.resolve(nomeUnico);
        try {
            Files.write(destino, conteudo);
            return nomeUnico;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar arquivo em disco: " + nomeOriginal, e);
        }
    }

    @Override
    public byte[] lerArquivo(String caminhoRelativo) {
        Path arquivo = this.rootLocation.resolve(caminhoRelativo).normalize();
        try {
            if (!Files.exists(arquivo)) {
                throw new RuntimeException("Arquivo não encontrado em: " + arquivo);
            }
            return Files.readAllBytes(arquivo);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao ler arquivo do disco: " + caminhoRelativo, e);
        }
    }
}

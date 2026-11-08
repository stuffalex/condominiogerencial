package com.residencial.anhandui.domain.ports.output;

public interface IDocumentoStoragePort {
    String salvarArquivo(String nomeOriginal, byte[] conteudo);
    byte[] lerArquivo(String caminhoRelativo);
}

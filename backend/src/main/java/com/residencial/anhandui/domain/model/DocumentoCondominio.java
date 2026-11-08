package com.residencial.anhandui.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class DocumentoCondominio implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String titulo;
    private CategoriaDocumento categoria;
    private String nomeArquivo;
    private String caminhoArquivo;
    private String localizacaoFisica; // Ex: "Pasta Azul 01 / Gaveta do Síndico / Envelope Julho 2024"
    private LocalDateTime dataUpload;

    public DocumentoCondominio() {
    }

    public DocumentoCondominio(UUID id, String titulo, CategoriaDocumento categoria, String nomeArquivo, String caminhoArquivo, String localizacaoFisica, LocalDateTime dataUpload) {
        this.id = id != null ? id : UUID.randomUUID();
        this.titulo = titulo;
        this.categoria = categoria != null ? categoria : CategoriaDocumento.OUTRO;
        this.nomeArquivo = nomeArquivo;
        this.caminhoArquivo = caminhoArquivo;
        this.localizacaoFisica = localizacaoFisica != null ? localizacaoFisica : "Arquivo Físico a Classificar";
        this.dataUpload = dataUpload != null ? dataUpload : LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public CategoriaDocumento getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDocumento categoria) {
        this.categoria = categoria;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public String getLocalizacaoFisica() {
        return localizacaoFisica;
    }

    public void setLocalizacaoFisica(String localizacaoFisica) {
        this.localizacaoFisica = localizacaoFisica;
    }

    public LocalDateTime getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(LocalDateTime dataUpload) {
        this.dataUpload = dataUpload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentoCondominio that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

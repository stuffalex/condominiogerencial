package com.residencial.anhandui.domain.model;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class DeclaracaoNegativaDebito implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String codigoAutenticidade;
    private UUID apartamentoId;
    private BlocoEnum bloco;
    private String numeroApartamento;
    private String nomeTitular;
    private LocalDate dataEmissao;
    private LocalDate validadeAte;
    private String observacao;

    public DeclaracaoNegativaDebito() {
    }

    public DeclaracaoNegativaDebito(UUID id, UUID apartamentoId, BlocoEnum bloco, String numeroApartamento, String nomeTitular, LocalDate dataEmissao, LocalDate validadeAte, String observacao) {
        this.id = id != null ? id : UUID.randomUUID();
        this.apartamentoId = apartamentoId;
        this.bloco = bloco;
        this.numeroApartamento = numeroApartamento;
        this.nomeTitular = nomeTitular;
        this.dataEmissao = dataEmissao != null ? dataEmissao : LocalDate.now();
        this.validadeAte = validadeAte != null ? validadeAte : this.dataEmissao.plusDays(30);
        this.observacao = observacao;
        this.codigoAutenticidade = gerarHashAutenticidade();
    }

    private String gerarHashAutenticidade() {
        try {
            String raw = String.format("%s-%s-%s-%s-%s",
                    bloco != null ? bloco.name() : "BLOCO",
                    numeroApartamento != null ? numeroApartamento : "000",
                    nomeTitular != null ? nomeTitular : "TITULAR",
                    dataEmissao != null ? dataEmissao.format(DateTimeFormatter.ISO_LOCAL_DATE) : "DATA",
                    id != null ? id.toString() : UUID.randomUUID().toString()
            );
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            // Retorna formato amigável para conferência física (ex: ANH-A1B2-C3D4-E5F6)
            String hex = hexString.toString().toUpperCase();
            return String.format("ANH-%s-%s-%s", hex.substring(0, 4), hex.substring(4, 8), hex.substring(8, 12));
        } catch (NoSuchAlgorithmException e) {
            return "ANH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCodigoAutenticidade() {
        return codigoAutenticidade;
    }

    public void setCodigoAutenticidade(String codigoAutenticidade) {
        this.codigoAutenticidade = codigoAutenticidade;
    }

    public UUID getApartamentoId() {
        return apartamentoId;
    }

    public void setApartamentoId(UUID apartamentoId) {
        this.apartamentoId = apartamentoId;
    }

    public BlocoEnum getBloco() {
        return bloco;
    }

    public void setBloco(BlocoEnum bloco) {
        this.bloco = bloco;
    }

    public String getNumeroApartamento() {
        return numeroApartamento;
    }

    public void setNumeroApartamento(String numeroApartamento) {
        this.numeroApartamento = numeroApartamento;
    }

    public String getNomeTitular() {
        return nomeTitular;
    }

    public void setNomeTitular(String nomeTitular) {
        this.nomeTitular = nomeTitular;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public LocalDate getValidadeAte() {
        return validadeAte;
    }

    public void setValidadeAte(LocalDate validadeAte) {
        this.validadeAte = validadeAte;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeclaracaoNegativaDebito that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

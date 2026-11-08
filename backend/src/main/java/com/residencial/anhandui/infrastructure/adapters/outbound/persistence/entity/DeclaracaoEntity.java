package com.residencial.anhandui.infrastructure.adapters.outbound.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "declaracoes_dnd")
public class DeclaracaoEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "codigo_autenticidade", length = 50, nullable = false, unique = true)
    private String codigoAutenticidade;

    @Column(name = "apartamento_id", length = 36, nullable = false)
    private String apartamentoId;

    @Column(name = "bloco", length = 20, nullable = false)
    private String bloco;

    @Column(name = "numero_apartamento", length = 10, nullable = false)
    private String numeroApartamento;

    @Column(name = "nome_titular", nullable = false)
    private String nomeTitular;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "validade_ate", nullable = false)
    private LocalDate validadeAte;

    @Column(name = "observacao")
    private String observacao;

    public DeclaracaoEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigoAutenticidade() {
        return codigoAutenticidade;
    }

    public void setCodigoAutenticidade(String codigoAutenticidade) {
        this.codigoAutenticidade = codigoAutenticidade;
    }

    public String getApartamentoId() {
        return apartamentoId;
    }

    public void setApartamentoId(String apartamentoId) {
        this.apartamentoId = apartamentoId;
    }

    public String getBloco() {
        return bloco;
    }

    public void setBloco(String bloco) {
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
}

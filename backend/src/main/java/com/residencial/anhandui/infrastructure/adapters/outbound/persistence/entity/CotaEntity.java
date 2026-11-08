package com.residencial.anhandui.infrastructure.adapters.outbound.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cotas_condominiais")
public class CotaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "apartamento_id", length = 36, nullable = false)
    private String apartamentoId;

    @Column(name = "ano_mes", length = 7, nullable = false)
    private String anoMes;

    @Column(name = "valor_base", nullable = false)
    private BigDecimal valorBase;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    public CotaEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApartamentoId() {
        return apartamentoId;
    }

    public void setApartamentoId(String apartamentoId) {
        this.apartamentoId = apartamentoId;
    }

    public String getAnoMes() {
        return anoMes;
    }

    public void setAnoMes(String anoMes) {
        this.anoMes = anoMes;
    }

    public BigDecimal getValorBase() {
        return valorBase;
    }

    public void setValorBase(BigDecimal valorBase) {
        this.valorBase = valorBase;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
}

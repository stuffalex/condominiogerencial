package com.residencial.anhandui.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class PagamentoDinheiro implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID cotaId;
    private UUID apartamentoId;
    private long numeroRecibo;
    private BigDecimal valorPago;
    private LocalDateTime dataHora;
    private String recebidoPor;
    private String observacao;

    public PagamentoDinheiro() {
    }

    public PagamentoDinheiro(UUID id, UUID cotaId, UUID apartamentoId, long numeroRecibo, BigDecimal valorPago, LocalDateTime dataHora, String recebidoPor, String observacao) {
        this.id = id != null ? id : UUID.randomUUID();
        this.cotaId = cotaId;
        this.apartamentoId = apartamentoId;
        this.numeroRecibo = numeroRecibo;
        this.valorPago = valorPago;
        this.dataHora = dataHora != null ? dataHora : LocalDateTime.now();
        this.recebidoPor = recebidoPor != null ? recebidoPor : "Administração / Síndico";
        this.observacao = observacao;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCotaId() {
        return cotaId;
    }

    public void setCotaId(UUID cotaId) {
        this.cotaId = cotaId;
    }

    public UUID getApartamentoId() {
        return apartamentoId;
    }

    public void setApartamentoId(UUID apartamentoId) {
        this.apartamentoId = apartamentoId;
    }

    public long getNumeroRecibo() {
        return numeroRecibo;
    }

    public void setNumeroRecibo(long numeroRecibo) {
        this.numeroRecibo = numeroRecibo;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getRecebidoPor() {
        return recebidoPor;
    }

    public void setRecebidoPor(String recebidoPor) {
        this.recebidoPor = recebidoPor;
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
        if (!(o instanceof PagamentoDinheiro that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

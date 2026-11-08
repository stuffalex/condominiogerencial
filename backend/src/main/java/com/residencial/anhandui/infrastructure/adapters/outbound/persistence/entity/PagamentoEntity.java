package com.residencial.anhandui.infrastructure.adapters.outbound.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos_dinheiro")
public class PagamentoEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "cota_id", length = 36, nullable = false)
    private String cotaId;

    @Column(name = "apartamento_id", length = 36, nullable = false)
    private String apartamentoId;

    @Column(name = "numero_recibo", nullable = false)
    private long numeroRecibo;

    @Column(name = "valor_pago", nullable = false)
    private BigDecimal valorPago;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "recebido_por", nullable = false)
    private String recebidoPor;

    @Column(name = "observacao")
    private String observacao;

    public PagamentoEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCotaId() {
        return cotaId;
    }

    public void setCotaId(String cotaId) {
        this.cotaId = cotaId;
    }

    public String getApartamentoId() {
        return apartamentoId;
    }

    public void setApartamentoId(String apartamentoId) {
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
}

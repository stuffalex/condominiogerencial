package com.residencial.anhandui.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class CotaCondominial implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final BigDecimal VALOR_TAXA_PADRAO = new BigDecimal("180.00");

    private UUID id;
    private UUID apartamentoId;
    private String anoMes; // Formato YYYY-MM (ex: "2026-11")
    private BigDecimal valorBase;
    private LocalDate dataVencimento;
    private StatusCota status;
    private LocalDate dataPagamento;
    private String comprovanteFisicoArquivo;

    public CotaCondominial() {
    }

    public CotaCondominial(UUID id, UUID apartamentoId, String anoMes, BigDecimal valorBase, LocalDate dataVencimento, StatusCota status, LocalDate dataPagamento) {
        this.id = id != null ? id : UUID.randomUUID();
        this.apartamentoId = apartamentoId;
        this.anoMes = anoMes;
        this.valorBase = valorBase != null ? valorBase : VALOR_TAXA_PADRAO;
        this.dataVencimento = dataVencimento;
        this.status = status != null ? status : StatusCota.PENDENTE;
        this.dataPagamento = dataPagamento;
    }

    public static CotaCondominial criarPadrao(UUID apartamentoId, String anoMes, LocalDate dataVencimento) {
        return new CotaCondominial(
                UUID.randomUUID(),
                apartamentoId,
                anoMes,
                VALOR_TAXA_PADRAO,
                dataVencimento,
                StatusCota.PENDENTE,
                null
        );
    }

    public boolean isPaga() {
        return StatusCota.PAGA.equals(this.status);
    }

    public boolean isEmAtraso(LocalDate dataReferencia) {
        if (isPaga()) return false;
        return dataVencimento != null && dataVencimento.isBefore(dataReferencia);
    }

    public void marcarComoPaga(LocalDate dataPagamento) {
        this.status = StatusCota.PAGA;
        this.dataPagamento = dataPagamento != null ? dataPagamento : LocalDate.now();
    }

    public boolean isPossuiComprovanteFisico() {
        return comprovanteFisicoArquivo != null && !comprovanteFisicoArquivo.isBlank();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getApartamentoId() {
        return apartamentoId;
    }

    public void setApartamentoId(UUID apartamentoId) {
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

    public StatusCota getStatus() {
        return status;
    }

    public void setStatus(StatusCota status) {
        this.status = status;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getComprovanteFisicoArquivo() {
        return comprovanteFisicoArquivo;
    }

    public void setComprovanteFisicoArquivo(String comprovanteFisicoArquivo) {
        this.comprovanteFisicoArquivo = comprovanteFisicoArquivo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CotaCondominial that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

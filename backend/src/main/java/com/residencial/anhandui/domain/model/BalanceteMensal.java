package com.residencial.anhandui.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BalanceteMensal implements Serializable {
    private static final long serialVersionUID = 1L;

    private String mesAno; // "2026-11"
    private int totalApartamentos;
    private int totalAdimplentes;
    private int totalInadimplentes;
    private BigDecimal totalArrecadadoTaxaOrdinaria;
    private BigDecimal totalArrecadadoFundoMuro;
    private BigDecimal totalDespesas;
    private BigDecimal saldoMes;
    private List<String> demonstrativoLinhas;

    public BalanceteMensal() {
        this.demonstrativoLinhas = new ArrayList<>();
    }

    public BalanceteMensal(String mesAno, int totalApartamentos, int totalAdimplentes, int totalInadimplentes,
                           BigDecimal totalArrecadadoTaxaOrdinaria, BigDecimal totalArrecadadoFundoMuro,
                           BigDecimal totalDespesas) {
        this.mesAno = mesAno;
        this.totalApartamentos = totalApartamentos;
        this.totalAdimplentes = totalAdimplentes;
        this.totalInadimplentes = totalInadimplentes;
        this.totalArrecadadoTaxaOrdinaria = totalArrecadadoTaxaOrdinaria != null ? totalArrecadadoTaxaOrdinaria : BigDecimal.ZERO;
        this.totalArrecadadoFundoMuro = totalArrecadadoFundoMuro != null ? totalArrecadadoFundoMuro : BigDecimal.ZERO;
        this.totalDespesas = totalDespesas != null ? totalDespesas : BigDecimal.ZERO;
        
        BigDecimal totalEntradas = this.totalArrecadadoTaxaOrdinaria.add(this.totalArrecadadoFundoMuro);
        this.saldoMes = totalEntradas.subtract(this.totalDespesas);
        this.demonstrativoLinhas = new ArrayList<>();
    }

    public String getMesAno() {
        return mesAno;
    }

    public void setMesAno(String mesAno) {
        this.mesAno = mesAno;
    }

    public int getTotalApartamentos() {
        return totalApartamentos;
    }

    public void setTotalApartamentos(int totalApartamentos) {
        this.totalApartamentos = totalApartamentos;
    }

    public int getTotalAdimplentes() {
        return totalAdimplentes;
    }

    public void setTotalAdimplentes(int totalAdimplentes) {
        this.totalAdimplentes = totalAdimplentes;
    }

    public int getTotalInadimplentes() {
        return totalInadimplentes;
    }

    public void setTotalInadimplentes(int totalInadimplentes) {
        this.totalInadimplentes = totalInadimplentes;
    }

    public BigDecimal getTotalArrecadadoTaxaOrdinaria() {
        return totalArrecadadoTaxaOrdinaria;
    }

    public void setTotalArrecadadoTaxaOrdinaria(BigDecimal totalArrecadadoTaxaOrdinaria) {
        this.totalArrecadadoTaxaOrdinaria = totalArrecadadoTaxaOrdinaria;
    }

    public BigDecimal getTotalArrecadadoFundoMuro() {
        return totalArrecadadoFundoMuro;
    }

    public void setTotalArrecadadoFundoMuro(BigDecimal totalArrecadadoFundoMuro) {
        this.totalArrecadadoFundoMuro = totalArrecadadoFundoMuro;
    }

    public BigDecimal getTotalDespesas() {
        return totalDespesas;
    }

    public void setTotalDespesas(BigDecimal totalDespesas) {
        this.totalDespesas = totalDespesas;
    }

    public BigDecimal getSaldoMes() {
        return saldoMes;
    }

    public void setSaldoMes(BigDecimal saldoMes) {
        this.saldoMes = saldoMes;
    }

    public List<String> getDemonstrativoLinhas() {
        return demonstrativoLinhas;
    }

    public void setDemonstrativoLinhas(List<String> demonstrativoLinhas) {
        this.demonstrativoLinhas = demonstrativoLinhas;
    }
}

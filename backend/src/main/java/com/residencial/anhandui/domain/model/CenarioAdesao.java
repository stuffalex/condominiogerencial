package com.residencial.anhandui.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CenarioAdesao implements Serializable {
    private static final long serialVersionUID = 1L;

    private String descricao;
    private double percentualAdesao;
    private int unidadesAderentes;
    private BigDecimal arrecadacaoMensal;
    private int mesesNecessarios;
    private BigDecimal custoTotalPorUnidade;

    public CenarioAdesao() {
    }

    public CenarioAdesao(String descricao, double percentualAdesao, int totalUnidades, BigDecimal custoTotal, BigDecimal valorCotaMensal) {
        this.descricao = descricao;
        this.percentualAdesao = percentualAdesao;
        this.unidadesAderentes = (int) Math.round(totalUnidades * (percentualAdesao / 100.0));
        if (this.unidadesAderentes < 1) this.unidadesAderentes = 1;

        this.arrecadacaoMensal = valorCotaMensal.multiply(BigDecimal.valueOf(this.unidadesAderentes));
        
        if (this.arrecadacaoMensal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal mesesCalc = custoTotal.divide(this.arrecadacaoMensal, 2, RoundingMode.CEILING);
            this.mesesNecessarios = (int) Math.ceil(mesesCalc.doubleValue());
        } else {
            this.mesesNecessarios = 0;
        }

        if (this.unidadesAderentes > 0) {
            this.custoTotalPorUnidade = custoTotal.divide(BigDecimal.valueOf(this.unidadesAderentes), 2, RoundingMode.HALF_UP);
        } else {
            this.custoTotalPorUnidade = BigDecimal.ZERO;
        }
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPercentualAdesao() {
        return percentualAdesao;
    }

    public void setPercentualAdesao(double percentualAdesao) {
        this.percentualAdesao = percentualAdesao;
    }

    public int getUnidadesAderentes() {
        return unidadesAderentes;
    }

    public void setUnidadesAderentes(int unidadesAderentes) {
        this.unidadesAderentes = unidadesAderentes;
    }

    public BigDecimal getArrecadacaoMensal() {
        return arrecadacaoMensal;
    }

    public void setArrecadacaoMensal(BigDecimal arrecadacaoMensal) {
        this.arrecadacaoMensal = arrecadacaoMensal;
    }

    public int getMesesNecessarios() {
        return mesesNecessarios;
    }

    public void setMesesNecessarios(int mesesNecessarios) {
        this.mesesNecessarios = mesesNecessarios;
    }

    public BigDecimal getCustoTotalPorUnidade() {
        return custoTotalPorUnidade;
    }

    public void setCustoTotalPorUnidade(BigDecimal custoTotalPorUnidade) {
        this.custoTotalPorUnidade = custoTotalPorUnidade;
    }
}

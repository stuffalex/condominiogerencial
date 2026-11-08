package com.residencial.anhandui.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PlanoObraMuro implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final BigDecimal CUSTO_PADRAO_MURO = new BigDecimal("30000.00");
    public static final int TOTAL_UNIDADES_PADRAO = 24;

    private BigDecimal custoTotalEstimado;
    private BigDecimal valorCotaSugerida;
    private int totalUnidades;
    private List<CenarioAdesao> cenarios;

    public PlanoObraMuro() {
        this.cenarios = new ArrayList<>();
    }

    public PlanoObraMuro(BigDecimal custoTotalEstimado, BigDecimal valorCotaSugerida, int totalUnidades) {
        this.custoTotalEstimado = custoTotalEstimado != null ? custoTotalEstimado : CUSTO_PADRAO_MURO;
        this.valorCotaSugerida = valorCotaSugerida != null ? valorCotaSugerida : new BigDecimal("100.00");
        this.totalUnidades = totalUnidades > 0 ? totalUnidades : TOTAL_UNIDADES_PADRAO;
        this.cenarios = new ArrayList<>();
        gerarCenarios();
    }

    public void gerarCenarios() {
        this.cenarios.clear();
        this.cenarios.add(new CenarioAdesao("100% de Adesão (Pleno)", 100.0, totalUnidades, custoTotalEstimado, valorCotaSugerida));
        this.cenarios.add(new CenarioAdesao("70% de Adesão (Realista)", 70.0, totalUnidades, custoTotalEstimado, valorCotaSugerida));
        this.cenarios.add(new CenarioAdesao("50% de Adesão (Crítico)", 50.0, totalUnidades, custoTotalEstimado, valorCotaSugerida));
    }

    public BigDecimal getCustoTotalEstimado() {
        return custoTotalEstimado;
    }

    public void setCustoTotalEstimado(BigDecimal custoTotalEstimado) {
        this.custoTotalEstimado = custoTotalEstimado;
        gerarCenarios();
    }

    public BigDecimal getValorCotaSugerida() {
        return valorCotaSugerida;
    }

    public void setValorCotaSugerida(BigDecimal valorCotaSugerida) {
        this.valorCotaSugerida = valorCotaSugerida;
        gerarCenarios();
    }

    public int getTotalUnidades() {
        return totalUnidades;
    }

    public void setTotalUnidades(int totalUnidades) {
        this.totalUnidades = totalUnidades;
        gerarCenarios();
    }

    public List<CenarioAdesao> getCenarios() {
        return cenarios;
    }

    public void setCenarios(List<CenarioAdesao> cenarios) {
        this.cenarios = cenarios;
    }
}

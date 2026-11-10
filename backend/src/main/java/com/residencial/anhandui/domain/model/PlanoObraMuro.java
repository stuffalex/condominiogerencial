package com.residencial.anhandui.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PlanoObraMuro implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final BigDecimal CUSTO_PADRAO_MURO = new BigDecimal("30000.00");
    public static final int TOTAL_UNIDADES_PADRAO = 24;

    private String tituloServico;
    private BigDecimal valorMaoDeObra;
    private BigDecimal valorMaterial;
    private BigDecimal custoTotalEstimado;
    private Integer tempoExecucaoServico;
    private String prestadorNome;
    private String prestadorTelefone;

    // Gestão de Caixa & Contas
    private BigDecimal saldoCaixaAtual;
    private BigDecimal despesasPrevisiveis;
    private BigDecimal despesasNaoPrevisiveis;
    private BigDecimal totalContasMensais;

    private BigDecimal creditoTotal;
    private BigDecimal debitoTotal;
    private BigDecimal saldoCaixaProjetado;
    private BigDecimal caixinhaObraProjetada;

    // Modalidade de Adesão
    private TipoCalculoAdesao tipoCalculoAdesao;
    private BigDecimal valorCotaSugerida;
    private Integer mesesDesejados;
    private int totalUnidades;
    private List<CenarioAdesao> cenarios;

    public PlanoObraMuro() {
        this.cenarios = new ArrayList<>();
    }

    public PlanoObraMuro(BigDecimal custoTotalEstimado, BigDecimal valorCotaSugerida, int totalUnidades) {
        this("Obra do Muro dos Blocos G e H",
                BigDecimal.ZERO,
                custoTotalEstimado != null ? custoTotalEstimado : CUSTO_PADRAO_MURO,
                3,
                "A Definir / Empreiteira",
                "",
                new BigDecimal("5000.00"),
                new BigDecimal("1200.00"),
                new BigDecimal("300.00"),
                TipoCalculoAdesao.POR_VALOR_PARCELA,
                valorCotaSugerida != null ? valorCotaSugerida : new BigDecimal("100.00"),
                12,
                totalUnidades > 0 ? totalUnidades : TOTAL_UNIDADES_PADRAO);
    }

    public PlanoObraMuro(String tituloServico, BigDecimal valorMaoDeObra, BigDecimal valorMaterial,
                         Integer tempoExecucaoServico, String prestadorNome, String prestadorTelefone,
                         BigDecimal saldoCaixaAtual, BigDecimal despesasPrevisiveis, BigDecimal despesasNaoPrevisiveis,
                         TipoCalculoAdesao tipoCalculoAdesao, BigDecimal valorCotaSugerida,
                         Integer mesesDesejados, int totalUnidades) {
        this.tituloServico = (tituloServico != null && !tituloServico.isBlank()) ? tituloServico : "Obra do Muro dos Blocos G e H";
        this.valorMaoDeObra = valorMaoDeObra != null ? valorMaoDeObra : BigDecimal.ZERO;
        this.valorMaterial = valorMaterial != null ? valorMaterial : BigDecimal.ZERO;

        BigDecimal soma = this.valorMaoDeObra.add(this.valorMaterial);
        this.custoTotalEstimado = soma.compareTo(BigDecimal.ZERO) > 0 ? soma : CUSTO_PADRAO_MURO;

        this.tempoExecucaoServico = tempoExecucaoServico != null ? tempoExecucaoServico : 3;
        this.prestadorNome = prestadorNome != null ? prestadorNome : "A Definir";
        this.prestadorTelefone = prestadorTelefone != null ? prestadorTelefone : "";

        this.saldoCaixaAtual = saldoCaixaAtual != null ? saldoCaixaAtual : BigDecimal.ZERO;
        this.despesasPrevisiveis = despesasPrevisiveis != null ? despesasPrevisiveis : new BigDecimal("1200.00");
        this.despesasNaoPrevisiveis = despesasNaoPrevisiveis != null ? despesasNaoPrevisiveis : new BigDecimal("300.00");
        this.totalContasMensais = this.despesasPrevisiveis.add(this.despesasNaoPrevisiveis);

        this.tipoCalculoAdesao = tipoCalculoAdesao != null ? tipoCalculoAdesao : TipoCalculoAdesao.POR_VALOR_PARCELA;
        this.valorCotaSugerida = valorCotaSugerida != null ? valorCotaSugerida : new BigDecimal("100.00");
        this.mesesDesejados = (mesesDesejados != null && mesesDesejados > 0) ? mesesDesejados : 12;
        this.totalUnidades = totalUnidades > 0 ? totalUnidades : TOTAL_UNIDADES_PADRAO;

        this.cenarios = new ArrayList<>();
        gerarCenariosECaixa();
    }

    public void gerarCenariosECaixa() {
        this.cenarios.clear();

        // Cenários de Adesão
        this.cenarios.add(new CenarioAdesao("100% de Adesão (Pleno)", 100.0, totalUnidades,
                custoTotalEstimado, valorCotaSugerida, tipoCalculoAdesao, mesesDesejados));
        this.cenarios.add(new CenarioAdesao("70% de Adesão (Realista)", 70.0, totalUnidades,
                custoTotalEstimado, valorCotaSugerida, tipoCalculoAdesao, mesesDesejados));
        this.cenarios.add(new CenarioAdesao("50% de Adesão (Crítico)", 50.0, totalUnidades,
                custoTotalEstimado, valorCotaSugerida, tipoCalculoAdesao, mesesDesejados));

        // Projeção Financeira baseada no cenário realista (70%)
        CenarioAdesao cenarioRef = this.cenarios.get(1); // 70%
        int mesesHorizonte = cenarioRef.getMesesNecessarios() > 0 ? cenarioRef.getMesesNecessarios() : 12;

        BigDecimal arrecadacaoExtraordinariaTotal = cenarioRef.getArrecadacaoMensal().multiply(BigDecimal.valueOf(mesesHorizonte));
        BigDecimal arrecadacaoOrdinariaTotal = BigDecimal.valueOf(totalUnidades)
                .multiply(new BigDecimal("180.00"))
                .multiply(BigDecimal.valueOf(mesesHorizonte));

        // Crédito: saldo em caixa atual + cotas ordinárias + cotas extraordinárias da obra
        this.creditoTotal = this.saldoCaixaAtual.add(arrecadacaoOrdinariaTotal).add(arrecadacaoExtraordinariaTotal);

        // Débito: despesas mensais ordinárias do condomínio + custo total da obra
        BigDecimal despesasTotaisPeriodo = this.totalContasMensais.multiply(BigDecimal.valueOf(mesesHorizonte));
        this.debitoTotal = despesasTotaisPeriodo.add(this.custoTotalEstimado);

        // Saldo líquido projetado do caixa
        this.saldoCaixaProjetado = this.creditoTotal.subtract(this.debitoTotal);

        // Caixinha da Obra: saldo reservado especificamente para o projeto
        this.caixinhaObraProjetada = arrecadacaoExtraordinariaTotal;
    }

    public String getTituloServico() {
        return tituloServico;
    }

    public void setTituloServico(String tituloServico) {
        this.tituloServico = tituloServico;
    }

    public BigDecimal getValorMaoDeObra() {
        return valorMaoDeObra;
    }

    public void setValorMaoDeObra(BigDecimal valorMaoDeObra) {
        this.valorMaoDeObra = valorMaoDeObra;
    }

    public BigDecimal getValorMaterial() {
        return valorMaterial;
    }

    public void setValorMaterial(BigDecimal valorMaterial) {
        this.valorMaterial = valorMaterial;
    }

    public BigDecimal getCustoTotalEstimado() {
        return custoTotalEstimado;
    }

    public void setCustoTotalEstimado(BigDecimal custoTotalEstimado) {
        this.custoTotalEstimado = custoTotalEstimado;
        gerarCenariosECaixa();
    }

    public Integer getTempoExecucaoServico() {
        return tempoExecucaoServico;
    }

    public void setTempoExecucaoServico(Integer tempoExecucaoServico) {
        this.tempoExecucaoServico = tempoExecucaoServico;
    }

    public String getPrestadorNome() {
        return prestadorNome;
    }

    public void setPrestadorNome(String prestadorNome) {
        this.prestadorNome = prestadorNome;
    }

    public String getPrestadorTelefone() {
        return prestadorTelefone;
    }

    public void setPrestadorTelefone(String prestadorTelefone) {
        this.prestadorTelefone = prestadorTelefone;
    }

    public BigDecimal getSaldoCaixaAtual() {
        return saldoCaixaAtual;
    }

    public void setSaldoCaixaAtual(BigDecimal saldoCaixaAtual) {
        this.saldoCaixaAtual = saldoCaixaAtual;
    }

    public BigDecimal getDespesasPrevisiveis() {
        return despesasPrevisiveis;
    }

    public void setDespesasPrevisiveis(BigDecimal despesasPrevisiveis) {
        this.despesasPrevisiveis = despesasPrevisiveis;
    }

    public BigDecimal getDespesasNaoPrevisiveis() {
        return despesasNaoPrevisiveis;
    }

    public void setDespesasNaoPrevisiveis(BigDecimal despesasNaoPrevisiveis) {
        this.despesasNaoPrevisiveis = despesasNaoPrevisiveis;
    }

    public BigDecimal getTotalContasMensais() {
        return totalContasMensais;
    }

    public BigDecimal getCreditoTotal() {
        return creditoTotal;
    }

    public BigDecimal getDebitoTotal() {
        return debitoTotal;
    }

    public BigDecimal getSaldoCaixaProjetado() {
        return saldoCaixaProjetado;
    }

    public BigDecimal getCaixinhaObraProjetada() {
        return caixinhaObraProjetada;
    }

    public TipoCalculoAdesao getTipoCalculoAdesao() {
        return tipoCalculoAdesao;
    }

    public void setTipoCalculoAdesao(TipoCalculoAdesao tipoCalculoAdesao) {
        this.tipoCalculoAdesao = tipoCalculoAdesao;
        gerarCenariosECaixa();
    }

    public BigDecimal getValorCotaSugerida() {
        return valorCotaSugerida;
    }

    public void setValorCotaSugerida(BigDecimal valorCotaSugerida) {
        this.valorCotaSugerida = valorCotaSugerida;
        gerarCenariosECaixa();
    }

    public Integer getMesesDesejados() {
        return mesesDesejados;
    }

    public void setMesesDesejados(Integer mesesDesejados) {
        this.mesesDesejados = mesesDesejados;
        gerarCenariosECaixa();
    }

    public int getTotalUnidades() {
        return totalUnidades;
    }

    public void setTotalUnidades(int totalUnidades) {
        this.totalUnidades = totalUnidades;
        gerarCenariosECaixa();
    }

    public List<CenarioAdesao> getCenarios() {
        return cenarios;
    }

    public void setCenarios(List<CenarioAdesao> cenarios) {
        this.cenarios = cenarios;
    }
}

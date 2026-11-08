package com.residencial.anhandui.domain.usecase;

import com.residencial.anhandui.domain.model.BalanceteMensal;
import com.residencial.anhandui.domain.model.CotaCondominial;
import com.residencial.anhandui.domain.ports.output.IApartamentoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.ICotaRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IPdfGeneratorPort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GerarBalanceteMensalUseCase {

    private final IApartamentoRepositoryPort apartamentoRepository;
    private final ICotaRepositoryPort cotaRepository;
    private final IPdfGeneratorPort pdfGenerator;

    public GerarBalanceteMensalUseCase(IApartamentoRepositoryPort apartamentoRepository,
                                       ICotaRepositoryPort cotaRepository,
                                       IPdfGeneratorPort pdfGenerator) {
        this.apartamentoRepository = apartamentoRepository;
        this.cotaRepository = cotaRepository;
        this.pdfGenerator = pdfGenerator;
    }

    public ResultadoBalancete executar(String anoMes, BigDecimal despesasDoMes) {
        long totalAptos = apartamentoRepository.contar();
        int total = totalAptos > 0 ? (int) totalAptos : 24;

        List<CotaCondominial> cotasDoMes = cotaRepository.listarPorAnoMes(anoMes);

        int adimplentes = 0;
        int inadimplentes = 0;
        BigDecimal totalArrecadado = BigDecimal.ZERO;

        for (CotaCondominial cota : cotasDoMes) {
            if (cota.isPaga()) {
                adimplentes++;
                totalArrecadado = totalArrecadado.add(cota.getValorBase());
            } else {
                inadimplentes++;
            }
        }

        BigDecimal despesas = despesasDoMes != null ? despesasDoMes : BigDecimal.ZERO;
        BalanceteMensal balancete = new BalanceteMensal(
                anoMes,
                total,
                adimplentes,
                inadimplentes,
                totalArrecadado,
                BigDecimal.ZERO, // Fundo Muro pode ser adicionado
                despesas
        );

        List<String> linhas = new ArrayList<>();
        linhas.add("Taxa Ordinária (R$ 180,00 x " + adimplentes + " unidades pagas): R$ " + totalArrecadado);
        linhas.add("Inadimplência Registrada: " + inadimplentes + " unidades");
        linhas.add("Despesas Operacionais do Mês: R$ " + despesas);
        linhas.add("Saldo Líquido Operacional do Mês: R$ " + balancete.getSaldoMes());
        balancete.setDemonstrativoLinhas(linhas);

        byte[] pdfBytes = pdfGenerator.gerarBalanceteMensalPdf(balancete);

        return new ResultadoBalancete(balancete, pdfBytes);
    }

    public record ResultadoBalancete(
            BalanceteMensal balancete,
            byte[] pdfBytes
    ) {}
}

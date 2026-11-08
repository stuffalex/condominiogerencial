package com.residencial.anhandui.domain.usecase;

import com.residencial.anhandui.domain.exception.DomainException;
import com.residencial.anhandui.domain.exception.EntidadeNaoEncontradaException;
import com.residencial.anhandui.domain.model.Apartamento;
import com.residencial.anhandui.domain.model.CotaCondominial;
import com.residencial.anhandui.domain.model.PagamentoDinheiro;
import com.residencial.anhandui.domain.ports.output.IApartamentoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.ICotaRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IPagamentoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IPdfGeneratorPort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegistrarPagamentoPeriodoUseCase {

    private final IApartamentoRepositoryPort apartamentoRepository;
    private final ICotaRepositoryPort cotaRepository;
    private final IPagamentoRepositoryPort pagamentoRepository;
    private final IPdfGeneratorPort pdfGenerator;

    public RegistrarPagamentoPeriodoUseCase(IApartamentoRepositoryPort apartamentoRepository,
                                           ICotaRepositoryPort cotaRepository,
                                           IPagamentoRepositoryPort pagamentoRepository,
                                           IPdfGeneratorPort pdfGenerator) {
        this.apartamentoRepository = apartamentoRepository;
        this.cotaRepository = cotaRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.pdfGenerator = pdfGenerator;
    }

    public ResultadoPeriodo executar(UUID apartamentoId, String anoMesInicio, String anoMesFim,
                                     BigDecimal valorPorMes, String recebidoPor, String observacao) {

        Apartamento apartamento = apartamentoRepository.buscarPorId(apartamentoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Apartamento não encontrado para o ID: " + apartamentoId));

        YearMonth inicio;
        YearMonth fim;
        try {
            inicio = YearMonth.parse(anoMesInicio);
            fim = YearMonth.parse(anoMesFim);
        } catch (Exception e) {
            throw new DomainException("Formato de mês inválido. Utilize o formato YYYY-MM (ex: 2026-01).");
        }

        if (inicio.isAfter(fim)) {
            throw new DomainException("O mês inicial (" + anoMesInicio + ") não pode ser posterior ao mês final (" + anoMesFim + ").");
        }

        BigDecimal valorUnitario = (valorPorMes != null && valorPorMes.compareTo(BigDecimal.ZERO) > 0)
                ? valorPorMes
                : CotaCondominial.VALOR_TAXA_PADRAO;

        List<CotaCondominial> cotasLiquidadas = new ArrayList<>();
        YearMonth atual = inicio;
        LocalDate hoje = LocalDate.now();

        while (!atual.isAfter(fim)) {
            String mesStr = atual.toString();
            LocalDate vencimento = atual.atDay(10);

            CotaCondominial cota = cotaRepository.buscarPorApartamentoEAnoMes(apartamentoId, mesStr)
                    .orElseGet(() -> CotaCondominial.criarPadrao(apartamentoId, mesStr, vencimento));

            cota.setValorBase(valorUnitario);
            cota.marcarComoPaga(hoje);
            CotaCondominial salva = cotaRepository.salvar(cota);
            cotasLiquidadas.add(salva);

            atual = atual.plusMonths(1);
        }

        if (cotasLiquidadas.isEmpty()) {
            throw new DomainException("Nenhuma cota selecionada no período informado.");
        }

        BigDecimal valorTotalAcumulado = valorUnitario.multiply(BigDecimal.valueOf(cotasLiquidadas.size()));
        long proximoRecibo = pagamentoRepository.obterProximoNumeroRecibo();

        // Grava o pagamento em dinheiro consolidado
        PagamentoDinheiro pagamento = new PagamentoDinheiro(
                UUID.randomUUID(),
                cotasLiquidadas.get(0).getId(), // Referência da 1ª cota
                apartamento.getId(),
                proximoRecibo,
                valorTotalAcumulado,
                LocalDateTime.now(),
                recebidoPor != null ? recebidoPor : "Administração / Síndico",
                String.format("Quitação acumulada de %d meses (%s a %s). %s",
                        cotasLiquidadas.size(), anoMesInicio, anoMesFim,
                        observacao != null ? observacao : "")
        );
        pagamentoRepository.salvar(pagamento);

        // Opção A: Emite recibo consolidado em 2 vias
        byte[] pdfBytes = pdfGenerator.gerarReciboConsolidadoPeriodoPdf(pagamento, apartamento, cotasLiquidadas);

        return new ResultadoPeriodo(pagamento, apartamento, cotasLiquidadas, pdfBytes);
    }

    public record ResultadoPeriodo(
            PagamentoDinheiro pagamento,
            Apartamento apartamento,
            List<CotaCondominial> cotasLiquidadas,
            byte[] reciboPdfBytes
    ) {}
}

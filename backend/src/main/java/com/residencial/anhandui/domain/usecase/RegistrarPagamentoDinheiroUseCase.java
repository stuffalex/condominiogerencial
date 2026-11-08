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
import java.util.UUID;

public class RegistrarPagamentoDinheiroUseCase {

    private final ICotaRepositoryPort cotaRepository;
    private final IApartamentoRepositoryPort apartamentoRepository;
    private final IPagamentoRepositoryPort pagamentoRepository;
    private final IPdfGeneratorPort pdfGenerator;

    public RegistrarPagamentoDinheiroUseCase(ICotaRepositoryPort cotaRepository,
                                            IApartamentoRepositoryPort apartamentoRepository,
                                            IPagamentoRepositoryPort pagamentoRepository,
                                            IPdfGeneratorPort pdfGenerator) {
        this.cotaRepository = cotaRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.pdfGenerator = pdfGenerator;
    }

    public ResultadoPagamento executar(UUID cotaId, BigDecimal valorPago, String recebidoPor, String observacao) {
        CotaCondominial cota = cotaRepository.buscarPorId(cotaId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cota condominial não encontrada para o ID: " + cotaId));

        if (cota.isPaga()) {
            throw new DomainException("Esta cota condominial já foi liquidada em " + cota.getDataPagamento());
        }

        Apartamento apartamento = apartamentoRepository.buscarPorId(cota.getApartamentoId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Apartamento não encontrado para o ID: " + cota.getApartamentoId()));

        BigDecimal valorEfetivo = (valorPago != null && valorPago.compareTo(BigDecimal.ZERO) > 0)
                ? valorPago
                : cota.getValorBase();

        // 1. Marca cota como paga
        cota.marcarComoPaga(LocalDate.now());
        cotaRepository.salvar(cota);

        // 2. Gera recibo sequencial
        long proximoNumeroRecibo = pagamentoRepository.obterProximoNumeroRecibo();
        PagamentoDinheiro pagamento = new PagamentoDinheiro(
                UUID.randomUUID(),
                cota.getId(),
                apartamento.getId(),
                proximoNumeroRecibo,
                valorEfetivo,
                LocalDateTime.now(),
                recebidoPor != null ? recebidoPor : "Administração / Síndico",
                observacao
        );
        pagamentoRepository.salvar(pagamento);

        // 3. Gera PDF do recibo em duas vias
        byte[] pdfBytes = pdfGenerator.gerarReciboDinheiroPdf(pagamento, apartamento, cota);

        return new ResultadoPagamento(pagamento, cota, apartamento, pdfBytes);
    }

    public record ResultadoPagamento(
            PagamentoDinheiro pagamento,
            CotaCondominial cota,
            Apartamento apartamento,
            byte[] reciboPdfBytes
    ) {}
}

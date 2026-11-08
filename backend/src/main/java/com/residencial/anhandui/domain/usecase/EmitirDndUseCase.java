package com.residencial.anhandui.domain.usecase;

import com.residencial.anhandui.domain.exception.EntidadeNaoEncontradaException;
import com.residencial.anhandui.domain.exception.InadimplenciaDetectadaException;
import com.residencial.anhandui.domain.model.Apartamento;
import com.residencial.anhandui.domain.model.DeclaracaoNegativaDebito;
import com.residencial.anhandui.domain.ports.output.IApartamentoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.ICotaRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IDeclaracaoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IPdfGeneratorPort;

import java.time.LocalDate;
import java.util.UUID;

public class EmitirDndUseCase {

    private final IApartamentoRepositoryPort apartamentoRepository;
    private final ICotaRepositoryPort cotaRepository;
    private final IDeclaracaoRepositoryPort declaracaoRepository;
    private final IPdfGeneratorPort pdfGenerator;

    public EmitirDndUseCase(IApartamentoRepositoryPort apartamentoRepository,
                            ICotaRepositoryPort cotaRepository,
                            IDeclaracaoRepositoryPort declaracaoRepository,
                            IPdfGeneratorPort pdfGenerator) {
        this.apartamentoRepository = apartamentoRepository;
        this.cotaRepository = cotaRepository;
        this.declaracaoRepository = declaracaoRepository;
        this.pdfGenerator = pdfGenerator;
    }

    public ResultadoDnd executar(UUID apartamentoId, String solicitante, String observacao) {
        Apartamento apartamento = apartamentoRepository.buscarPorId(apartamentoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Apartamento não encontrado para o ID: " + apartamentoId));

        LocalDate hoje = LocalDate.now();

        // 1. Verifica se há inadimplência
        boolean possuiPendencias = cotaRepository.possuiPendenciasAte(apartamentoId, hoje);
        if (possuiPendencias) {
            throw new InadimplenciaDetectadaException(String.format(
                    "Não é possível emitir a DND. O %s - Apto %s possui cotas condominiais pendentes de pagamento.",
                    apartamento.getBloco().getDescricao(),
                    apartamento.getNumero()
            ));
        }

        // 2. Determina titular para o documento
        String titular = (solicitante != null && !solicitante.isBlank())
                ? solicitante
                : (apartamento.getProprietario() != null && !apartamento.getProprietario().isNaoInformado()
                ? apartamento.getProprietario().getNome()
                : "Condômino Titular da Unidade " + apartamento.getNumero());

        // 3. Emite DND
        DeclaracaoNegativaDebito dnd = new DeclaracaoNegativaDebito(
                UUID.randomUUID(),
                apartamento.getId(),
                apartamento.getBloco(),
                apartamento.getNumero(),
                titular,
                hoje,
                hoje.plusDays(30),
                observacao
        );
        declaracaoRepository.salvar(dnd);

        // 4. Gera PDF da Certidão
        byte[] pdfBytes = pdfGenerator.gerarDndPdf(dnd, apartamento);

        return new ResultadoDnd(dnd, apartamento, pdfBytes);
    }

    public record ResultadoDnd(
            DeclaracaoNegativaDebito dnd,
            Apartamento apartamento,
            byte[] pdfBytes
    ) {}
}

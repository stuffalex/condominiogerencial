package com.residencial.anhandui.infrastructure.adapters.inbound.rest;

import com.residencial.anhandui.domain.exception.EntidadeNaoEncontradaException;
import com.residencial.anhandui.domain.model.Apartamento;
import com.residencial.anhandui.domain.model.DeclaracaoNegativaDebito;
import com.residencial.anhandui.domain.ports.output.IApartamentoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IDeclaracaoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IPdfGeneratorPort;
import com.residencial.anhandui.domain.usecase.EmitirDndUseCase;
import com.residencial.anhandui.infrastructure.adapters.inbound.rest.dto.EmitirDndRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dnd")
@CrossOrigin(origins = "*")
public class DndController {

    private final EmitirDndUseCase emitirDndUseCase;
    private final IDeclaracaoRepositoryPort declaracaoRepository;
    private final IApartamentoRepositoryPort apartamentoRepository;
    private final IPdfGeneratorPort pdfGenerator;

    public DndController(EmitirDndUseCase emitirDndUseCase,
                         IDeclaracaoRepositoryPort declaracaoRepository,
                         IApartamentoRepositoryPort apartamentoRepository,
                         IPdfGeneratorPort pdfGenerator) {
        this.emitirDndUseCase = emitirDndUseCase;
        this.declaracaoRepository = declaracaoRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.pdfGenerator = pdfGenerator;
    }

    @PostMapping("/emitir")
    public ResponseEntity<byte[]> emitirDnd(@RequestBody EmitirDndRequest request) {
        EmitirDndUseCase.ResultadoDnd res = emitirDndUseCase.executar(
                request.apartamentoId(),
                request.solicitante(),
                request.observacao()
        );

        String filename = String.format("dnd-%s-%s.pdf",
                res.apartamento().getBloco().name(), res.apartamento().getNumero());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header("X-DND-Codigo", res.dnd().getCodigoAutenticidade())
                .contentType(MediaType.APPLICATION_PDF)
                .body(res.pdfBytes());
    }

    @GetMapping("/listar")
    public ResponseEntity<List<DeclaracaoNegativaDebito>> listarTodas() {
        return ResponseEntity.ok(declaracaoRepository.listarTodas());
    }

    @GetMapping("/{codigo}/pdf")
    public ResponseEntity<byte[]> baixarPorCodigo(@PathVariable String codigo) {
        DeclaracaoNegativaDebito dnd = declaracaoRepository.buscarPorCodigo(codigo)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("DND não encontrada para o código: " + codigo));

        Apartamento apto = apartamentoRepository.buscarPorId(dnd.getApartamentoId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Apartamento não encontrado"));

        byte[] pdf = pdfGenerator.gerarDndPdf(dnd, apto);
        String filename = "dnd-" + dnd.getCodigoAutenticidade() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}

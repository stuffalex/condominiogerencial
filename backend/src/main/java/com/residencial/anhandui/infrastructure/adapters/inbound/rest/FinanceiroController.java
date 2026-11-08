package com.residencial.anhandui.infrastructure.adapters.inbound.rest;

import com.residencial.anhandui.domain.exception.DomainException;
import com.residencial.anhandui.domain.exception.EntidadeNaoEncontradaException;
import com.residencial.anhandui.domain.model.Apartamento;
import com.residencial.anhandui.domain.model.BalanceteMensal;
import com.residencial.anhandui.domain.model.CotaCondominial;
import com.residencial.anhandui.domain.model.PagamentoDinheiro;
import com.residencial.anhandui.domain.ports.output.IApartamentoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.ICotaRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IDocumentoStoragePort;
import com.residencial.anhandui.domain.ports.output.IPagamentoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IPdfGeneratorPort;
import com.residencial.anhandui.domain.usecase.GerarBalanceteMensalUseCase;
import com.residencial.anhandui.domain.usecase.RegistrarPagamentoDinheiroUseCase;
import com.residencial.anhandui.domain.usecase.RegistrarPagamentoPeriodoUseCase;
import com.residencial.anhandui.infrastructure.adapters.inbound.rest.dto.RegistrarPagamentoPeriodoRequest;
import com.residencial.anhandui.infrastructure.adapters.inbound.rest.dto.RegistrarPagamentoRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/financeiro")
@CrossOrigin(origins = "*")
public class FinanceiroController {

    private final ICotaRepositoryPort cotaRepository;
    private final IApartamentoRepositoryPort apartamentoRepository;
    private final IPagamentoRepositoryPort pagamentoRepository;
    private final RegistrarPagamentoDinheiroUseCase registrarPagamentoUseCase;
    private final RegistrarPagamentoPeriodoUseCase registrarPagamentoPeriodoUseCase;
    private final GerarBalanceteMensalUseCase gerarBalanceteUseCase;
    private final IDocumentoStoragePort storagePort;
    private final IPdfGeneratorPort pdfGenerator;

    public FinanceiroController(ICotaRepositoryPort cotaRepository,
                                IApartamentoRepositoryPort apartamentoRepository,
                                IPagamentoRepositoryPort pagamentoRepository,
                                RegistrarPagamentoDinheiroUseCase registrarPagamentoUseCase,
                                RegistrarPagamentoPeriodoUseCase registrarPagamentoPeriodoUseCase,
                                GerarBalanceteMensalUseCase gerarBalanceteUseCase,
                                IDocumentoStoragePort storagePort,
                                IPdfGeneratorPort pdfGenerator) {
        this.cotaRepository = cotaRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.registrarPagamentoUseCase = registrarPagamentoUseCase;
        this.registrarPagamentoPeriodoUseCase = registrarPagamentoPeriodoUseCase;
        this.gerarBalanceteUseCase = gerarBalanceteUseCase;
        this.storagePort = storagePort;
        this.pdfGenerator = pdfGenerator;
    }

    @GetMapping("/cotas")
    public ResponseEntity<List<CotaCondominial>> listarCotas(@RequestParam(required = false) String anoMes) {
        String ref = (anoMes != null && !anoMes.isBlank()) ? anoMes : YearMonth.now().toString();
        List<CotaCondominial> cotas = cotaRepository.listarPorAnoMes(ref);

        if (cotas.isEmpty()) {
            YearMonth ym = YearMonth.parse(ref);
            LocalDate venc = ym.atDay(10);
            List<Apartamento> aptos = apartamentoRepository.listarTodos();
            List<CotaCondominial> novas = new ArrayList<>();
            for (Apartamento apto : aptos) {
                novas.add(CotaCondominial.criarPadrao(apto.getId(), ref, venc));
            }
            cotaRepository.salvarTodas(novas);
            cotas = cotaRepository.listarPorAnoMes(ref);
        }

        return ResponseEntity.ok(cotas);
    }

    @PostMapping("/pagamentos/dinheiro")
    public ResponseEntity<byte[]> registrarPagamento(@RequestBody RegistrarPagamentoRequest request) {
        RegistrarPagamentoDinheiroUseCase.ResultadoPagamento resultado = registrarPagamentoUseCase.executar(
                request.cotaId(),
                request.valorPago(),
                request.recebidoPor(),
                request.observacao()
        );

        String filename = String.format("recibo-cota-%s-apto-%s.pdf",
                resultado.cota().getAnoMes(), resultado.apartamento().getNumero());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resultado.reciboPdfBytes());
    }

    @PostMapping("/pagamentos/periodo")
    public ResponseEntity<byte[]> registrarPagamentoPeriodo(@RequestBody RegistrarPagamentoPeriodoRequest request) {
        RegistrarPagamentoPeriodoUseCase.ResultadoPeriodo resultado = registrarPagamentoPeriodoUseCase.executar(
                request.apartamentoId(),
                request.anoMesInicio(),
                request.anoMesFim(),
                request.valorPorMes(),
                request.recebidoPor(),
                request.observacao()
        );

        String filename = String.format("recibo-consolidado-%s-%s-a-%s.pdf",
                resultado.apartamento().getNumero(), request.anoMesInicio(), request.anoMesFim());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resultado.reciboPdfBytes());
    }

    @PostMapping(value = "/cotas/{cotaId}/recibo-fisico", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CotaCondominial> uploadReciboFisico(
            @PathVariable UUID cotaId,
            @RequestParam("arquivo") MultipartFile arquivo) throws IOException {

        CotaCondominial cota = cotaRepository.buscarPorId(cotaId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cota condominial não encontrada para o ID: " + cotaId));

        if (arquivo.isEmpty()) {
            throw new DomainException("O arquivo de comprovante não pode estar vazio.");
        }

        String caminhoRelativo = storagePort.salvarArquivo(arquivo.getOriginalFilename(), arquivo.getBytes());
        cota.setComprovanteFisicoArquivo(caminhoRelativo);
        CotaCondominial salva = cotaRepository.salvar(cota);

        return ResponseEntity.ok(salva);
    }

    @GetMapping("/cotas/{cotaId}/recibo-fisico")
    public ResponseEntity<byte[]> downloadReciboFisico(@PathVariable UUID cotaId) {
        CotaCondominial cota = cotaRepository.buscarPorId(cotaId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cota não encontrada"));

        if (!cota.isPossuiComprovanteFisico()) {
            throw new DomainException("Esta cota não possui comprovante físico anexado.");
        }

        byte[] conteudo = storagePort.lerArquivo(cota.getComprovanteFisicoArquivo());
        String nome = "comprovante-fisico-" + cota.getAnoMes() + "-" + cota.getComprovanteFisicoArquivo();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nome + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(conteudo);
    }

    @GetMapping("/pagamentos/{pagamentoId}/recibo")
    public ResponseEntity<byte[]> baixarRecibo(@PathVariable UUID pagamentoId) {
        PagamentoDinheiro pag = pagamentoRepository.listarTodos().stream()
                .filter(p -> p.getId().equals(pagamentoId))
                .findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Pagamento não encontrado"));

        CotaCondominial cota = cotaRepository.buscarPorId(pag.getCotaId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cota não encontrada"));

        Apartamento apto = apartamentoRepository.buscarPorId(pag.getApartamentoId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Apartamento não encontrado"));

        byte[] pdf = pdfGenerator.gerarReciboDinheiroPdf(pag, apto, cota);
        String filename = String.format("recibo-%06d.pdf", pag.getNumeroRecibo());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/balancete")
    public ResponseEntity<BalanceteMensal> obterBalancete(
            @RequestParam(required = false) String anoMes,
            @RequestParam(required = false, defaultValue = "0") BigDecimal despesas) {
        String ref = (anoMes != null && !anoMes.isBlank()) ? anoMes : YearMonth.now().toString();
        GerarBalanceteMensalUseCase.ResultadoBalancete res = gerarBalanceteUseCase.executar(ref, despesas);
        return ResponseEntity.ok(res.balancete());
    }

    @GetMapping("/balancete/pdf")
    public ResponseEntity<byte[]> baixarBalancetePdf(
            @RequestParam(required = false) String anoMes,
            @RequestParam(required = false, defaultValue = "0") BigDecimal despesas) {
        String ref = (anoMes != null && !anoMes.isBlank()) ? anoMes : YearMonth.now().toString();
        GerarBalanceteMensalUseCase.ResultadoBalancete res = gerarBalanceteUseCase.executar(ref, despesas);

        String nomeMes = "mes";
        try {
            YearMonth ym = YearMonth.parse(ref);
            nomeMes = ym.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")).toLowerCase();
        } catch (Exception ignored) {}

        String filename = String.format("balancete-%s-%s-Residencial-Anhandui.pdf", ref, nomeMes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(res.pdfBytes());
    }
}

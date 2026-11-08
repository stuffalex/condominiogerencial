package com.residencial.anhandui.infrastructure.adapters.inbound.rest;

import com.residencial.anhandui.domain.exception.EntidadeNaoEncontradaException;
import com.residencial.anhandui.domain.model.Apartamento;
import com.residencial.anhandui.domain.model.BalanceteMensal;
import com.residencial.anhandui.domain.model.CotaCondominial;
import com.residencial.anhandui.domain.model.PagamentoDinheiro;
import com.residencial.anhandui.domain.ports.output.IApartamentoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.ICotaRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IPagamentoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IPdfGeneratorPort;
import com.residencial.anhandui.domain.usecase.GerarBalanceteMensalUseCase;
import com.residencial.anhandui.domain.usecase.RegistrarPagamentoDinheiroUseCase;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/financeiro")
@CrossOrigin(origins = "*")
public class FinanceiroController {

    private final ICotaRepositoryPort cotaRepository;
    private final IApartamentoRepositoryPort apartamentoRepository;
    private final IPagamentoRepositoryPort pagamentoRepository;
    private final RegistrarPagamentoDinheiroUseCase registrarPagamentoUseCase;
    private final GerarBalanceteMensalUseCase gerarBalanceteUseCase;
    private final IPdfGeneratorPort pdfGenerator;

    public FinanceiroController(ICotaRepositoryPort cotaRepository,
                                IApartamentoRepositoryPort apartamentoRepository,
                                IPagamentoRepositoryPort pagamentoRepository,
                                RegistrarPagamentoDinheiroUseCase registrarPagamentoUseCase,
                                GerarBalanceteMensalUseCase gerarBalanceteUseCase,
                                IPdfGeneratorPort pdfGenerator) {
        this.cotaRepository = cotaRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.registrarPagamentoUseCase = registrarPagamentoUseCase;
        this.gerarBalanceteUseCase = gerarBalanceteUseCase;
        this.pdfGenerator = pdfGenerator;
    }

    @GetMapping("/cotas")
    public ResponseEntity<List<CotaCondominial>> listarCotas(@RequestParam(required = false) String anoMes) {
        String ref = (anoMes != null && !anoMes.isBlank()) ? anoMes : YearMonth.now().toString();
        List<CotaCondominial> cotas = cotaRepository.listarPorAnoMes(ref);

        // Se o mês ainda não foi gerado, gera sob demanda
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

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"balancete-" + ref + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(res.pdfBytes());
    }
}

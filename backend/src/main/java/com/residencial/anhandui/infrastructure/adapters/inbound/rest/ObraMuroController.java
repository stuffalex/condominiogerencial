package com.residencial.anhandui.infrastructure.adapters.inbound.rest;

import com.residencial.anhandui.domain.model.PlanoObraMuro;
import com.residencial.anhandui.domain.model.TipoCalculoAdesao;
import com.residencial.anhandui.domain.usecase.SimularPlanoMuroUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/obra-muro")
@CrossOrigin(origins = "*")
public class ObraMuroController {

    private final SimularPlanoMuroUseCase simularPlanoMuroUseCase;

    public ObraMuroController(SimularPlanoMuroUseCase simularPlanoMuroUseCase) {
        this.simularPlanoMuroUseCase = simularPlanoMuroUseCase;
    }

    @GetMapping("/simular")
    public ResponseEntity<PlanoObraMuro> simular(
            @RequestParam(required = false) BigDecimal custoTotal,
            @RequestParam(required = false, defaultValue = "100.00") BigDecimal cotaSugerida,
            @RequestParam(required = false, defaultValue = "Obra do Muro dos Blocos G e H") String titulo,
            @RequestParam(required = false) BigDecimal maoDeObra,
            @RequestParam(required = false) BigDecimal material,
            @RequestParam(required = false, defaultValue = "3") Integer tempoServico,
            @RequestParam(required = false, defaultValue = "A Definir / Empreiteira") String prestadorNome,
            @RequestParam(required = false, defaultValue = "") String prestadorTelefone,
            @RequestParam(required = false, defaultValue = "5000.00") BigDecimal saldoCaixa,
            @RequestParam(required = false, defaultValue = "1200.00") BigDecimal previsiveis,
            @RequestParam(required = false, defaultValue = "300.00") BigDecimal naoPrevisiveis,
            @RequestParam(required = false, defaultValue = "POR_VALOR_PARCELA") String tipoCalculo,
            @RequestParam(required = false, defaultValue = "12") Integer mesesDesejados) {

        TipoCalculoAdesao tipo = TipoCalculoAdesao.POR_VALOR_PARCELA;
        try {
            if (tipoCalculo != null) {
                tipo = TipoCalculoAdesao.valueOf(tipoCalculo.toUpperCase());
            }
        } catch (Exception ignored) {}

        BigDecimal mo = maoDeObra != null ? maoDeObra : BigDecimal.ZERO;
        BigDecimal mat = material != null ? material : BigDecimal.ZERO;

        // Se maoDeObra ou material foram informados, calcula custo total composto
        if (mo.compareTo(BigDecimal.ZERO) > 0 || mat.compareTo(BigDecimal.ZERO) > 0) {
            PlanoObraMuro plano = simularPlanoMuroUseCase.executar(
                    titulo, mo, mat, tempoServico, prestadorNome, prestadorTelefone,
                    saldoCaixa, previsiveis, naoPrevisiveis, tipo, cotaSugerida, mesesDesejados);
            return ResponseEntity.ok(plano);
        }

        // Modo simplificado / legado
        BigDecimal custo = (custoTotal != null && custoTotal.compareTo(BigDecimal.ZERO) > 0)
                ? custoTotal
                : PlanoObraMuro.CUSTO_PADRAO_MURO;
        PlanoObraMuro plano = simularPlanoMuroUseCase.executar(custo, cotaSugerida);
        return ResponseEntity.ok(plano);
    }
}

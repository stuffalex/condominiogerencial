package com.residencial.anhandui.infrastructure.adapters.inbound.rest;

import com.residencial.anhandui.domain.model.PlanoObraMuro;
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
            @RequestParam(required = false, defaultValue = "30000.00") BigDecimal custoTotal,
            @RequestParam(required = false, defaultValue = "100.00") BigDecimal cotaSugerida) {
        PlanoObraMuro plano = simularPlanoMuroUseCase.executar(custoTotal, cotaSugerida);
        return ResponseEntity.ok(plano);
    }
}

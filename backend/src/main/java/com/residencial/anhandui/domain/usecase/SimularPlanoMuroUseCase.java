package com.residencial.anhandui.domain.usecase;

import com.residencial.anhandui.domain.model.PlanoObraMuro;
import com.residencial.anhandui.domain.ports.output.IApartamentoRepositoryPort;

import java.math.BigDecimal;

public class SimularPlanoMuroUseCase {

    private final IApartamentoRepositoryPort apartamentoRepository;

    public SimularPlanoMuroUseCase(IApartamentoRepositoryPort apartamentoRepository) {
        this.apartamentoRepository = apartamentoRepository;
    }

    public PlanoObraMuro executar(BigDecimal custoTotalEstimado, BigDecimal valorCotaSugerida) {
        long totalUnidades = apartamentoRepository.contar();
        int total = totalUnidades > 0 ? (int) totalUnidades : PlanoObraMuro.TOTAL_UNIDADES_PADRAO;

        BigDecimal custo = (custoTotalEstimado != null && custoTotalEstimado.compareTo(BigDecimal.ZERO) > 0)
                ? custoTotalEstimado
                : PlanoObraMuro.CUSTO_PADRAO_MURO;

        BigDecimal cota = (valorCotaSugerida != null && valorCotaSugerida.compareTo(BigDecimal.ZERO) > 0)
                ? valorCotaSugerida
                : new BigDecimal("100.00");

        return new PlanoObraMuro(custo, cota, total);
    }
}

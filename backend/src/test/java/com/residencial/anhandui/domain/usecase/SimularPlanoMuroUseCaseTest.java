package com.residencial.anhandui.domain.usecase;

import com.residencial.anhandui.domain.model.CenarioAdesao;
import com.residencial.anhandui.domain.model.PlanoObraMuro;
import com.residencial.anhandui.domain.model.TipoCalculoAdesao;
import com.residencial.anhandui.domain.ports.output.IApartamentoRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimularPlanoMuroUseCaseTest {

    @Mock
    private IApartamentoRepositoryPort apartamentoRepository;

    @InjectMocks
    private SimularPlanoMuroUseCase useCase;

    @Test
    @DisplayName("Deve simular cenários da obra do muro para 100%, 70% e 50% de adesão para R$ 30.000 com cota de R$ 100")
    void deveSimularCenariosObraMuroComSucesso() {
        // Arrange
        when(apartamentoRepository.contar()).thenReturn(24L);

        // Act
        PlanoObraMuro plano = useCase.executar(new BigDecimal("30000.00"), new BigDecimal("100.00"));

        // Assert
        assertNotNull(plano);
        assertEquals(3, plano.getCenarios().size());

        // Cenário 1: 100% (24 unidades, R$ 2.400/mês -> 30.000 / 2.400 = 12.5 -> 13 meses)
        CenarioAdesao c100 = plano.getCenarios().get(0);
        assertEquals(24, c100.getUnidadesAderentes());
        assertEquals(new BigDecimal("2400.00"), c100.getArrecadacaoMensal());
        assertEquals(13, c100.getMesesNecessarios());

        // Cenário 2: 70% (17 unidades, R$ 1.700/mês -> 30.000 / 1.700 = 17.6 -> 18 meses)
        CenarioAdesao c70 = plano.getCenarios().get(1);
        assertEquals(17, c70.getUnidadesAderentes());
        assertEquals(new BigDecimal("1700.00"), c70.getArrecadacaoMensal());
        assertEquals(18, c70.getMesesNecessarios());

        // Cenário 3: 50% (12 unidades, R$ 1.200/mês -> 30.000 / 1.200 = 25 meses)
        CenarioAdesao c50 = plano.getCenarios().get(2);
        assertEquals(12, c50.getUnidadesAderentes());
        assertEquals(new BigDecimal("1200.00"), c50.getArrecadacaoMensal());
        assertEquals(25, c50.getMesesNecessarios());
    }

    @Test
    @DisplayName("Deve simular meta com Mão de Obra R$ 31.000 e Materiais R$ 30.000 totalizando R$ 61.000 com previsão de caixa")
    void deveSimularMetaComMaoDeObraMateriaisEPrevisaoCaixa() {
        // Arrange
        when(apartamentoRepository.contar()).thenReturn(24L);

        // Act
        PlanoObraMuro plano = useCase.executar(
                "Construção do Muro Perimetral",
                new BigDecimal("31000.00"),
                new BigDecimal("30000.00"),
                3,
                "Empreiteira Silva & Filhos",
                "67999887766",
                new BigDecimal("5000.00"),
                new BigDecimal("1200.00"),
                new BigDecimal("300.00"),
                TipoCalculoAdesao.POR_VALOR_PARCELA,
                new BigDecimal("150.00"),
                12
        );

        // Assert
        assertNotNull(plano);
        assertEquals("Construção do Muro Perimetral", plano.getTituloServico());
        assertEquals(new BigDecimal("31000.00"), plano.getValorMaoDeObra());
        assertEquals(new BigDecimal("30000.00"), plano.getValorMaterial());
        assertEquals(new BigDecimal("61000.00"), plano.getCustoTotalEstimado());
        assertEquals("Empreiteira Silva & Filhos", plano.getPrestadorNome());
        assertEquals("67999887766", plano.getPrestadorTelefone());

        // Validação de Débito, Crédito e Caixinha
        assertNotNull(plano.getCreditoTotal());
        assertNotNull(plano.getDebitoTotal());
        assertNotNull(plano.getCaixinhaObraProjetada());
        assertTrue(plano.getCreditoTotal().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(plano.getDebitoTotal().compareTo(new BigDecimal("61000.00")) >= 0);

        // Cenário de 100%: 24 aptos a R$ 150 = R$ 3.600/mês -> 61.000 / 3.600 = 16.94 -> 17 meses
        CenarioAdesao c100 = plano.getCenarios().get(0);
        assertEquals(24, c100.getUnidadesAderentes());
        assertEquals(new BigDecimal("3600.00"), c100.getArrecadacaoMensal());
        assertEquals(17, c100.getMesesNecessarios());
    }

    @Test
    @DisplayName("Deve simular cálculo de adesão por tempo desejado (ex: 12 meses)")
    void deveSimularAdesaoPorTempoDesejado() {
        // Arrange
        when(apartamentoRepository.contar()).thenReturn(24L);

        // Act
        PlanoObraMuro plano = useCase.executar(
                "Pintura dos Blocos G e H",
                new BigDecimal("10000.00"),
                new BigDecimal("14000.00"),
                2,
                "Pintor João",
                "67988887777",
                new BigDecimal("2000.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("200.00"),
                TipoCalculoAdesao.POR_TEMPO_PRAZO,
                new BigDecimal("100.00"),
                12
        );

        // Assert
        assertEquals(new BigDecimal("24000.00"), plano.getCustoTotalEstimado());
        // 12 meses desejados
        CenarioAdesao c100 = plano.getCenarios().get(0);
        assertEquals(12, c100.getMesesNecessarios());
        // Arrecadação mensal necessária: 24.000 / 12 = 2.000
        assertEquals(new BigDecimal("2000.00"), c100.getArrecadacaoMensal());
        // Parcela por unidade (24 aptos): 2.000 / 24 = 83.34
        assertEquals(new BigDecimal("83.34"), c100.getValorParcelaSugerida());
    }
}

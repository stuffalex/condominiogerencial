package com.residencial.anhandui.domain.usecase;

import com.residencial.anhandui.domain.model.CenarioAdesao;
import com.residencial.anhandui.domain.model.PlanoObraMuro;
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
}

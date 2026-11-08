package com.residencial.anhandui.domain.usecase;

import com.residencial.anhandui.domain.exception.DomainException;
import com.residencial.anhandui.domain.model.Apartamento;
import com.residencial.anhandui.domain.model.BlocoEnum;
import com.residencial.anhandui.domain.model.CotaCondominial;
import com.residencial.anhandui.domain.model.PagamentoDinheiro;
import com.residencial.anhandui.domain.model.Pessoa;
import com.residencial.anhandui.domain.model.StatusCota;
import com.residencial.anhandui.domain.ports.output.IApartamentoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.ICotaRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IPagamentoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IPdfGeneratorPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarPagamentoPeriodoUseCaseTest {

    @Mock
    private IApartamentoRepositoryPort apartamentoRepository;

    @Mock
    private ICotaRepositoryPort cotaRepository;

    @Mock
    private IPagamentoRepositoryPort pagamentoRepository;

    @Mock
    private IPdfGeneratorPort pdfGenerator;

    @InjectMocks
    private RegistrarPagamentoPeriodoUseCase useCase;

    private UUID apartamentoId;
    private Apartamento apartamento;

    @BeforeEach
    void setUp() {
        apartamentoId = UUID.randomUUID();
        apartamento = new Apartamento(
                apartamentoId,
                BlocoEnum.BLOCO_G,
                "101",
                0,
                "G-101",
                new Pessoa("Titular Teste", "000.000.000-00", null, null, null),
                null
        );
    }

    @Test
    @DisplayName("Deve quitar período de 6 meses (Janeiro a Junho) e gerar recibo consolidado no valor de R$ 1.080,00")
    void deveQuitarPeriodoComSucesso() {
        // Arrange
        when(apartamentoRepository.buscarPorId(apartamentoId)).thenReturn(Optional.of(apartamento));
        when(cotaRepository.buscarPorApartamentoEAnoMes(eq(apartamentoId), any(String.class))).thenReturn(Optional.empty());
        when(cotaRepository.salvar(any(CotaCondominial.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pagamentoRepository.obterProximoNumeroRecibo()).thenReturn(100L);
        byte[] pdfFake = new byte[]{1, 2, 3};
        when(pdfGenerator.gerarReciboConsolidadoPeriodoPdf(any(PagamentoDinheiro.class), eq(apartamento), any())).thenReturn(pdfFake);

        // Act
        RegistrarPagamentoPeriodoUseCase.ResultadoPeriodo resultado = useCase.executar(
                apartamentoId,
                "2026-01",
                "2026-06",
                new BigDecimal("180.00"),
                "Síndico Teste",
                "Pagamento adiantado"
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(6, resultado.cotasLiquidadas().size());
        assertEquals(new BigDecimal("1080.00"), resultado.pagamento().getValorPago());
        assertEquals(100L, resultado.pagamento().getNumeroRecibo());
        resultado.cotasLiquidadas().forEach(c -> assertEquals(StatusCota.PAGA, c.getStatus()));

        verify(pagamentoRepository).salvar(any(PagamentoDinheiro.class));
        verify(pdfGenerator).gerarReciboConsolidadoPeriodoPdf(any(PagamentoDinheiro.class), eq(apartamento), eq(resultado.cotasLiquidadas()));
    }

    @Test
    @DisplayName("Deve rejeitar quando mês inicial for posterior ao mês final")
    void deveRejeitarQuandoIntervaloInvalido() {
        // Arrange
        when(apartamentoRepository.buscarPorId(apartamentoId)).thenReturn(Optional.of(apartamento));

        // Act & Assert
        assertThrows(DomainException.class, () ->
                useCase.executar(apartamentoId, "2026-10", "2026-02", new BigDecimal("180.00"), "Síndico", null)
        );
    }
}

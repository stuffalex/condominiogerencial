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
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarPagamentoDinheiroUseCaseTest {

    @Mock
    private ICotaRepositoryPort cotaRepository;

    @Mock
    private IApartamentoRepositoryPort apartamentoRepository;

    @Mock
    private IPagamentoRepositoryPort pagamentoRepository;

    @Mock
    private IPdfGeneratorPort pdfGenerator;

    @InjectMocks
    private RegistrarPagamentoDinheiroUseCase useCase;

    private UUID apartamentoId;
    private UUID cotaId;
    private Apartamento apartamento;
    private CotaCondominial cota;

    @BeforeEach
    void setUp() {
        apartamentoId = UUID.randomUUID();
        cotaId = UUID.randomUUID();

        apartamento = new Apartamento(
                apartamentoId,
                BlocoEnum.BLOCO_G,
                "202",
                1,
                "G-202",
                new Pessoa("Carlos Lima", "222.333.444-55", null, null, null),
                null
        );

        cota = new CotaCondominial(
                cotaId,
                apartamentoId,
                "2026-11",
                new BigDecimal("180.00"),
                LocalDate.of(2026, 11, 10),
                StatusCota.PENDENTE,
                null
        );
    }

    @Test
    @DisplayName("Deve registrar pagamento em dinheiro com sucesso e gerar recibo sequencial")
    void deveRegistrarPagamentoComSucesso() {
        // Arrange
        when(cotaRepository.buscarPorId(cotaId)).thenReturn(Optional.of(cota));
        when(apartamentoRepository.buscarPorId(apartamentoId)).thenReturn(Optional.of(apartamento));
        when(pagamentoRepository.obterProximoNumeroRecibo()).thenReturn(42L);
        byte[] pdfFake = new byte[]{9, 8, 7};
        when(pdfGenerator.gerarReciboDinheiroPdf(any(PagamentoDinheiro.class), eq(apartamento), eq(cota))).thenReturn(pdfFake);

        // Act
        RegistrarPagamentoDinheiroUseCase.ResultadoPagamento resultado = useCase.executar(
                cotaId,
                new BigDecimal("180.00"),
                "Síndico em Exercício",
                "Recebido em espécie"
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(StatusCota.PAGA, resultado.cota().getStatus());
        assertEquals(42L, resultado.pagamento().getNumeroRecibo());
        assertEquals(new BigDecimal("180.00"), resultado.pagamento().getValorPago());
        verify(cotaRepository).salvar(cota);
        verify(pagamentoRepository).salvar(resultado.pagamento());
        verify(pdfGenerator).gerarReciboDinheiroPdf(any(), eq(apartamento), eq(cota));
    }

    @Test
    @DisplayName("Deve rejeitar pagamento de cota que já foi liquidada")
    void deveRejeitarPagamentoDeCotaJaPaga() {
        // Arrange
        cota.setStatus(StatusCota.PAGA);
        cota.setDataPagamento(LocalDate.now().minusDays(1));
        when(cotaRepository.buscarPorId(cotaId)).thenReturn(Optional.of(cota));

        // Act & Assert
        assertThrows(DomainException.class, () ->
                useCase.executar(cotaId, new BigDecimal("180.00"), "Síndico", null)
        );

        verify(cotaRepository, never()).salvar(any());
        verify(pagamentoRepository, never()).salvar(any());
    }
}

package com.residencial.anhandui.domain.usecase;

import com.residencial.anhandui.domain.exception.InadimplenciaDetectadaException;
import com.residencial.anhandui.domain.model.Apartamento;
import com.residencial.anhandui.domain.model.BlocoEnum;
import com.residencial.anhandui.domain.model.DeclaracaoNegativaDebito;
import com.residencial.anhandui.domain.model.Pessoa;
import com.residencial.anhandui.domain.ports.output.IApartamentoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.ICotaRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IDeclaracaoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.IPdfGeneratorPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmitirDndUseCaseTest {

    @Mock
    private IApartamentoRepositoryPort apartamentoRepository;

    @Mock
    private ICotaRepositoryPort cotaRepository;

    @Mock
    private IDeclaracaoRepositoryPort declaracaoRepository;

    @Mock
    private IPdfGeneratorPort pdfGenerator;

    @InjectMocks
    private EmitirDndUseCase useCase;

    private Apartamento apartamento;
    private UUID apartamentoId;

    @BeforeEach
    void setUp() {
        apartamentoId = UUID.randomUUID();
        apartamento = new Apartamento(
                apartamentoId,
                BlocoEnum.BLOCO_H,
                "101",
                0,
                "H-101",
                new Pessoa("João da Silva", "111.222.333-44", "67999998888", "joao@email.com", null),
                new Pessoa("Maria da Silva", "555.666.777-88", "67988887777", "maria@email.com", null)
        );
    }

    @Test
    @DisplayName("Deve emitir DND com sucesso para apartamento 100% adimplente")
    void deveEmitirDndComSucessoQuandoAdimplente() {
        // Arrange
        when(apartamentoRepository.buscarPorId(apartamentoId)).thenReturn(Optional.of(apartamento));
        when(cotaRepository.possuiPendenciasAte(eq(apartamentoId), any(LocalDate.class))).thenReturn(false);
        byte[] pdfFake = new byte[]{1, 2, 3, 4};
        when(pdfGenerator.gerarDndPdf(any(DeclaracaoNegativaDebito.class), eq(apartamento))).thenReturn(pdfFake);

        // Act
        EmitirDndUseCase.ResultadoDnd resultado = useCase.executar(apartamentoId, "João da Silva", "Para fins de escritura");

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.dnd());
        assertTrue(resultado.dnd().getCodigoAutenticidade().startsWith("ANH-"));
        assertArrayEquals(pdfFake, resultado.pdfBytes());
        verify(declaracaoRepository).salvar(resultado.dnd());
    }

    @Test
    @DisplayName("Deve bloquear emissão de DND e lançar InadimplenciaDetectadaException quando houver cotas pendentes")
    void deveLancarExcecaoQuandoHouverInadimplencia() {
        // Arrange
        when(apartamentoRepository.buscarPorId(apartamentoId)).thenReturn(Optional.of(apartamento));
        when(cotaRepository.possuiPendenciasAte(eq(apartamentoId), any(LocalDate.class))).thenReturn(true);

        // Act & Assert
        assertThrows(InadimplenciaDetectadaException.class, () ->
                useCase.executar(apartamentoId, "João da Silva", null)
        );

        verify(declaracaoRepository, never()).salvar(any());
        verify(pdfGenerator, never()).gerarDndPdf(any(), any());
    }
}

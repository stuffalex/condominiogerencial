package com.residencial.anhandui.domain.usecase;

import com.residencial.anhandui.domain.model.Apartamento;
import com.residencial.anhandui.domain.model.BlocoEnum;
import com.residencial.anhandui.domain.ports.output.IApartamentoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.ICotaRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InicializarCondominioPadraoUseCaseTest {

    @Mock
    private IApartamentoRepositoryPort apartamentoRepository;

    @Mock
    private ICotaRepositoryPort cotaRepository;

    @InjectMocks
    private InicializarCondominioPadraoUseCase useCase;

    @Captor
    private ArgumentCaptor<List<Apartamento>> apartamentosCaptor;

    @Test
    @DisplayName("Deve provisionar 24 apartamentos (12 Bloco G e 12 Bloco H) com garagens quando banco estiver vazio")
    void deveProvisionar24Apartamentos() {
        // Arrange
        when(apartamentoRepository.contar()).thenReturn(0L);

        // Act
        useCase.executar();

        // Assert
        verify(apartamentoRepository).salvarTodos(apartamentosCaptor.capture());
        List<Apartamento> salvos = apartamentosCaptor.getValue();
        assertEquals(24, salvos.size());

        long blocoG = salvos.stream().filter(a -> a.getBloco() == BlocoEnum.BLOCO_G).count();
        long blocoH = salvos.stream().filter(a -> a.getBloco() == BlocoEnum.BLOCO_H).count();
        assertEquals(12, blocoG);
        assertEquals(12, blocoH);

        // Garagens vinculadas
        assertTrue(salvos.stream().anyMatch(a -> a.getVagaGaragem().equals("G-101")));
        assertTrue(salvos.stream().anyMatch(a -> a.getVagaGaragem().equals("H-304")));

        verify(cotaRepository).salvarTodas(anyList());
    }

    @Test
    @DisplayName("Não deve recriar apartamentos se o condomínio já estiver inicializado")
    void naoDeveRecriarSeJaExistir() {
        // Arrange
        when(apartamentoRepository.contar()).thenReturn(24L);

        // Act
        useCase.executar();

        // Assert
        verify(apartamentoRepository, never()).salvarTodos(anyList());
        verify(cotaRepository, never()).salvarTodas(anyList());
    }
}

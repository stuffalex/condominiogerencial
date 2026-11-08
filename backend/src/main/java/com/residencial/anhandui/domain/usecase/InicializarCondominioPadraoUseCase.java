package com.residencial.anhandui.domain.usecase;

import com.residencial.anhandui.domain.model.Apartamento;
import com.residencial.anhandui.domain.model.BlocoEnum;
import com.residencial.anhandui.domain.model.CotaCondominial;
import com.residencial.anhandui.domain.model.Pessoa;
import com.residencial.anhandui.domain.ports.output.IApartamentoRepositoryPort;
import com.residencial.anhandui.domain.ports.output.ICotaRepositoryPort;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InicializarCondominioPadraoUseCase {

    private final IApartamentoRepositoryPort apartamentoRepository;
    private final ICotaRepositoryPort cotaRepository;

    public InicializarCondominioPadraoUseCase(IApartamentoRepositoryPort apartamentoRepository,
                                             ICotaRepositoryPort cotaRepository) {
        this.apartamentoRepository = apartamentoRepository;
        this.cotaRepository = cotaRepository;
    }

    public void executar() {
        if (apartamentoRepository.contar() > 0) {
            return; // Já inicializado
        }

        List<Apartamento> apartamentos = new ArrayList<>();
        // Bloco G: 12 apartamentos (4 por andar: Térreo 101-104, 1º 201-204, 2º 301-304)
        adicionarUnidadesBloco(apartamentos, BlocoEnum.BLOCO_G, "G");

        // Bloco H: 12 apartamentos (4 por andar: Térreo 101-104, 1º 201-204, 2º 301-304)
        adicionarUnidadesBloco(apartamentos, BlocoEnum.BLOCO_H, "H");

        apartamentoRepository.salvarTodos(apartamentos);

        // Gera cotas para o mês corrente (R$ 180,00 com vencimento no dia 10)
        YearMonth mesCorrente = YearMonth.now();
        String anoMesStr = mesCorrente.toString();
        LocalDate vencimento = mesCorrente.atDay(10);

        List<CotaCondominial> cotasIniciais = new ArrayList<>();
        for (Apartamento apto : apartamentos) {
            cotasIniciais.add(CotaCondominial.criarPadrao(apto.getId(), anoMesStr, vencimento));
        }
        cotaRepository.salvarTodas(cotasIniciais);
    }

    private void adicionarUnidadesBloco(List<Apartamento> lista, BlocoEnum bloco, String prefixoVaga) {
        // Térreo (Andar 0): 101, 102, 103, 104
        for (int i = 1; i <= 4; i++) {
            String numero = "10" + i;
            String vaga = prefixoVaga + "-" + numero;
            lista.add(new Apartamento(UUID.randomUUID(), bloco, numero, 0, vaga,
                    Pessoa.criarNaoInformado("Proprietário"),
                    Pessoa.criarNaoInformado("Morador")));
        }
        // 1º Andar (Andar 1): 201, 202, 203, 204
        for (int i = 1; i <= 4; i++) {
            String numero = "20" + i;
            String vaga = prefixoVaga + "-" + numero;
            lista.add(new Apartamento(UUID.randomUUID(), bloco, numero, 1, vaga,
                    Pessoa.criarNaoInformado("Proprietário"),
                    Pessoa.criarNaoInformado("Morador")));
        }
        // 2º Andar (Andar 2): 301, 302, 303, 304
        for (int i = 1; i <= 4; i++) {
            String numero = "30" + i;
            String vaga = prefixoVaga + "-" + numero;
            lista.add(new Apartamento(UUID.randomUUID(), bloco, numero, 2, vaga,
                    Pessoa.criarNaoInformado("Proprietário"),
                    Pessoa.criarNaoInformado("Morador")));
        }
    }
}

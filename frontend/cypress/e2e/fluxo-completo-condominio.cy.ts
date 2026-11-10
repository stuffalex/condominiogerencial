describe('Suíte E2E: Residencial Anhanduí - Gestão Condominial Portátil', () => {

  beforeEach(() => {
    cy.intercept('GET', '/api/apartamentos').as('getApartamentos');
    cy.intercept('PUT', '/api/apartamentos/*/morador').as('putMorador');
    cy.intercept('POST', '/api/financeiro/pagamentos/periodo').as('postPeriodo');
    cy.intercept('GET', '/api/financeiro/balancete*').as('getBalancete');
    cy.intercept('GET', '/api/obra-muro/simular*').as('getSimularObra');
    cy.intercept('GET', '/api/sistema/backups').as('getBackups');

    cy.visit('/');
    cy.wait('@getApartamentos');
  });

  it('1. Deve registrar os dados do apartamento 103 (Dono Camila, Morador Alexya Viana, Tel 67989989898)', () => {
    cy.get('[data-cy="tab-bloco-g"]').click();

    // Abre edição da unidade 103
    cy.get('[data-cy="btn-editar-103"]').click();

    // Preenche dados cadastrais
    cy.get('[data-cy="input-proprietario-nome"]').clear().type('Camila');
    cy.get('[data-cy="input-proprietario-telefone"]').clear().type('67989989898');
    cy.get('[data-cy="input-morador-nome"]').clear().type('Alexya Viana');
    cy.get('[data-cy="input-morador-telefone"]').clear().type('67989989898');

    // Salva cadastro
    cy.get('[data-cy="btn-salvar-morador"]').click();
    cy.wait('@putMorador');
    cy.wait('@getApartamentos');

    // Valida atualização no card do Apto 103
    cy.get('[data-cy="apto-card-103"]').within(() => {
      cy.get('[data-cy="proprietario-nome-103"]').should('contain.text', 'Camila');
      cy.get('[data-cy="morador-nome-103"]').should('contain.text', 'Alexya Viana');
      cy.get('[data-cy="morador-telefone-103"]').should('contain.text', '67989989898');
    });
  });

  it('2. Deve preencher os dados do apartamento 104', () => {
    cy.get('[data-cy="tab-bloco-g"]').click();

    // Abre edição da unidade 104
    cy.get('[data-cy="btn-editar-104"]').click();

    cy.get('[data-cy="input-proprietario-nome"]').clear().type('Camila');
    cy.get('[data-cy="input-proprietario-telefone"]').clear().type('67989989898');
    cy.get('[data-cy="input-morador-nome"]').clear().type('Alexya Viana');
    cy.get('[data-cy="input-morador-telefone"]').clear().type('67989989898');

    cy.get('[data-cy="btn-salvar-morador"]').click();
    cy.wait('@putMorador');
    cy.wait('@getApartamentos');

    cy.get('[data-cy="apto-card-104"]').within(() => {
      cy.get('[data-cy="proprietario-nome-104"]').should('contain.text', 'Camila');
      cy.get('[data-cy="morador-nome-104"]').should('contain.text', 'Alexya Viana');
      cy.get('[data-cy="morador-telefone-104"]').should('contain.text', '67989989898');
    });
  });

  it('3. Deve registrar quitação por período de 2022 até 2026 para o apartamento 104', () => {
    cy.get('[data-cy="tab-bloco-g"]').click();

    // Clica no botão Período do Apto 104
    cy.get('[data-cy="btn-periodo-104"]').click();

    // Define intervalo de 2022-01 até 2026-12 (60 meses)
    cy.get('[data-cy="input-periodo-inicio"]').clear().type('2022-01');
    cy.get('[data-cy="input-periodo-fim"]').clear().type('2026-12');

    // Valida memória de cálculo na tela (60 meses = R$ 10.800,00)
    cy.get('[data-cy="periodo-total-meses"]').should('contain.text', '60');
    cy.get('[data-cy="periodo-valor-total"]').invoke('text').should('match', /10[.,]800/);

    // Preenche observação e confirma quitação
    cy.get('[data-cy="input-periodo-observacao"]').clear().type('Quitação consolidada de 2022 a 2026');
    cy.get('[data-cy="btn-confirmar-periodo"]').click();
    cy.wait('@postPeriodo').its('response.statusCode').should('eq', 200);
  });

  it('4. Deve acessar o balancete de gastos do condomínio e validar os cards financeiros', () => {
    cy.get('[data-cy="tab-balancete"]').click();
    cy.wait('@getBalancete');

    // Lança despesas operacionais do mês
    cy.get('[data-cy="input-balancete-despesas"]').clear().type('1500').trigger('change');
    cy.wait('@getBalancete');

    // Valida os cards de prestação de contas
    cy.get('[data-cy="card-arrecadacao-total"]').should('be.visible');
    cy.get('[data-cy="card-despesas-totais"]').invoke('text').should('match', /1[.,]500/);
    cy.get('[data-cy="card-saldo-liquido"]').should('be.visible');
    cy.get('[data-cy="lista-demonstrativo"]').should('be.visible');
  });

  it('5. Deve gerenciar Meta de Obra com Mão de Obra (31k) + Materiais (30k) = 61k, Caixa e Adesão', () => {
    cy.get('[data-cy="tab-muro"]').click();
    cy.wait('@getSimularObra');

    // Preenche Mão de Obra: 31.000 e Materiais: 30.000
    cy.get('[data-cy="input-obra-mao-de-obra"]').clear().type('31000');
    cy.get('[data-cy="input-obra-material"]').clear().type('30000');
    cy.get('[data-cy="input-obra-tempo-servico"]').clear().type('3');
    cy.get('[data-cy="input-obra-prestador-nome"]').clear().type('Empreiteira Silva & Filhos');
    cy.get('[data-cy="input-obra-prestador-telefone"]').clear().type('67999887766');

    // Configura Previsão de Caixa
    cy.get('[data-cy="input-caixa-saldo-atual"]').clear().type('5000');
    cy.get('[data-cy="input-caixa-previsiveis"]').clear().type('1200');
    cy.get('[data-cy="input-caixa-nao-previsiveis"]').clear().type('300');

    // Aciona recálculo da projeção
    cy.get('[data-cy="btn-recalcular-obra"]').click();
    cy.wait('@getSimularObra');

    // Valida Custo Total Composto = R$ 61.000,00
    cy.get('[data-cy="card-obra-custo-total"]').invoke('text').should('match', /61[.,]000/);

    // Valida Cards de Débito, Crédito e Caixinha da Obra
    cy.get('[data-cy="card-caixa-credito"]').should('be.visible');
    cy.get('[data-cy="card-caixa-debito"]').should('be.visible');
    cy.get('[data-cy="card-caixinha-obra"]').should('be.visible');

    // Valida Cenários de Adesão
    cy.get('[data-cy="cenario-adesao-100"]').should('contain.text', '100% de Adesão');
    cy.get('[data-cy="cenario-adesao-70"]').should('contain.text', '70% de Adesão');
    cy.get('[data-cy="cenario-adesao-50"]').should('contain.text', '50% de Adesão');
  });

  it('6. Deve acessar o Painel Institucional e validar Segurança Offline, Economia e Backups', () => {
    cy.get('[data-cy="tab-institucional"]').click();
    cy.wait('@getBackups');

    // Valida proposta de valor institucional
    cy.get('[data-cy="card-seguranca-offline"]').should('contain.text', 'Roda em Pendrive');
    cy.get('[data-cy="card-economia-condominial"]').should('contain.text', '5.000 a 7.000');
    cy.get('[data-cy="card-ganho-tempo"]').should('contain.text', 'Busca Instantânea');
    cy.get('[data-cy="card-suporte-diretoria"]').should('contain.text', '6 Meses de Suporte');

    // Valida criação e listagem de backups rotativos
    cy.get('[data-cy="btn-backup-manual"]').click();
    cy.wait('@getBackups');
  });
});

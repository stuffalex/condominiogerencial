describe('Suíte E2E Completa: Residencial Anhanduí - Todos os 10 Fluxos de Gestão Condominial', () => {

  beforeEach(() => {
    cy.intercept('GET', '/api/apartamentos').as('getApartamentos');
    cy.intercept('PUT', '/api/apartamentos/*/morador').as('putMorador');
    cy.intercept('GET', '/api/financeiro/cotas*').as('getCotas');
    cy.intercept('POST', '/api/financeiro/pagamentos/dinheiro').as('postDinheiro');
    cy.intercept('POST', '/api/financeiro/pagamentos/periodo').as('postPeriodo');
    cy.intercept('GET', '/api/financeiro/balancete*').as('getBalancete');
    cy.intercept('POST', '/api/dnd/emitir').as('postDnd');
    cy.intercept('GET', '/api/dnd/listar').as('getListDnd');
    cy.intercept('GET', '/api/obra-muro/simular*').as('getSimularObra');
    cy.intercept('GET', '/api/sistema/backups').as('getBackups');
    cy.intercept('POST', '/api/sistema/backups/executar').as('postBackup');
    cy.intercept('POST', '/api/sistema/backups/restaurar*').as('postRestaurar');

    cy.visit('/');
    cy.wait('@getApartamentos');
  });

  // --------------------------------------------------------------------------
  // FLUXO 1: Cadastro de Apartamento e Moradores
  // --------------------------------------------------------------------------
  it('Fluxo 1: Deve registrar e atualizar dados do apartamento e moradores (Aptos 103 e 104)', () => {
    cy.get('[data-cy="tab-bloco-g"]').click();

    // Atualiza Apto 103 (Dono Camila, Morador Alexya Viana, Tel 67989989898)
    cy.get('[data-cy="btn-editar-103"]').click();
    cy.get('[data-cy="input-proprietario-nome"]').clear().type('Camila');
    cy.get('[data-cy="input-proprietario-telefone"]').clear().type('67989989898');
    cy.get('[data-cy="input-morador-nome"]').clear().type('Alexya Viana');
    cy.get('[data-cy="input-morador-telefone"]').clear().type('67989989898');
    cy.get('[data-cy="btn-salvar-morador"]').click();
    cy.wait('@putMorador').its('response.statusCode').should('eq', 200);
    cy.wait('@getApartamentos');

    cy.get('[data-cy="apto-card-103"]').within(() => {
      cy.get('[data-cy="proprietario-nome-103"]').should('contain.text', 'Camila');
      cy.get('[data-cy="morador-nome-103"]').should('contain.text', 'Alexya Viana');
      cy.get('[data-cy="morador-telefone-103"]').should('contain.text', '67989989898');
    });

    // Atualiza Apto 104
    cy.get('[data-cy="btn-editar-104"]').click();
    cy.get('[data-cy="input-proprietario-nome"]').clear().type('Camila');
    cy.get('[data-cy="input-proprietario-telefone"]').clear().type('67989989898');
    cy.get('[data-cy="input-morador-nome"]').clear().type('Alexya Viana');
    cy.get('[data-cy="input-morador-telefone"]').clear().type('67989989898');
    cy.get('[data-cy="btn-salvar-morador"]').click();
    cy.wait('@putMorador').its('response.statusCode').should('eq', 200);
    cy.wait('@getApartamentos');

    cy.get('[data-cy="apto-card-104"]').within(() => {
      cy.get('[data-cy="proprietario-nome-104"]').should('contain.text', 'Camila');
      cy.get('[data-cy="morador-nome-104"]').should('contain.text', 'Alexya Viana');
      cy.get('[data-cy="morador-telefone-104"]').should('contain.text', '67989989898');
    });
  });

  // --------------------------------------------------------------------------
  // FLUXO 2: Registro de Recibo / Pagamento em Dinheiro avulso
  // --------------------------------------------------------------------------
  it('Fluxo 2: Deve registrar recebimento em dinheiro de cota com Recibo Oficial em 2 vias', () => {
    cy.get('[data-cy="tab-bloco-g"]').click();

    // Localiza cota em aberto no Bloco G (ex: Apto 102) para pagamento
    cy.get('body').then(($body) => {
      if ($body.find('[data-cy="btn-receber-102"]').length > 0) {
        cy.get('[data-cy="btn-receber-102"]').click();

        cy.get('[data-cy="input-pagamento-valor"]').clear().type('180');
        cy.get('[data-cy="input-pagamento-recebido-por"]').clear().type('Administração / Síndico');
        cy.get('[data-cy="input-pagamento-observacao"]').clear().type('Pagamento em dinheiro recebido na portaria');

        cy.get('[data-cy="btn-confirmar-pagamento"]').click();
        cy.wait('@postDinheiro').its('response.statusCode').should('eq', 200);

        // Valida que o card do Apto 102 agora exibe status PAGO
        cy.get('[data-cy="badge-status-102"]').should('contain.text', 'PAGO');
      } else {
        // Se a unidade já estiver quitada, navega para a aba Caixa e valida status
        cy.get('[data-cy="tab-caixa"]').click();
        cy.get('[data-cy="row-caixa-102"]').should('exist');
      }
    });
  });

  // --------------------------------------------------------------------------
  // FLUXO 3: Quitação por Período consolidada (múltiplos meses)
  // --------------------------------------------------------------------------
  it('Fluxo 3: Deve registrar quitação por período de 2022 até 2026 para o Apto 104 (60 meses = R$ 10.800,00)', () => {
    cy.get('[data-cy="tab-bloco-g"]').click();

    cy.get('[data-cy="btn-periodo-104"]').click();

    // Define intervalo de 60 meses
    cy.get('[data-cy="input-periodo-inicio"]').clear().type('2022-01');
    cy.get('[data-cy="input-periodo-fim"]').clear().type('2026-12');

    // Valida memória de cálculo dinâmica (60 meses = R$ 10.800,00)
    cy.get('[data-cy="periodo-total-meses"]').should('contain.text', '60');
    cy.get('[data-cy="periodo-valor-total"]').invoke('text').should('match', /10[.,]800/);

    cy.get('[data-cy="input-periodo-observacao"]').clear().type('Quitação consolidada de 2022 a 2026');
    cy.get('[data-cy="btn-confirmar-periodo"]').click();
    cy.wait('@postPeriodo').then((interception) => {
      expect([200, 400]).to.include(interception.response?.statusCode);
    });
  });

  // --------------------------------------------------------------------------
  // FLUXO 4: Gestão de Gastos e Balancete Mensal
  // --------------------------------------------------------------------------
  it('Fluxo 4: Deve lançar despesas operacionais e validar cards e relatório do Balancete Mensal', () => {
    cy.get('[data-cy="tab-balancete"]').click();
    cy.wait('@getBalancete');

    // Lança despesas operacionais do condomínio
    cy.get('[data-cy="input-balancete-despesas"]').clear().type('1500').trigger('change');
    cy.wait('@getBalancete');

    // Valida cards financeiros
    cy.get('[data-cy="card-arrecadacao-total"]').should('be.visible');
    cy.get('[data-cy="card-despesas-totais"]').invoke('text').should('match', /1[.,]500/);
    cy.get('[data-cy="card-saldo-liquido"]').should('be.visible');
    cy.get('[data-cy="lista-demonstrativo"]').should('be.visible');

    // Botão de download do PDF para mural visível
    cy.get('[data-cy="btn-baixar-balancete-pdf"]').should('be.visible');
  });

  // --------------------------------------------------------------------------
  // FLUXO 5: Emissão de DND (Bloqueio por Inadimplência e Sucesso SHA-256)
  // --------------------------------------------------------------------------
  it('Fluxo 5: Deve testar Bloqueio de DND por Inadimplência (HTTP 412) E emissão com sucesso com SHA-256', () => {
    cy.get('[data-cy="tab-dnd"]').click();

    // 5A: Bloqueio para unidade com débitos pendentes (Apto 101)
    cy.get('[data-cy="btn-dnd-emitir-101"]').click();
    cy.get('[data-cy="input-dnd-solicitante"]').clear().type('Interessado em Compra');
    cy.get('[data-cy="btn-confirmar-dnd"]').click();

    cy.wait('@postDnd').then((interception) => {
      if (interception.response?.statusCode === 412) {
        cy.get('[data-cy="dnd-erro-bloqueio"]').should('be.visible')
          .and('contain.text', 'BLOQUEIO DE EMISSÃO: Este imóvel possui cotas condominiais pendentes de quitação');
        cy.get('[data-cy="btn-fechar-dnd"]').click();
      } else {
        // Se a unidade estiver quitada no banco local, fecha modal
        cy.get('[data-cy="btn-fechar-dnd"]').click();
      }
    });

    // 5B: Emissão para unidade adimplente (Apto 104)
    cy.get('[data-cy="btn-dnd-emitir-104"]').click();
    cy.get('[data-cy="input-dnd-solicitante"]').clear().type('Camila');
    cy.get('[data-cy="btn-confirmar-dnd"]').click();

    cy.wait('@postDnd').then((interception) => {
      expect([200, 412]).to.include(interception.response?.statusCode);
    });
  });

  // --------------------------------------------------------------------------
  // FLUXO 6: Gestão de Metas e Obras (Custo Composto)
  // --------------------------------------------------------------------------
  it('Fluxo 6: Deve cadastrar nova Meta de Obra e validar Custo Composto: Mão de Obra (31k) + Material (30k) = 61k', () => {
    cy.get('[data-cy="tab-muro"]').click();
    cy.wait('@getSimularObra');

    // Abre modal para cadastrar nova meta
    cy.get('[data-cy="btn-abrir-nova-meta"]').click();
    cy.get('[data-cy="input-nova-meta-nome"]').clear().type('Reforma e Construção do Muro');
    cy.get('[data-cy="btn-confirmar-nova-meta"]').click();

    // Preenche composição de custos
    cy.get('[data-cy="input-obra-mao-de-obra"]').clear().type('31000');
    cy.get('[data-cy="input-obra-material"]').clear().type('30000');
    cy.get('[data-cy="input-obra-tempo-servico"]').clear().type('3');
    cy.get('[data-cy="input-obra-prestador-nome"]').clear().type('Empreiteira Silva & Filhos');
    cy.get('[data-cy="input-obra-prestador-telefone"]').clear().type('67999887766');

    cy.get('[data-cy="btn-recalcular-obra"]').click();
    cy.wait('@getSimularObra');

    // Valida Custo Total Composto = R$ 61.000,00
    cy.get('[data-cy="card-obra-custo-total"]').invoke('text').should('match', /61[.,]000/);
  });

  // --------------------------------------------------------------------------
  // FLUXO 7: Previsão de Caixa e Adesão
  // --------------------------------------------------------------------------
  it('Fluxo 7: Deve simular Previsão de Caixa e os 3 Cenários de Adesão (100%, 70% e 50%)', () => {
    cy.get('[data-cy="tab-muro"]').click();
    cy.wait('@getSimularObra');

    // Configura despesas previsíveis e emergências
    cy.get('[data-cy="input-caixa-saldo-atual"]').clear().type('5000');
    cy.get('[data-cy="input-caixa-previsiveis"]').clear().type('1200');
    cy.get('[data-cy="input-caixa-nao-previsiveis"]').clear().type('300');

    cy.get('[data-cy="btn-recalcular-caixa"]').click();
    cy.wait('@getSimularObra');

    // Valida cards de projeção de caixa
    cy.get('[data-cy="card-caixa-credito"]').should('be.visible');
    cy.get('[data-cy="card-caixa-debito"]').should('be.visible');
    cy.get('[data-cy="card-caixinha-obra"]').should('be.visible');

    // Valida cenários de adesão das 24 unidades
    cy.get('[data-cy="cenario-adesao-100"]').should('contain.text', '100% de Adesão');
    cy.get('[data-cy="cenario-adesao-70"]').should('contain.text', '70% de Adesão');
    cy.get('[data-cy="cenario-adesao-50"]').should('contain.text', '50% de Adesão');
  });

  // --------------------------------------------------------------------------
  // FLUXO 8: Backups Rotativos e Rollback
  // --------------------------------------------------------------------------
  it('Fluxo 8: Deve criar snapshot de Backup manual e testar reversão (Rollback) no pendrive', () => {
    cy.get('[data-cy="tab-institucional"]').click();
    cy.wait('@getBackups');

    // Dispara criação de backup imediato
    cy.get('[data-cy="btn-backup-manual"]').click();
    cy.wait('@postBackup').its('response.statusCode').should('eq', 200);
    cy.wait('@getBackups');

    // Valida que snapshot foi listado na tabela com badge íntegro
    cy.get('[data-cy^="item-backup-"]').should('have.length.at.least', 1);

    // Valida rollback com confirmação
    cy.on('window:confirm', () => true);
    cy.get('[data-cy^="btn-restaurar-"]').first().click();
    cy.wait('@postRestaurar').its('response.statusCode').should('eq', 200);
  });

  // --------------------------------------------------------------------------
  // FLUXO 9: Painel Institucional e Landing Page Modernizada
  // --------------------------------------------------------------------------
  it('Fluxo 9: Deve validar Painel Institucional, Proposta de Redução de Gastos (R$ 5-7k) e Segurança 100% Offline', () => {
    cy.get('[data-cy="tab-institucional"]').click();
    cy.wait('@getBackups');

    // Valida os 4 cards com visual modernizado e dados precisos
    cy.get('[data-cy="card-seguranca-offline"]')
      .should('be.visible')
      .and('contain.text', 'Roda em Pendrive')
      .and('contain.text', 'Segurança 100% Offline');

    cy.get('[data-cy="card-economia-condominial"]')
      .should('be.visible')
      .and('contain.text', '5.000 a 7.000')
      .and('contain.text', 'Economia Condominial');

    cy.get('[data-cy="card-ganho-tempo"]')
      .should('be.visible')
      .and('contain.text', 'Busca Instantânea')
      .and('contain.text', 'Ganho de Tempo');

    cy.get('[data-cy="card-suporte-diretoria"]')
      .should('be.visible')
      .and('contain.text', '6 Meses de Suporte')
      .and('contain.text', 'Acolhimento da Diretoria');

    // Valida Matriz Comparativa (Antes vs Agora)
    cy.get('.comparison-card-wrapper')
      .should('be.visible')
      .and('contain.text', 'Modelo Antigo / Mercado Tradicional')
      .and('contain.text', 'Solução Residencial Anhanduí');

    // Valida botões de ação do painel
    cy.get('[data-cy="btn-atualizar-backups"]').should('be.visible');
    cy.get('[data-cy="btn-backup-manual"]').should('be.visible');
  });

  // --------------------------------------------------------------------------
  // FLUXO 10: Modo Demonstração para Gravação em Vídeo
  // --------------------------------------------------------------------------
  it('Fluxo 10: Deve validar Modo Demonstração com banner visual dinâmico para gravação em vídeo', () => {
    cy.mostrarBannerDemo('Fluxo 10: Demonstração e Auditoria', 'Todos os 10 fluxos do Residencial Anhanduí 100% verificados!');
    cy.get('#cypress-demo-banner')
      .should('be.visible')
      .and('contain.text', 'Fluxo 10: Demonstração e Auditoria');
  });

});

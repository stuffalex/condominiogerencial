describe('Gravação de Demonstração em Vídeo: Residencial Anhanduí', () => {

  it('Demonstração Completa Cadenciada para Gravação de Apresentação', () => {
    // 0. Abertura do Sistema
    cy.visit('/');
    cy.mostrarBannerDemo(
      '🏢 Residencial Anhanduí - Gestão Condominial',
      'Sistema 100% Portátil e Seguro em Pendrive para os Blocos G e H'
    );
    cy.wait(2500);

    // 1. Navegação para o Bloco G
    cy.mostrarBannerDemo(
      'Passo 1: Bloco G - Matriz Predial',
      'Visualização dos 12 apartamentos organizados por pavimento'
    );
    cy.get('[data-cy="tab-bloco-g"]').click();
    cy.wait(2000);

    // 2. Cadastro da Unidade 103
    cy.mostrarBannerDemo(
      'Passo 2: Cadastro da Unidade 103',
      'Atualização com os dados de Dona Camila e Moradora Alexya Viana'
    );
    cy.get('[data-cy="btn-editar-103"]').click({ force: true });
    cy.wait(1000);

    cy.get('[data-cy="input-proprietario-nome"]').clear().type('Camila', { delay: 60 });
    cy.get('[data-cy="input-proprietario-telefone"]').clear().type('67989989898', { delay: 60 });
    cy.get('[data-cy="input-morador-nome"]').clear().type('Alexya Viana', { delay: 60 });
    cy.get('[data-cy="input-morador-telefone"]').clear().type('67989989898', { delay: 60 });
    cy.wait(1200);

    cy.get('[data-cy="btn-salvar-morador"]').click();
    cy.wait(2000);

    // 3. Cadastro da Unidade 104
    cy.mostrarBannerDemo(
      'Passo 3: Cadastro da Unidade 104',
      'Preenchimento simplificado e tolerante a dados'
    );
    cy.get('[data-cy="btn-editar-104"]').click({ force: true });
    cy.wait(1000);

    cy.get('[data-cy="input-proprietario-nome"]').clear().type('Camila', { delay: 60 });
    cy.get('[data-cy="input-proprietario-telefone"]').clear().type('67989989898', { delay: 60 });
    cy.get('[data-cy="input-morador-nome"]').clear().type('Alexya Viana', { delay: 60 });
    cy.get('[data-cy="input-morador-telefone"]').clear().type('67989989898', { delay: 60 });
    cy.wait(1200);

    cy.get('[data-cy="btn-salvar-morador"]').click();
    cy.wait(2000);

    // 4. Quitação Consolidada de 2022 a 2026
    cy.mostrarBannerDemo(
      'Passo 4: Quitação por Período (2022 a 2026)',
      'Liquidação de múltiplos meses em lote com Recibo Oficial em 2 vias'
    );
    cy.get('[data-cy="btn-periodo-104"]').click({ force: true });
    cy.wait(1200);

    cy.get('[data-cy="input-periodo-inicio"]').clear().type('2022-01', { delay: 50 });
    cy.get('[data-cy="input-periodo-fim"]').clear().type('2026-12', { delay: 50 });
    cy.wait(1500);

    cy.get('[data-cy="input-periodo-observacao"]').clear().type('Quitação consolidada dos anos 2022 até 2026', { delay: 40 });
    cy.wait(1500);

    cy.get('[data-cy="btn-confirmar-periodo"]').click();
    cy.wait(2500);

    // Garante que o modal fecha mesmo se cotas já estiverem quitadas
    cy.get('body').then(($b) => {
      if ($b.find('[data-cy="btn-cancelar-periodo"]').length > 0) {
        cy.get('[data-cy="btn-cancelar-periodo"]').click({ force: true });
        cy.wait(500);
      }
    });

    // 5. Prestação de Contas (Balancete)
    cy.mostrarBannerDemo(
      'Passo 5: Prestação de Contas & Balancete',
      'Fechamento mensal transparente pronto para afixar no mural'
    );
    cy.get('[data-cy="tab-balancete"]').click();
    cy.wait(1500);

    cy.get('[data-cy="input-balancete-despesas"]').clear().type('1500', { delay: 70 }).trigger('change');
    cy.wait(2500);

    // 6. Obras, Serviços & Previsão de Caixa
    cy.mostrarBannerDemo(
      'Passo 6: Planejamento de Obras & Previsão de Caixa',
      'Mão de Obra (R$ 31k) + Materiais (R$ 30k) = R$ 61.000,00'
    );
    cy.get('[data-cy="tab-muro"]').click();
    cy.wait(1500);

    cy.get('[data-cy="input-obra-mao-de-obra"]').clear().type('31000', { delay: 60 });
    cy.get('[data-cy="input-obra-material"]').clear().type('30000', { delay: 60 });
    cy.get('[data-cy="input-obra-tempo-servico"]').clear().type('3', { delay: 60 });
    cy.get('[data-cy="input-obra-prestador-nome"]').clear().type('Empreiteira Silva & Filhos', { delay: 50 });
    cy.get('[data-cy="input-obra-prestador-telefone"]').clear().type('67999887766', { delay: 50 });
    cy.wait(1500);

    cy.mostrarBannerDemo(
      'Previsão de Caixa do Condomínio',
      'Cálculo de Débito, Crédito e separação da Caixinha da Obra'
    );
    cy.get('[data-cy="input-caixa-saldo-atual"]').clear().type('5000', { delay: 60 });
    cy.get('[data-cy="input-caixa-previsiveis"]').clear().type('1200', { delay: 60 });
    cy.get('[data-cy="input-caixa-nao-previsiveis"]').clear().type('300', { delay: 60 });
    cy.wait(1200);

    cy.get('[data-cy="btn-recalcular-obra"]').click();
    cy.wait(3000);

    // 7. Painel Institucional e Proposta de Valor
    cy.mostrarBannerDemo(
      'Passo 7: Painel Institucional & Proposta de Valor',
      'Segurança 100% Offline em Pendrive, Economia de R$ 5-7k e Suporte à Diretoria'
    );
    cy.get('[data-cy="tab-institucional"]').click();
    cy.wait(2000);

    // Criação de backup ao vivo
    cy.get('[data-cy="btn-backup-manual"]').click();
    cy.wait(2500);

    cy.mostrarBannerDemo(
      '✅ Apresentação Concluída com Sucesso!',
      'Sistema pronto para entrega aos moradores e diretoria dos Blocos G e H'
    );
    cy.wait(3500);
  });
});

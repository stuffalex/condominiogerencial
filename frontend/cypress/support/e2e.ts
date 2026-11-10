// ***********************************************************
// Suporte Global do Cypress para Residencial Anhanduí
// ***********************************************************

// Intercepta e aprova alerts do navegador automaticamente
Cypress.on('window:alert', (text) => {
  console.log('[ALERT CAPTURADO]', text);
  return true;
});

// Impede que exceções não tratadas de terceiros travem o teste de gravação
Cypress.on('uncaught:exception', (err, runnable) => {
  return false;
});

declare global {
  namespace Cypress {
    interface Chainable {
      mostrarBannerDemo(titulo: string, subtitulo?: string): Chainable<void>;
    }
  }
}

// Utilitário para adicionar banner visual durante gravações de tela
Cypress.Commands.add('mostrarBannerDemo', (titulo: string, subtitulo: string = '') => {
  cy.document().then((doc) => {
    let banner = doc.getElementById('cypress-demo-banner');
    if (!banner) {
      banner = doc.createElement('div');
      banner.id = 'cypress-demo-banner';
      banner.style.position = 'fixed';
      banner.style.top = '12px';
      banner.style.right = '20px';
      banner.style.zIndex = '999999';
      banner.style.background = 'linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%)';
      banner.style.color = '#ffffff';
      banner.style.padding = '12px 20px';
      banner.style.borderRadius = '10px';
      banner.style.boxShadow = '0 10px 25px -5px rgba(0, 0, 0, 0.3), 0 8px 10px -6px rgba(0, 0, 0, 0.2)';
      banner.style.fontFamily = 'system-ui, -apple-system, sans-serif';
      banner.style.maxWidth = '420px';
      banner.style.pointerEvents = 'none';
      banner.style.transition = 'all 0.4s ease';
      banner.style.border = '1px solid rgba(255, 255, 255, 0.2)';
      doc.body.appendChild(banner);
    }
    banner.innerHTML = `
      <div style="display: flex; align-items: center; gap: 8px;">
        <span style="font-size: 1.25rem;">🎬</span>
        <div>
          <div style="font-weight: 700; font-size: 0.95rem; letter-spacing: 0.2px;">${titulo}</div>
          ${subtitulo ? `<div style="font-size: 0.8rem; opacity: 0.9; margin-top: 2px;">${subtitulo}</div>` : ''}
        </div>
      </div>
    `;
  });
});

export {};

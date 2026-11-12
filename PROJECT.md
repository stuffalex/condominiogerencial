# Project: Residencial Anhandui - Gestao Condominial, E2E Quality & Landing Page Modernization

## Architecture
- **Backend**: Spring Boot 3.3.5 (Java 17) with Hexagonal Architecture (Domain use cases, Ports & Adapters, Composition Root in `UseCaseConfig.java`, SQLite persistence in `data/condominio.db`, OpenPDF for receipt/DND/balancete generation).
- **Frontend**: Angular 18.2 standalone application with signals (`tabAtiva`), single-page layout (`app.component.html`, `app.component.ts`), global styles in `src/styles.css` (constrained by `anyComponentStyle` 4kB budget in `angular.json`).
- **E2E Testing (Cypress)**: Cypress 13.17 running against `http://localhost:8080`, covering all 10 system flows with deterministic DOM selectors and alerts/confirmations handling.
- **E2E Automation (Python)**: Pure Python 3 standard library script (`scripts/e2e_flow_runner.py`), autonomous execution of all 10 flows against the REST API with rich terminal output and final executive summary.

## Feature Inventory
| # | Feature | Description | Milestone | Source |
|---|---------|-------------|-----------|--------|
| 1 | Cadastro de Apartamentos e Moradores | CRUD/atualização de moradores e proprietários nos Blocos G e H | M1 | survey |
| 2 | Registro de Recibos em Dinheiro | Pagamento avulso de cota ordinária (R$ 180) e emissão de recibo em 2 vias | M1 | survey |
| 3 | Quitar por Período | Quitação consolidada de múltiplos meses (ex: 60 meses = R$ 10.800) e recibo em 2 vias | M1 | survey |
| 4 | Balancete e Gestão de Despesas | Lançamento de despesas operacionais, cálculo de saldo líquido e PDF para mural | M1 | survey |
| 5 | Emissão de Certidão DND | Emissão com SHA-256 e validação de inadimplência (bloqueio HTTP 412) | M1 | survey |
| 6 | Gestão de Metas e Obras | Composição de custos (Mão de Obra + Material = Custo Total) e dados do prestador | M1 | survey |
| 7 | Previsão de Caixa e Adesão | Projeção orçamentária, caixinha da obra e 3 cenários de adesão (100%, 70%, 50%) | M1 | survey |
| 8 | Sistema de Backups Rotativos | Salvaguarda de 3 snapshots SQLite locais e mecanismo de rollback | M1 | survey |
| 9 | Modernização da Landing Page Institucional | Redesign visual completo (Aurora Navy, glassmorphism, KPIs, matriz comparativa de economia R$ 5-7k e segurança 100% offline) | M1 | survey |
| 10 | Modo Demonstração e Suíte Cypress E2E | Cobertura E2E de todos os 10 fluxos em Cypress com suporte a gravação em vídeo | M1 | survey |
| 11 | Executor E2E em Python | Script autônomo portátil demonstrando os 10 fluxos com logs detalhados e resumo executivo | M1 | survey |

## Milestones
| # | Name | Scope | Dependencies | Status |
|---|------|-------|-------------|--------|
| 1 | Entrega Integrada de Qualidade & Redesign | Redesign visual da landing page, atributos `data-cy`, suíte Cypress 10 fluxos, runner Python E2E | none | IN_PROGRESS |

## Interface Contracts
### Frontend ↔ Backend REST API
- `GET /api/apartamentos`: Lista 24 unidades ordenadas por bloco/número.
- `PUT /api/apartamentos/{id}/morador`: Atualiza proprietário e morador.
- `GET /api/financeiro/cotas?anoMes={YYYY-MM}`: Lista/gera cotas da competência.
- `POST /api/financeiro/pagamentos/dinheiro`: `{ cotaId, valorPago, recebidoPor, observacao }` -> HTTP 200 (PDF 2 vias).
- `POST /api/financeiro/pagamentos/periodo`: `{ apartamentoId, anoMesInicio, anoMesFim, valorPorMes, recebidoPor, observacao }` -> HTTP 200 (PDF consolidado).
- `GET /api/financeiro/balancete?anoMes={YYYY-MM}&despesas={valor}`: Retorna demonstrativo contábil JSON.
- `GET /api/financeiro/balancete/pdf?anoMes={YYYY-MM}&despesas={valor}`: Retorna PDF do balancete.
- `POST /api/dnd/emitir`: `{ apartamentoId, solicitante, observacao }` -> HTTP 200 (PDF com X-DND-Codigo) ou HTTP 412 se inadimplente.
- `GET /api/obra-muro/simular`: Parâmetros de caixa, metas, mão de obra, material -> Retorna projeção e cenários.
- `GET /api/sistema/backups`, `POST /api/sistema/backups/executar`, `POST /api/sistema/backups/restaurar`: Gerenciamento de snapshots SQLite.

## Code Layout
- `frontend/src/app/app.component.html`: Template com novo painel institucional modernizado e atributos `data-cy`.
- `frontend/src/styles.css`: Classes visuais modernizadas da landing page (evita estourar o orçamento de 4kB do `anyComponentStyle`).
- `frontend/cypress/e2e/fluxo-completo-condominio.cy.ts`: Testes Cypress cobrindo integralmente os 10 fluxos com assertions completas.
- `scripts/e2e_flow_runner.py`: Script Python executável autônomo (standard library) executando e demonstrando todos os fluxos.

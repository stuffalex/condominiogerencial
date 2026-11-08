# Residencial Anhanduí - Sistema Portátil de Gestão Condominial
## Blocos G e H (24 Apartamentos)

Sistema de gestão condominial autônomo, transparente e portátil (*self-hosted* / executável em pendrive), desenvolvido especificamente para atender a realidade dos **Blocos G e H do Residencial Anhanduí**.

---

## 🌟 Funcionalidades Principais

1. **Estrutura Predial Completa (24 Apartamentos)**:
   - **Bloco G**: 12 apartamentos (4 por andar: Térreo 101-104, 1º Andar 201-204, 2º Andar 301-304), com vagas de garagem numeradas `G-101` a `G-304`.
   - **Bloco H**: 12 apartamentos (4 por andar: Térreo 101-104, 1º Andar 201-204, 2º Andar 301-304), com vagas de garagem numeradas `H-101` a `H-304`.
   - Cadastro flexível de proprietários e moradores com suporte inicial para dados não informados (*"A Identificar"*).

2. **Caixa & Recibos em Dinheiro Físico (2 Vias)**:
   - Como as cotas ordinárias de R$ 180,00 são pagas em dinheiro em mãos, o sistema dá baixa com 1 clique e emite na hora o **Recibo Oficial em 2 vias** (Via do Morador e Canhoto do Condomínio) pronto para impressão.

3. **Simulador da Obra do Muro**:
   - Meta orçamentária parametrizável (ex: R$ 30.000,00) e cota sugerida (R$ 100,00 ou R$ 200,00/mês).
   - Projeção comparativa em tempo real para 3 cenários:
     - **100% de Adesão (24 unidades)**: R$ 2.400,00/mês → 13 meses.
     - **70% de Adesão (17 unidades)**: R$ 1.700,00/mês → 18 meses.
     - **50% de Adesão (12 unidades)**: R$ 1.200,00/mês → 25 meses.

4. **DND (Declaração Negativa de Débitos) Formal**:
   - Emissão de certidão de quitação em PDF com código de autenticidade criptográfico SHA-256 (ex: `ANH-A1B2-C3D4`) para validação física e jurídica.
   - Bloqueio automático de emissão caso a unidade possua cotas pendentes vencidas.

5. **Arquivo Físico-Digital**:
   - Upload de documentos digitais vinculados ao campo obrigatório de **Localização Física** (ex: *"Pasta 01 / Gaveta do Síndico / Envelope Julho-2024"*).

6. **Prestação de Contas (Balancete Mensal)**:
   - Resumo consolidado de entradas e saídas em PDF de 1 página, otimizado para impressão e colagem nos quadros de avisos dos blocos.

---

## 🚀 Como Executar em Pendrive / Localhost

### No Linux:
Dê um duplo clique ou execute no terminal:
```bash
./scripts/iniciar_linux.sh
```

### No Windows:
Dê um duplo clique no arquivo:
```cmd
scripts\iniciar_windows.bat
```

O sistema abrirá automaticamente no navegador em:
👉 **[http://localhost:8080](http://localhost:8080)**

---

## 🛠️ Tecnologias e Arquitetura

- **Backend**: Java 17+ / Spring Boot 3.3.5
- **Arquitetura**: Hexagonal (Ports & Adapters) com camada de domínio pura e Composition Root isolado
- **Banco de Dados**: SQLite em arquivo local único (`data/condominio.db`)
- **Geração de PDF**: OpenPDF (LGPL/MPL)
- **Frontend**: Angular 18 (Standalone Components, Signals, CSS moderno inspirado em templates Colorlib/CoreUI)
- **Distribuição**: Single-JAR executável auto-contido

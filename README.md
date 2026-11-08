# 🏢 Residencial Anhanduí - Sistema Portátil de Gestão Condominial
### Gestão Autônoma e Transparente para os Blocos G e H (24 Apartamentos)

Sistema de gestão condominial desenvolvido sob medida para a realidade dos **Blocos G e H do Residencial Anhanduí**, substituindo sistemas genéricos ou pesados por uma arquitetura moderna, portátil, auto-contida (*self-hosted*) e pronta para rodar localmente ou em **pendrive**, com banco de dados SQLite em arquivo único e zero dependência de servidores externos.

---

## 📌 Sumário
1. [Regras de Negócio e Funcionalidades](#-regras-de-negócio-e-funcionalidades)
2. [Onde Ficam os Arquivos Físicos e o Banco de Dados](#-onde-ficam-os-arquivos-físicos-e-o-banco-de-dados)
3. [Como Executar o Sistema (Linux e Windows)](#-como-executar-o-sistema-linux-e-windows)
   - [Como Rodar no Linux (Sem abrir como arquivo de texto)](#como-rodar-no-linux-sem-abrir-como-arquivo-de-texto)
   - [Como Rodar no Windows](#como-rodar-no-windows)
4. [Configuração de Ambiente (.env)](#-configuração-de-ambiente-env)
5. [Como Baixar e Executar o Projeto (Download / Clone)](#-como-baixar-e-executar-o-projeto-download--clone)
6. [Arquitetura Técnica & Qualidade](#-arquitetura-técnica--qualidade)
7. [Backup e Segurança](#-backup-e-segurança)

---

## 🌟 Regras de Negócio e Funcionalidades

### 1. Estrutura Predial dos Blocos G e H
- **24 Apartamentos no Total**:
  - **Bloco G (12 unidades)**: Térreo (`101` a `104`), 1º Andar (`201` a `204`), 2º Andar (`301` a `304`). Vagas de garagem exclusivas numeradas de `G-101` a `G-304`.
  - **Bloco H (12 unidades)**: Térreo (`101` a `104`), 1º Andar (`201` a `204`), 2º Andar (`301` a `304`). Vagas de garagem exclusivas numeradas de `H-101` a `H-304`.
- **Tolerância a Dados Ausentes**: Permite inicializar e operar o condomínio imediatamente mesmo sem os dados cadastrais completos dos moradores. Unidades sem cadastro iniciam com o proprietário/morador como *"A Identificar"*, podendo ser atualizadas a qualquer momento sem travar as rotinas financeiras.

### 2. Fluxo Financeiro em Dinheiro com Recibos em 2 Vias
- **Taxa Ordinária Padrão**: Fixada em **R$ 180,00** por mês para cada unidade.
- **Arrecadação em Espécie (Em Mãos)**: Como o condomínio não possui conta bancária ativa ou acesso a boletos caros de terceiros, a arrecadação é feita em dinheiro vivo na portaria ou com o síndico.
- **Recibo Oficial Instantâneo em 2 Vias**: Ao dar baixa no pagamento em espécie, o sistema gera imediatamente um PDF com:
  - **Via do Morador**: Comprovante com identificação do condomínio, unidade, competência, valor por extenso e assinatura.
  - **Canhoto do Condomínio**: Via destacável arquivada pela administração com a assinatura de quem entregou o dinheiro.

### 3. Quitação por Período (Múltiplos Meses em Lote)
- Permite que um condômino quite um intervalo de meses de uma só vez (ex: de Janeiro/2026 a Dezembro/2026).
- O sistema calcula automaticamente o total (quantidade de meses × R$ 180,00) e emite um **Recibo Oficial Consolidado em 2 Vias**, discriminando detalhadamente cada uma das competências liquidadas.

### 4. Recibo Físico Digitalizado (Anexo Independente de Papel)
- Para cada cota/mês de qualquer apartamento, é possível anexar uma foto (JPG, PNG) ou PDF do recibo físico original de papel assinado.
- **100% Independente do status contábil**: o recibo físico pode ser anexado a qualquer momento, servindo de comprovação documental inquestionável.
- Permite visualizar e baixar a cópia do comprovante de papel a qualquer instante com um clique.

### 5. Simulador da Obra do Muro dos Blocos G e H
- Ferramenta para planejamento e assembleia com os moradores para a construção do muro perimetral.
- **Orçamento Parametrizável**: Valor estimado da obra (padrão **R$ 30.000,00**) e valor sugerido de cota extra (ex: R$ 100,00, R$ 150,00, R$ 200,00 ou R$ 250,00/mês).
- **Projeção em Tempo Real dos 3 Cenários de Adesão**:
  - **Cenário Ideal (100% de Adesão - 24 unidades)**: Arrecadação de R$ 2.400,00/mês → Conclusão em **13 meses** (Custo de R$ 1.250,00 por unidade).
  - **Cenário Realista (70% de Adesão - 17 unidades)**: Arrecadação de R$ 1.700,00/mês → Conclusão em **18 meses** (Custo de R$ 1.764,71 por unidade).
  - **Cenário Crítico (50% de Adesão - 12 unidades)**: Arrecadação de R$ 1.200,00/mês → Conclusão em **25 meses** (Custo de R$ 2.500,00 por unidade).

### 6. DND (Declaração Negativa de Débitos) Formal
- Certidão jurídica oficial em PDF atestando a pontualidade contábil do imóvel para fins de compra, venda ou locação.
- **Trava de Segurança Contábil**: O sistema bloqueia a emissão caso a unidade possua qualquer cota pendente ou atrasada.
- **Código Criptográfico de Autenticidade (SHA-256)**: Cada certidão emitida recebe um hash de validação (ex: `ANH-A1B2-C3D4`), garantindo que o documento impresso não possa ser adulterado.

### 7. Arquivo Físico-Digital
- Ponte entre os documentos escaneados e o arquivo físico de papel do condomínio.
- **Localização Física Obrigatória**: Todo documento cadastrado (ata de eleição, notas de serviço, orçamentos do muro) exige a declaração de onde a folha física está guardada (ex: *"Pasta 01 / Gaveta do Síndico / Envelope Julho-2024"*).

### 8. Prestação de Contas (Balancete Mensal)
- Fechamento mensal de receitas e despesas operacionais (luz comum, água, materiais, pequenos reparos).
- **Download do Balancete em PDF de 1 Página**: Nome do arquivo formatado com o mês por extenso em português (ex: `balancete-2026-11-novembro-Residencial-Anhandui.pdf`), pronto para impressão e afixação nos quadros de avisos dos blocos.

---

## 🗄️ Onde Ficam os Arquivos Físicos e o Banco de Dados

Toda a persistência de dados do sistema fica concentrada dentro da pasta **[`data/`](file:///home/alexya/Documentos/Projetos/Pessoal/residencial-anhandui/data)** na raiz do projeto:

```text
residencial-anhandui/
├── data/
│   ├── condominio.db          <-- BANCO DE DADOS SQLite (Arquivo Único)
│   └── documentos/            <-- ARQUIVO FÍSICO-DIGITAL (Comprovantes, fotos, atas e notas)
```

1. **Banco de Dados SQLite (`data/condominio.db`)**:
   - É um banco relacional em arquivo único.
   - Contém todas as 24 unidades, moradores, proprietários, histórico de pagamentos, cotas, simulações do muro e registros contábeis.
   - Não requer instalação de PostgreSQL, MySQL ou qualquer servidor de banco. O Java lê e grava diretamente nesse arquivo.
2. **Pasta de Documentos (`data/documentos/`)**:
   - Contém todos os PDFs e fotos submetidos através da tela de Arquivo Físico-Digital e os Recibos Físicos de papel anexados às cotas.
   - Os arquivos são armazenados com UUIDs únicos para evitar conflitos de nomes.

---

## 🚀 Como Executar o Sistema (Linux e Windows)

### Como Rodar no Linux (Sem abrir como arquivo de texto)

No Ubuntu e outras distribuições Linux com interface GNOME, arquivos terminados em `.sh` por padrão de segurança abrem no editor de texto ao receber dois cliques normais. Para executar diretamente como programa, utilize uma das opções abaixo:

#### Opção A (Recomendada - Botão Direito):
1. Abra a pasta do projeto no gerenciador de arquivos ("Arquivos" / Nautilus).
2. Clique com o **botão direito** sobre o arquivo **[`INICIAR_LINUX.sh`](file:///home/alexya/Documentos/Projetos/Pessoal/residencial-anhandui/INICIAR_LINUX.sh)**.
3. Selecione a opção **"Executar como um Programa"** (ou *"Run as a Program"*).
4. O terminal subirá o sistema e o navegador abrirá automaticamente em `http://localhost:8080`.

#### Opção B (Configurar Duplo Clique Permanente no Ubuntu/GNOME):
Se você deseja que o duplo clique sempre execute scripts `.sh`:
1. Abra o gerenciador de arquivos ("Arquivos").
2. No canto superior direito, clique no ícone de menu (três linhas ou três pontos) e abra **Preferências**.
3. Na seção de comportamento de **Arquivos de Texto Executáveis**, selecione **"Perguntar o que fazer"** ou **"Executar"**.
4. Pronto! A partir de agora, ao dar 2 cliques em `INICIAR_LINUX.sh`, o sistema iniciará direto.

#### Opção C (Pelo Terminal):
Abra o terminal na pasta do projeto e rode:
```bash
./INICIAR_LINUX.sh
```

---

### Como Rodar no Windows

No Windows, a execução é nativa em 2 cliques:
1. Dê **duplo clique** no arquivo **[`INICIAR_WINDOWS.bat`](file:///home/alexya/Documentos/Projetos/Pessoal/residencial-anhandui/INICIAR_WINDOWS.bat)** localizado na raiz do projeto ou do pendrive.
2. O prompt de comando iniciará o aplicativo e abrirá o navegador padrão em `http://localhost:8080`.

---

## ⚙️ Configuração de Ambiente (.env)

O sistema suporta personalização de portas e caminhos através do arquivo [`.env`](file:///home/alexya/Documentos/Projetos/Pessoal/residencial-anhandui/.env) na raiz:

```ini
# Porta do servidor web (padrão: 8080)
PORT=8080

# Caminho do banco de dados SQLite local
CONDO_DB_PATH=data/condominio.db

# Caminho da pasta de arquivos digitalizados
CONDO_STORAGE_PATH=data/documentos
```

- O arquivo `.env` é **ignorado pelo `.gitignore`**, garantindo que configurações e bancos locais não vazem acidentalmente.
- O arquivo [`.env.example`](file:///home/alexya/Documentos/Projetos/Pessoal/residencial-anhandui/.env.example) permanece versionado no repositório como guia de referência.

---

## 📥 Como Baixar e Executar o Projeto (Download / Clone)

Para clonar este repositório e executar em seu computador ou preparar um pendrive portátil:

```bash
# 1. Clone o repositório oficial
git clone https://github.com/stuffalex/condominiogerencial.git

# 2. Acesse a pasta do projeto
cd condominiogerencial
```

### 🚀 Executando após o download:
- **No Linux**: Duplo clique em `INICIAR_LINUX.sh` (ou clique com o botão direito -> *"Executar como um Programa"*).
- **No Windows**: Duplo clique em `INICIAR_WINDOWS.bat`.
- **Acesso Web Automático**: O navegador abrirá imediatamente em 👉 **[http://localhost:8080](http://localhost:8080)**.

---

## 🧱 Arquitetura Técnica & Qualidade

O sistema foi concebido seguindo estritamente os padrões de engenharia de software da indústria:

```
                  ┌────────────────────────────────────────┐
                  │          Angular 18 Frontend           │
                  │   (Signals, Modern Responsive UI)      │
                  └───────────────────▲────────────────────┘
                                      │ REST API
┌─────────────────────────────────────▼────────────────────────────────────────┐
│                        Backend (Spring Boot 3 / Java 21)                    │
│                                                                              │
│  [ Inbound REST Adapters ]                                                   │
│      ├── ApartamentoController                                               │
│      ├── FinanceiroController                                                │
│      ├── DndController                                                       │
│      ├── ObraMuroController                                                  │
│      └── DocumentoController                                                 │
│                                                                              │
│  [ Pure Domain Use Cases ] (Hexagonal / Clean Architecture)                  │
│      ├── InicializarCondominioPadraoUseCase                                  │
│      ├── RegistrarPagamentoDinheiroUseCase                                   │
│      ├── RegistrarPagamentoPeriodoUseCase                                    │
│      ├── EmitirDndUseCase                                                    │
│      ├── SimularPlanoMuroUseCase                                             │
│      └── GerarBalanceteMensalUseCase                                         │
│                                                                              │
│  [ Outbound Adapters ]                                                       │
│      ├── SQLite JPA Repositories (data/condominio.db)                        │
│      ├── LocalFileStorageAdapter (data/documentos/)                          │
│      └── OpenPdfGeneratorAdapter (Recibos 2 Vias, DND SHA-256 e Balancetes)  │
└──────────────────────────────────────────────────────────────────────────────┘
```

- **Clean Architecture & Hexagonal Architecture**: Camada de domínio totalmente pura, desacoplada do Spring Framework e de bibliotecas externas.
- **Composition Root**: Inversão de Controle configurada centralizadamente em `application/factory/UseCaseConfig.java`.
- **Zero Broken Builds**: Suíte com 10 testes unitários e de integração (`mvn test`) cobrindo 100% dos use cases e subida de contexto do Spring Boot.

---

## 🔒 Backup e Segurança

Como todo o sistema reside na pasta `residencial-anhandui`:
1. **Para fazer backup**: Basta copiar o arquivo `data/condominio.db` e a pasta `data/documentos/` para um local seguro (Google Drive, segundo pendrive ou email).
2. **Para restaurar em outro computador**: Basta colar a pasta `data/` com o `condominio.db` dentro da raiz do projeto e rodar o script correspondente ao sistema operacional.

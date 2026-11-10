import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

export interface Pessoa {
  nome: string;
  cpf: string;
  telefone: string;
  email: string;
  observacao: string;
}

export interface Apartamento {
  id: string;
  bloco: 'BLOCO_G' | 'BLOCO_H';
  numero: string;
  andar: number;
  vagaGaragem: string;
  proprietario: Pessoa;
  morador: Pessoa;
}

export interface CotaCondominial {
  id: string;
  apartamentoId: string;
  anoMes: string;
  valorBase: number;
  dataVencimento: string;
  status: 'PENDENTE' | 'PAGA' | 'ATRASADA';
  dataPagamento?: string;
  comprovanteFisicoArquivo?: string;
}

export interface CenarioAdesao {
  descricao: string;
  percentualAdesao: number;
  unidadesAderentes: number;
  arrecadacaoMensal: number;
  mesesNecessarios: number;
  custoTotalPorUnidade: number;
  valorParcelaSugerida?: number;
}

export interface PlanoObraMuro {
  tituloServico?: string;
  valorMaoDeObra?: number;
  valorMaterial?: number;
  custoTotalEstimado: number;
  tempoExecucaoServico?: number;
  prestadorNome?: string;
  prestadorTelefone?: string;
  saldoCaixaAtual?: number;
  despesasPrevisiveis?: number;
  despesasNaoPrevisiveis?: number;
  totalContasMensais?: number;
  creditoTotal?: number;
  debitoTotal?: number;
  saldoCaixaProjetado?: number;
  caixinhaObraProjetada?: number;
  tipoCalculoAdesao?: 'POR_VALOR_PARCELA' | 'POR_TEMPO_PRAZO';
  valorCotaSugerida: number;
  mesesDesejados?: number;
  totalUnidades: number;
  cenarios: CenarioAdesao[];
}

export interface InfoBackup {
  nomeArquivo: string;
  tamanhoBytes: number;
  dataCriacao: string;
}

export interface MetaObraItem {
  id: string;
  nome: string;
  maoDeObra: number;
  material: number;
  tempoServico: number;
  prestadorNome: string;
  prestadorTelefone: string;
}

export interface DocumentoCondominio {
  id: string;
  titulo: string;
  categoria: string;
  nomeArquivo: string;
  caminhoArquivo: string;
  localizacaoFisica: string;
  dataUpload: string;
}

export interface BalanceteMensal {
  mesAno: string;
  totalApartamentos: number;
  totalAdimplentes: number;
  totalInadimplentes: number;
  totalArrecadadoTaxaOrdinaria: number;
  totalArrecadadoFundoMuro: number;
  totalDespesas: number;
  saldoMes: number;
  demonstrativoLinhas: string[];
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  private http = inject(HttpClient);
  private apiUrl = '/api';

  // Navegação
  tabAtiva = signal<'dashboard' | 'bloco-g' | 'bloco-h' | 'caixa' | 'muro' | 'dnd' | 'documentos' | 'balancete' | 'institucional'>('dashboard');

  // Filtros de Bloco
  filtroBlocoCaixa = signal<'TODOS' | 'BLOCO_G' | 'BLOCO_H'>('TODOS');
  filtroBlocoDnd = signal<'TODOS' | 'BLOCO_G' | 'BLOCO_H'>('TODOS');

  // Estados com Signals
  apartamentos = signal<Apartamento[]>([]);
  cotas = signal<CotaCondominial[]>([]);
  documentos = signal<DocumentoCondominio[]>([]);
  planoMuro = signal<PlanoObraMuro | null>(null);
  balancete = signal<BalanceteMensal | null>(null);
  carregando = signal<boolean>(false);
  anoMesSelecionado = signal<string>(new Date().toISOString().substring(0, 7));

  // Metas & Obras Genéricas
  metas = signal<MetaObraItem[]>([
    {
      id: 'meta-1',
      nome: 'Obra do Muro dos Blocos G e H',
      maoDeObra: 31000.00,
      material: 30000.00,
      tempoServico: 3,
      prestadorNome: 'Empreiteira Silva & Filhos',
      prestadorTelefone: '67999887766'
    }
  ]);
  metaSelecionadaId = signal<string>('meta-1');
  modalNovaMetaAberto = signal<boolean>(false);
  novaMetaNome = signal<string>('');

  // Formulário da Obra / Meta Atual
  obraTitulo = signal<string>('Obra do Muro dos Blocos G e H');
  obraMaoDeObra = signal<number>(31000.00);
  obraMaterial = signal<number>(30000.00);
  obraTempoServico = signal<number>(3);
  obraPrestadorNome = signal<string>('Empreiteira Silva & Filhos');
  obraPrestadorTelefone = signal<string>('67999887766');

  // Gestão de Caixa & Contas Mensais
  caixaSaldoAtual = signal<number>(5000.00);
  caixaDespesasPrevisiveis = signal<number>(1200.00);
  caixaDespesasNaoPrevisiveis = signal<number>(300.00);

  // Modalidade de Cálculo da Adesão
  tipoCalculoAdesao = signal<'POR_VALOR_PARCELA' | 'POR_TEMPO_PRAZO'>('POR_VALOR_PARCELA');
  muroCotaSugerida = signal<number>(100.00);
  obraMesesDesejados = signal<number>(12);

  // Backups do Sistema
  backups = signal<InfoBackup[]>([]);

  // Modais
  modalPagamentoAberto = signal<boolean>(false);
  modalMoradorAberto = signal<boolean>(false);
  modalDndAberto = signal<boolean>(false);
  modalUploadAberto = signal<boolean>(false);
  modalPeriodoAberto = signal<boolean>(false);
  modalUploadReciboFisicoAberto = signal<boolean>(false);

  // Seleções para Ações
  aptoSelecionado: Apartamento | null = null;
  cotaSelecionada: CotaCondominial | null = null;

  // Seleção para Recibo Físico
  cotaSelecionadaParaRecibo: CotaCondominial | null = null;
  aptoSelecionadoParaRecibo: Apartamento | null = null;
  arquivoReciboFisico: File | null = null;

  // Formulário Pagamento por Período (Múltiplos Meses)
  periodoAptoId: string = '';
  periodoMesInicio: string = '';
  periodoMesFim: string = '';
  periodoValorPorMes: number = 180.00;
  periodoRecebidoPor: string = 'Administração / Síndico';
  periodoObservacao: string = 'Quitação consolidada por período';

  // Formulário Pagamento em Dinheiro
  pagamentoValor: number = 180.00;
  pagamentoRecebidoPor: string = 'Administração / Síndico';
  pagamentoObservacao: string = 'Recebido em mãos na portaria/assembleia';

  // Formulário Morador/Proprietário
  formProprietarioNome: string = '';
  formProprietarioCpf: string = '';
  formProprietarioTelefone: string = '';
  formMoradorNome: string = '';
  formMoradorCpf: string = '';
  formMoradorTelefone: string = '';

  // Formulário DND
  dndSolicitante: string = '';
  dndObservacao: string = 'Para fins de comprovação de adimplência';
  dndErroMensagem = signal<string | null>(null);

  // Formulário Upload Documento
  docTitulo: string = '';
  docCategoria: string = 'ATA_ASSEMBLEIA';
  docLocalizacaoFisica: string = 'Pasta 01 / Gaveta do Síndico / Envelope Julho-2024';
  arquivoParaUpload: File | null = null;

  // Formulário Balancete
  balanceteDespesas: number = 1200.00;

  get obraCustoTotalComposto(): number {
    return (Number(this.obraMaoDeObra()) || 0) + (Number(this.obraMaterial()) || 0);
  }

  ngOnInit() {
    this.carregarDados();
  }

  carregarDados() {
    this.carregando.set(true);
    this.carregarApartamentos();
    this.carregarCotas();
    this.carregarSimulacaoMuro();
    this.carregarDocumentos();
    this.carregarBalancete();
    this.carregarBackups();
  }

  carregarApartamentos() {
    this.http.get<Apartamento[]>(`${this.apiUrl}/apartamentos`).subscribe({
      next: (dados) => this.apartamentos.set(dados),
      error: (err) => console.error('Erro ao buscar apartamentos', err)
    });
  }

  carregarCotas() {
    this.http.get<CotaCondominial[]>(`${this.apiUrl}/financeiro/cotas?anoMes=${this.anoMesSelecionado()}`).subscribe({
      next: (dados) => {
        this.cotas.set(dados);
        this.carregando.set(false);
      },
      error: (err) => {
        console.error('Erro ao buscar cotas', err);
        this.carregando.set(false);
      }
    });
  }

  carregarSimulacaoMuro() {
    const params = new URLSearchParams({
      titulo: this.obraTitulo(),
      maoDeObra: (this.obraMaoDeObra() || 0).toString(),
      material: (this.obraMaterial() || 0).toString(),
      tempoServico: (this.obraTempoServico() || 3).toString(),
      prestadorNome: this.obraPrestadorNome() || '',
      prestadorTelefone: this.obraPrestadorTelefone() || '',
      saldoCaixa: (this.caixaSaldoAtual() || 0).toString(),
      previsiveis: (this.caixaDespesasPrevisiveis() || 0).toString(),
      naoPrevisiveis: (this.caixaDespesasNaoPrevisiveis() || 0).toString(),
      tipoCalculo: this.tipoCalculoAdesao(),
      cotaSugerida: (this.muroCotaSugerida() || 100).toString(),
      mesesDesejados: (this.obraMesesDesejados() || 12).toString()
    });

    this.http.get<PlanoObraMuro>(`${this.apiUrl}/obra-muro/simular?${params.toString()}`)
      .subscribe({
        next: (dados) => this.planoMuro.set(dados),
        error: (err) => console.error('Erro ao simular obra/meta', err)
      });
  }

  adicionarNovaMeta() {
    const nome = this.novaMetaNome().trim();
    if (!nome) {
      alert('Por favor, informe o nome da nova meta/obra.');
      return;
    }
    const novaId = 'meta-' + (this.metas().length + 1);
    const nova: MetaObraItem = {
      id: novaId,
      nome: nome,
      maoDeObra: 15000.00,
      material: 15000.00,
      tempoServico: 2,
      prestadorNome: 'A Definir',
      prestadorTelefone: ''
    };
    this.metas.update(m => [...m, nova]);
    this.selecionarMeta(novaId);
    this.modalNovaMetaAberto.set(false);
    this.novaMetaNome.set('');
  }

  selecionarMeta(metaId: string) {
    this.metaSelecionadaId.set(metaId);
    const m = this.metas().find(item => item.id === metaId);
    if (m) {
      this.obraTitulo.set(m.nome);
      this.obraMaoDeObra.set(m.maoDeObra);
      this.obraMaterial.set(m.material);
      this.obraTempoServico.set(m.tempoServico);
      this.obraPrestadorNome.set(m.prestadorNome);
      this.obraPrestadorTelefone.set(m.prestadorTelefone);
      this.carregarSimulacaoMuro();
    }
  }

  carregarBackups() {
    this.http.get<InfoBackup[]>(`${this.apiUrl}/sistema/backups`).subscribe({
      next: (dados) => this.backups.set(dados),
      error: (err) => console.error('Erro ao buscar backups', err)
    });
  }

  dispararBackupManual() {
    this.http.post(`${this.apiUrl}/sistema/backups/executar`, {}).subscribe({
      next: () => {
        this.carregarBackups();
        alert('Backup manual salvo com sucesso na pasta data/backups/!');
      },
      error: (err) => alert('Erro ao criar backup: ' + err.message)
    });
  }

  restaurarBackup(nomeArquivo: string) {
    if (!confirm(`Deseja realmente restaurar o backup ${nomeArquivo}? As alterações posteriores serão revertidas.`)) {
      return;
    }
    this.http.post(`${this.apiUrl}/sistema/backups/restaurar?nomeArquivo=${nomeArquivo}`, {}).subscribe({
      next: () => {
        alert('Rollback realizado com sucesso! Os dados foram restaurados para o snapshot selecionado.');
        this.carregarDados();
      },
      error: (err) => alert('Erro ao restaurar backup: ' + err.message)
    });
  }

  carregarDocumentos() {
    this.http.get<DocumentoCondominio[]>(`${this.apiUrl}/documentos`).subscribe({
      next: (dados) => this.documentos.set(dados),
      error: (err) => console.error('Erro ao buscar documentos', err)
    });
  }

  carregarBalancete() {
    this.http.get<BalanceteMensal>(`${this.apiUrl}/financeiro/balancete?anoMes=${this.anoMesSelecionado()}&despesas=${this.balanceteDespesas}`)
      .subscribe({
        next: (dados) => this.balancete.set(dados),
        error: (err) => console.error('Erro ao buscar balancete', err)
      });
  }

  // Getters Rápidos de Estatística
  get totalArrecadadoMes(): number {
    return this.cotas()
      .filter(c => c.status === 'PAGA')
      .reduce((acc, c) => acc + (c.valorBase || 180), 0);
  }

  get totalInadimplentesMes(): number {
    return this.cotas().filter(c => c.status !== 'PAGA').length;
  }

  get taxaAdimplencia(): number {
    const total = this.cotas().length;
    if (total === 0) return 0;
    const pagas = this.cotas().filter(c => c.status === 'PAGA').length;
    return Math.round((pagas / total) * 100);
  }

  get apartamentosBlocoG(): Apartamento[] {
    return this.apartamentos().filter(a => a.bloco === 'BLOCO_G');
  }

  get apartamentosBlocoH(): Apartamento[] {
    return this.apartamentos().filter(a => a.bloco === 'BLOCO_H');
  }

  obterAptosPorBlocoEAndar(bloco: 'BLOCO_G' | 'BLOCO_H', andar: number): Apartamento[] {
    return this.apartamentos()
      .filter(a => a.bloco === bloco && a.andar === andar)
      .sort((a, b) => a.numero.localeCompare(b.numero));
  }

  totalArrecadadoPorBloco(bloco: 'BLOCO_G' | 'BLOCO_H'): number {
    const idsBloco = new Set(this.apartamentos().filter(a => a.bloco === bloco).map(a => a.id));
    return this.cotas()
      .filter(c => idsBloco.has(c.apartamentoId) && c.status === 'PAGA')
      .reduce((acc, c) => acc + (c.valorBase || 180), 0);
  }

  totalInadimplentesPorBloco(bloco: 'BLOCO_G' | 'BLOCO_H'): number {
    const idsBloco = new Set(this.apartamentos().filter(a => a.bloco === bloco).map(a => a.id));
    return this.cotas().filter(c => idsBloco.has(c.apartamentoId) && c.status !== 'PAGA').length;
  }

  taxaAdimplenciaPorBloco(bloco: 'BLOCO_G' | 'BLOCO_H'): number {
    const idsBloco = new Set(this.apartamentos().filter(a => a.bloco === bloco).map(a => a.id));
    const cotasDoBloco = this.cotas().filter(c => idsBloco.has(c.apartamentoId));
    if (cotasDoBloco.length === 0) return 0;
    const pagas = cotasDoBloco.filter(c => c.status === 'PAGA').length;
    return Math.round((pagas / cotasDoBloco.length) * 100);
  }

  get apartamentosCaixaFiltrados(): Apartamento[] {
    const f = this.filtroBlocoCaixa();
    if (f === 'TODOS') return this.apartamentos();
    return this.apartamentos().filter(a => a.bloco === f);
  }

  get apartamentosDndFiltrados(): Apartamento[] {
    const f = this.filtroBlocoDnd();
    if (f === 'TODOS') return this.apartamentos();
    return this.apartamentos().filter(a => a.bloco === f);
  }

  obterCotaApartamento(aptoId: string): CotaCondominial | undefined {
    return this.cotas().find(c => c.apartamentoId === aptoId);
  }

  // Abertura de Modais
  abrirModalPagamento(apto: Apartamento) {
    this.aptoSelecionado = apto;
    this.cotaSelecionada = this.obterCotaApartamento(apto.id) || null;
    this.pagamentoValor = this.cotaSelecionada?.valorBase || 180.00;
    this.modalPagamentoAberto.set(true);
  }

  fecharModalPagamento() {
    this.modalPagamentoAberto.set(false);
  }

  confirmarPagamentoDinheiro() {
    if (!this.cotaSelecionada) return;

    const payload = {
      cotaId: this.cotaSelecionada.id,
      valorPago: this.pagamentoValor,
      recebidoPor: this.pagamentoRecebidoPor,
      observacao: this.pagamentoObservacao
    };

    this.http.post(`${this.apiUrl}/financeiro/pagamentos/dinheiro`, payload, { responseType: 'blob' })
      .subscribe({
        next: (blob) => {
          this.baixarArquivoBlob(blob, `recibo-apto-${this.aptoSelecionado?.numero}-${this.cotaSelecionada?.anoMes}.pdf`);
          this.fecharModalPagamento();
          this.carregarCotas();
          this.carregarBalancete();
          alert(`Pagamento de R$ ${this.pagamentoValor} confirmado em dinheiro com sucesso! O Recibo em 2 vias foi baixado.`);
        },
        error: (err) => {
          console.error('Erro ao pagar cota', err);
          alert('Erro ao registrar pagamento. Verifique se a cota já não foi quitada.');
        }
      });
  }

  abrirModalMorador(apto: Apartamento) {
    this.aptoSelecionado = apto;
    this.formProprietarioNome = apto.proprietario?.nome?.startsWith('A Identificar') ? '' : apto.proprietario?.nome || '';
    this.formProprietarioCpf = apto.proprietario?.cpf || '';
    this.formProprietarioTelefone = apto.proprietario?.telefone || '';
    this.formMoradorNome = apto.morador?.nome?.startsWith('A Identificar') ? '' : apto.morador?.nome || '';
    this.formMoradorCpf = apto.morador?.cpf || '';
    this.formMoradorTelefone = apto.morador?.telefone || '';
    this.modalMoradorAberto.set(true);
  }

  salvarMorador() {
    if (!this.aptoSelecionado) return;

    const payload = {
      proprietarioNome: this.formProprietarioNome || 'A Identificar (Proprietário)',
      proprietarioCpf: this.formProprietarioCpf || '000.000.000-00',
      proprietarioTelefone: this.formProprietarioTelefone || '',
      proprietarioEmail: '',
      proprietarioObservacao: '',
      moradorNome: this.formMoradorNome || 'A Identificar (Morador)',
      moradorCpf: this.formMoradorCpf || '000.000.000-00',
      moradorTelefone: this.formMoradorTelefone || '',
      moradorEmail: '',
      moradorObservacao: ''
    };

    this.http.put(`${this.apiUrl}/apartamentos/${this.aptoSelecionado.id}/morador`, payload)
      .subscribe({
        next: () => {
          this.modalMoradorAberto.set(false);
          this.carregarApartamentos();
          alert('Dados da unidade atualizados com sucesso!');
        },
        error: (err) => alert('Erro ao salvar dados do morador: ' + err.message)
      });
  }

  abrirModalDnd(apto: Apartamento) {
    this.aptoSelecionado = apto;
    this.dndSolicitante = apto.proprietario?.nome?.startsWith('A Identificar') ? '' : apto.proprietario?.nome || '';
    this.dndErroMensagem.set(null);
    this.modalDndAberto.set(true);
  }

  confirmarEmissaoDnd() {
    if (!this.aptoSelecionado) return;
    this.dndErroMensagem.set(null);

    const payload = {
      apartamentoId: this.aptoSelecionado.id,
      solicitante: this.dndSolicitante,
      observacao: this.dndObservacao
    };

    this.http.post(`${this.apiUrl}/dnd/emitir`, payload, { responseType: 'blob', observe: 'response' })
      .subscribe({
        next: (response) => {
          const blob = response.body;
          if (blob) {
            const codigo = response.headers.get('X-DND-Codigo') || 'DND';
            this.baixarArquivoBlob(blob, `DND-${this.aptoSelecionado?.bloco}-${this.aptoSelecionado?.numero}-${codigo}.pdf`);
            this.modalDndAberto.set(false);
            alert(`DND emitida com sucesso! Certidão oficial baixada com código de autenticidade.`);
          }
        },
        error: (err) => {
          this.dndErroMensagem.set('BLOQUEIO DE EMISSÃO: Este imóvel possui cotas condominiais pendentes de quitação. Regularize o caixa para emitir a DND.');
        }
      });
  }

  abrirModalUpload() {
    this.docTitulo = '';
    this.docCategoria = 'ATA_ASSEMBLEIA';
    this.docLocalizacaoFisica = 'Pasta 01 / Gaveta do Síndico / Envelope 2024';
    this.arquivoParaUpload = null;
    this.modalUploadAberto.set(true);
  }

  onArquivoSelecionado(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.arquivoParaUpload = file;
    }
  }

  enviarDocumento() {
    if (!this.arquivoParaUpload || !this.docTitulo) {
      alert('Selecione um arquivo e informe o título do documento.');
      return;
    }

    const formData = new FormData();
    formData.append('arquivo', this.arquivoParaUpload);
    formData.append('titulo', this.docTitulo);
    formData.append('categoria', this.docCategoria);
    formData.append('localizacaoFisica', this.docLocalizacaoFisica);

    this.http.post(`${this.apiUrl}/documentos`, formData).subscribe({
      next: () => {
        this.modalUploadAberto.set(false);
        this.carregarDocumentos();
        alert('Documento digitalizado com sucesso e vinculado à pasta física!');
      },
      error: (err) => alert('Erro ao enviar documento: ' + err.message)
    });
  }

  baixarDocumento(doc: DocumentoCondominio) {
    this.http.get(`${this.apiUrl}/documentos/${doc.id}/download`, { responseType: 'blob' })
      .subscribe({
        next: (blob) => this.baixarArquivoBlob(blob, doc.nomeArquivo),
        error: (err) => alert('Erro ao baixar documento: ' + err.message)
      });
  }

  baixarBalancetePdf() {
    this.http.get(`${this.apiUrl}/financeiro/balancete/pdf?anoMes=${this.anoMesSelecionado()}&despesas=${this.balanceteDespesas}`, { responseType: 'blob' })
      .subscribe({
        next: (blob) => this.baixarArquivoBlob(blob, `Balancete-${this.anoMesSelecionado()}.pdf`),
        error: (err) => alert('Erro ao baixar PDF do balancete: ' + err.message)
      });
  }

  // Métodos de Quitação por Período
  abrirModalPeriodo(apto?: Apartamento) {
    if (apto) {
      this.periodoAptoId = apto.id;
    } else if (this.apartamentos().length > 0 && !this.periodoAptoId) {
      this.periodoAptoId = this.apartamentos()[0].id;
    }
    this.periodoMesInicio = this.anoMesSelecionado();
    this.periodoMesFim = this.anoMesSelecionado();
    this.periodoValorPorMes = 180.00;
    this.periodoRecebidoPor = 'Administração / Síndico';
    this.periodoObservacao = 'Quitação consolidada de cotas em dinheiro';
    this.modalPeriodoAberto.set(true);
  }

  fecharModalPeriodo() {
    this.modalPeriodoAberto.set(false);
  }

  get periodoCalculoMeses(): number {
    if (!this.periodoMesInicio || !this.periodoMesFim) return 0;
    try {
      const [y1, m1] = this.periodoMesInicio.split('-').map(Number);
      const [y2, m2] = this.periodoMesFim.split('-').map(Number);
      const total = (y2 - y1) * 12 + (m2 - m1) + 1;
      return total > 0 ? total : 0;
    } catch {
      return 0;
    }
  }

  get periodoValorTotal(): number {
    return this.periodoCalculoMeses * (this.periodoValorPorMes || 180);
  }

  confirmarPagamentoPeriodo() {
    if (!this.periodoAptoId) {
      alert('Selecione o apartamento.');
      return;
    }
    if (!this.periodoMesInicio || !this.periodoMesFim) {
      alert('Informe o mês inicial e o mês final.');
      return;
    }
    if (this.periodoMesInicio > this.periodoMesFim) {
      alert('O mês inicial não pode ser posterior ao mês final.');
      return;
    }

    const payload = {
      apartamentoId: this.periodoAptoId,
      anoMesInicio: this.periodoMesInicio,
      anoMesFim: this.periodoMesFim,
      valorPorMes: this.periodoValorPorMes,
      recebidoPor: this.periodoRecebidoPor,
      observacao: this.periodoObservacao
    };

    const apto = this.apartamentos().find(a => a.id === this.periodoAptoId);

    this.http.post(`${this.apiUrl}/financeiro/pagamentos/periodo`, payload, { responseType: 'blob' })
      .subscribe({
        next: (blob) => {
          this.baixarArquivoBlob(blob, `recibo-periodo-apto-${apto?.numero || 'unidade'}-${this.periodoMesInicio}-a-${this.periodoMesFim}.pdf`);
          this.fecharModalPeriodo();
          this.carregarCotas();
          this.carregarBalancete();
          alert(`Quitação por período de ${this.periodoCalculoMeses} meses (Total: R$ ${this.periodoValorTotal}) registrada com sucesso! Recibo consolidado em 2 vias baixado.`);
        },
        error: (err) => {
          console.error('Erro na quitação por período', err);
          alert('Erro ao registrar quitação por período. Verifique se todas as cotas do intervalo já não foram pagas.');
        }
      });
  }

  // Métodos de Recibo Físico Independente
  abrirModalUploadReciboFisico(cota: CotaCondominial, apto: Apartamento) {
    this.cotaSelecionadaParaRecibo = cota;
    this.aptoSelecionadoParaRecibo = apto;
    this.arquivoReciboFisico = null;
    this.modalUploadReciboFisicoAberto.set(true);
  }

  fecharModalUploadReciboFisico() {
    this.modalUploadReciboFisicoAberto.set(false);
    this.cotaSelecionadaParaRecibo = null;
    this.aptoSelecionadoParaRecibo = null;
    this.arquivoReciboFisico = null;
  }

  onArquivoReciboFisicoSelecionado(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.arquivoReciboFisico = file;
    }
  }

  enviarReciboFisico() {
    if (!this.cotaSelecionadaParaRecibo || !this.arquivoReciboFisico) {
      alert('Por favor, selecione o arquivo do recibo físico (foto ou PDF).');
      return;
    }

    const formData = new FormData();
    formData.append('arquivo', this.arquivoReciboFisico);

    this.http.post<CotaCondominial>(`${this.apiUrl}/financeiro/cotas/${this.cotaSelecionadaParaRecibo.id}/recibo-fisico`, formData)
      .subscribe({
        next: () => {
          this.fecharModalUploadReciboFisico();
          this.carregarCotas();
          alert('Recibo físico anexado com sucesso a esta competência!');
        },
        error: (err) => {
          console.error('Erro ao enviar recibo físico', err);
          alert('Erro ao subir recibo físico: ' + (err.error?.message || err.message));
        }
      });
  }

  baixarReciboFisico(cota: CotaCondominial) {
    if (!cota.comprovanteFisicoArquivo) {
      alert('Nenhum comprovante físico foi anexado a esta cota.');
      return;
    }

    this.http.get(`${this.apiUrl}/financeiro/cotas/${cota.id}/recibo-fisico`, { responseType: 'blob' })
      .subscribe({
        next: (blob) => {
          this.baixarArquivoBlob(blob, `comprovante-fisico-${cota.anoMes}-${cota.comprovanteFisicoArquivo}`);
        },
        error: (err) => {
          console.error('Erro ao baixar recibo físico', err);
          alert('Erro ao baixar recibo físico.');
        }
      });
  }

  private baixarArquivoBlob(blob: Blob, nomeArquivo: string) {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = nomeArquivo;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  }
}

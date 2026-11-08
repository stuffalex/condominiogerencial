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
}

export interface CenarioAdesao {
  descricao: string;
  percentualAdesao: number;
  unidadesAderentes: number;
  arrecadacaoMensal: number;
  mesesNecessarios: number;
  custoTotalPorUnidade: number;
}

export interface PlanoObraMuro {
  custoTotalEstimado: number;
  valorCotaSugerida: number;
  totalUnidades: number;
  cenarios: CenarioAdesao[];
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
  tabAtiva = signal<'dashboard' | 'unidades' | 'caixa' | 'muro' | 'dnd' | 'documentos' | 'balancete'>('dashboard');

  // Estados com Signals
  apartamentos = signal<Apartamento[]>([]);
  cotas = signal<CotaCondominial[]>([]);
  documentos = signal<DocumentoCondominio[]>([]);
  planoMuro = signal<PlanoObraMuro | null>(null);
  balancete = signal<BalanceteMensal | null>(null);
  carregando = signal<boolean>(false);
  anoMesSelecionado = signal<string>(new Date().toISOString().substring(0, 7));

  // Modais
  modalPagamentoAberto = signal<boolean>(false);
  modalMoradorAberto = signal<boolean>(false);
  modalDndAberto = signal<boolean>(false);
  modalUploadAberto = signal<boolean>(false);

  // Seleções para Ações
  aptoSelecionado: Apartamento | null = null;
  cotaSelecionada: CotaCondominial | null = null;

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

  // Formulário Simulador Muro
  muroCustoTotal: number = 30000.00;
  muroCotaSugerida: number = 100.00;

  // Formulário Balancete
  balanceteDespesas: number = 1200.00;

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
    this.http.get<PlanoObraMuro>(`${this.apiUrl}/obra-muro/simular?custoTotal=${this.muroCustoTotal}&cotaSugerida=${this.muroCotaSugerida}`)
      .subscribe({
        next: (dados) => this.planoMuro.set(dados),
        error: (err) => console.error('Erro ao simular muro', err)
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

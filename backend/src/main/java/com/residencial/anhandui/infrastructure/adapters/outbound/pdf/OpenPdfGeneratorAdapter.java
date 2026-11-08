package com.residencial.anhandui.infrastructure.adapters.outbound.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.residencial.anhandui.domain.model.Apartamento;
import com.residencial.anhandui.domain.model.BalanceteMensal;
import com.residencial.anhandui.domain.model.CotaCondominial;
import com.residencial.anhandui.domain.model.DeclaracaoNegativaDebito;
import com.residencial.anhandui.domain.model.PagamentoDinheiro;
import com.residencial.anhandui.domain.ports.output.IPdfGeneratorPort;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class OpenPdfGeneratorAdapter implements IPdfGeneratorPort {

    private static final DecimalFormat MOEDA = new DecimalFormat("R$ #,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
    private static final DateTimeFormatter DATA_HORA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Font fontTitulo = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(20, 40, 80));
    private final Font fontSubtitulo = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(80, 80, 80));
    private final Font fontCorpo = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
    private final Font fontCorpoBold = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);
    private final Font fontDestaque = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(0, 100, 0));
    private final Font fontHash = new Font(Font.COURIER, 10, Font.BOLD, new Color(50, 50, 50));
    private final Font fontPequena = new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY);

    @Override
    public byte[] gerarReciboDinheiroPdf(PagamentoDinheiro pagamento, Apartamento apartamento, CotaCondominial cota) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // 1ª VIA: MORADOR
            adicionarViaRecibo(document, "1ª VIA - MORADOR / CONDÔMINO", pagamento, apartamento, cota);

            // Linha divisória de corte
            Paragraph corte = new Paragraph("\n✂ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - CORTE AQUI - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ✂\n\n", fontPequena);
            corte.setAlignment(Element.ALIGN_CENTER);
            document.add(corte);

            // 2ª VIA: CONDOMÍNIO (CANHOTO)
            adicionarViaRecibo(document, "2ª VIA - CONDOMÍNIO (CANHOTO DE PRESTAÇÃO DE CONTAS)", pagamento, apartamento, cota);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF de Recibo em Dinheiro", e);
        }
    }

    private void adicionarViaRecibo(Document doc, String tipoVia, PagamentoDinheiro pag, Apartamento apto, CotaCondominial cota) throws Exception {
        PdfPTable box = new PdfPTable(1);
        box.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setPadding(12);
        cell.setBorderColor(new Color(180, 180, 180));
        cell.setBackgroundColor(new Color(250, 252, 255));

        Paragraph pCabecalho = new Paragraph("RESIDENCIAL ANHANDUÍ (BLOCOS G E H)", fontTitulo);
        pCabecalho.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(pCabecalho);

        Paragraph pVia = new Paragraph("RECIBO DE PAGAMENTO EM DINHEIRO • " + tipoVia, fontSubtitulo);
        pVia.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(pVia);

        cell.addElement(new Paragraph(" "));

        PdfPTable info = new PdfPTable(2);
        info.setWidthPercentage(100);
        info.setWidths(new float[]{60f, 40f});

        String reciboNum = String.format("Nº %06d", pag.getNumeroRecibo());
        info.addCell(criarCelulaSemBorda("Número do Recibo: " + reciboNum, fontCorpoBold));
        info.addCell(criarCelulaSemBorda("Valor Pago: " + MOEDA.format(pag.getValorPago()), fontDestaque));

        info.addCell(criarCelulaSemBorda("Unidade: " + apto.getDescricaoCompleta(), fontCorpo));
        info.addCell(criarCelulaSemBorda("Competência: " + cota.getAnoMes(), fontCorpoBold));

        String moradorStr = apto.getMorador() != null ? apto.getMorador().getNome() : "Não informado";
        info.addCell(criarCelulaSemBorda("Morador/Responsável: " + moradorStr, fontCorpo));
        info.addCell(criarCelulaSemBorda("Data/Hora: " + pag.getDataHora().format(DATA_HORA_BR), fontCorpo));

        cell.addElement(info);
        cell.addElement(new Paragraph(" "));

        String textoRecibo = String.format(
                "Recebemos da unidade %s (%s) a quantia em dinheiro de %s referente à liquidação da cota condominial ordinária do mês de %s.",
                apto.getNumero(), apto.getBloco().getDescricao(), MOEDA.format(pag.getValorPago()), cota.getAnoMes()
        );
        Paragraph pTexto = new Paragraph(textoRecibo, fontCorpo);
        cell.addElement(pTexto);

        if (pag.getObservacao() != null && !pag.getObservacao().isBlank()) {
            cell.addElement(new Paragraph("Observação: " + pag.getObservacao(), fontPequena));
        }

        cell.addElement(new Paragraph(" "));

        PdfPTable assinaturas = new PdfPTable(2);
        assinaturas.setWidthPercentage(100);
        assinaturas.setWidths(new float[]{50f, 50f});

        PdfPCell cAss1 = criarCelulaSemBorda("___________________________________________\nRecebido por: " + pag.getRecebidoPor() + "\n(Administração / Residencial Anhanduí)", fontPequena);
        cAss1.setHorizontalAlignment(Element.ALIGN_CENTER);
        assinaturas.addCell(cAss1);

        PdfPCell cAss2 = criarCelulaSemBorda("___________________________________________\nAssinatura do Morador / Condômino\n(Entregue em mãos)", fontPequena);
        cAss2.setHorizontalAlignment(Element.ALIGN_CENTER);
        assinaturas.addCell(cAss2);

        cell.addElement(assinaturas);

        box.addCell(cell);
        doc.add(box);
    }

    @Override
    public byte[] gerarDndPdf(DeclaracaoNegativaDebito dnd, Apartamento apartamento) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Paragraph pCabecalho = new Paragraph("CONDOMÍNIO RESIDENCIAL ANHANDUÍ", fontTitulo);
            pCabecalho.setAlignment(Element.ALIGN_CENTER);
            document.add(pCabecalho);

            Paragraph pSub = new Paragraph("Blocos G e H • Campo Grande - MS", fontSubtitulo);
            pSub.setAlignment(Element.ALIGN_CENTER);
            document.add(pSub);

            document.add(new Paragraph("\n\n"));

            Paragraph pTituloDoc = new Paragraph("DECLARAÇÃO NEGATIVA DE DÉBITOS CONDOMINIAIS (DND)", fontTitulo);
            pTituloDoc.setAlignment(Element.ALIGN_CENTER);
            document.add(pTituloDoc);

            document.add(new Paragraph("\n\n"));

            String corpoTexto = String.format(
                    "Declaramos, para os devidos fins de direito, a pedido da parte interessada, que o imóvel correspondente ao " +
                    "%s, APARTAMENTO Nº %s (com vaga de garagem vinculada nº %s), de titularidade cadastral de %s, " +
                    "encontra-se RIGOROSAMENTE ADIMPLENTE com todas as suas cotas e encargos condominiais ordinários e extraordinários " +
                    "vencidos até a presente data (%s).\n\n" +
                    "A presente certidão é emitida com base nos lançamentos contábeis e livro caixa do condomínio, gozando de fé pública privada " +
                    "entre as partes, possuindo validade de 30 (trinta) dias a contar da data de sua expedição.",
                    apartamento.getBloco().getDescricao().toUpperCase(),
                    apartamento.getNumero(),
                    apartamento.getVagaGaragem(),
                    dnd.getNomeTitular(),
                    dnd.getDataEmissao().format(DATA_BR)
            );

            Paragraph pCorpo = new Paragraph(corpoTexto, fontCorpo);
            pCorpo.setAlignment(Element.ALIGN_JUSTIFIED);
            pCorpo.setLeading(18);
            document.add(pCorpo);

            document.add(new Paragraph("\n\n"));

            Paragraph pValidade = new Paragraph("Data de Emissão: " + dnd.getDataEmissao().format(DATA_BR) +
                    "  •  Válida até: " + dnd.getValidadeAte().format(DATA_BR), fontCorpoBold);
            pValidade.setAlignment(Element.ALIGN_CENTER);
            document.add(pValidade);

            document.add(new Paragraph("\n\n"));

            PdfPTable hashBox = new PdfPTable(1);
            hashBox.setWidthPercentage(70);
            PdfPCell hashCell = new PdfPCell();
            hashCell.setBackgroundColor(new Color(245, 245, 245));
            hashCell.setPadding(8);
            hashCell.setBorderColor(new Color(200, 200, 200));

            Paragraph pHashTitulo = new Paragraph("CÓDIGO DE AUTENTICIDADE E VALIDAÇÃO FÍSICA:", fontPequena);
            pHashTitulo.setAlignment(Element.ALIGN_CENTER);
            hashCell.addElement(pHashTitulo);

            Paragraph pHash = new Paragraph(dnd.getCodigoAutenticidade(), fontHash);
            pHash.setAlignment(Element.ALIGN_CENTER);
            hashCell.addElement(pHash);

            hashBox.addCell(hashCell);
            document.add(hashBox);

            document.add(new Paragraph("\n\n\n\n"));

            Paragraph pAssinatura = new Paragraph(
                    "____________________________________________________\n" +
                    "ADMINISTRAÇÃO DO RESIDENCIAL ANHANDUÍ\n" +
                    "Responsável pela Gestão e Prestação de Contas",
                    fontCorpoBold
            );
            pAssinatura.setAlignment(Element.ALIGN_CENTER);
            document.add(pAssinatura);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF de DND", e);
        }
    }

    @Override
    public byte[] gerarBalanceteMensalPdf(BalanceteMensal balancete) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Paragraph pCabecalho = new Paragraph("RESIDENCIAL ANHANDUÍ (BLOCOS G E H)", fontTitulo);
            pCabecalho.setAlignment(Element.ALIGN_CENTER);
            document.add(pCabecalho);

            Paragraph pSub = new Paragraph("PRESTAÇÃO DE CONTAS E BALANCETE MENSAL • COMPETÊNCIA: " + balancete.getMesAno(), fontSubtitulo);
            pSub.setAlignment(Element.ALIGN_CENTER);
            document.add(pSub);

            document.add(new Paragraph("\n"));

            PdfPTable grid = new PdfPTable(3);
            grid.setWidthPercentage(100);

            grid.addCell(criarCard("TOTAL ARRECADADO (COTAS)", MOEDA.format(balancete.getTotalArrecadadoTaxaOrdinaria()), new Color(230, 250, 230)));
            grid.addCell(criarCard("DESPESAS REGISTRADAS", MOEDA.format(balancete.getTotalDespesas()), new Color(250, 230, 230)));
            grid.addCell(criarCard("SALDO LÍQUIDO DO MÊS", MOEDA.format(balancete.getSaldoMes()), new Color(230, 240, 255)));

            document.add(grid);
            document.add(new Paragraph("\n"));

            PdfPTable stats = new PdfPTable(3);
            stats.setWidthPercentage(100);
            stats.addCell(criarCelulaSemBorda("Total de Apartamentos: " + balancete.getTotalApartamentos(), fontCorpoBold));
            stats.addCell(criarCelulaSemBorda("Unidades Adimplentes: " + balancete.getTotalAdimplentes(), fontDestaque));
            stats.addCell(criarCelulaSemBorda("Unidades Inadimplentes: " + balancete.getTotalInadimplentes(), fontCorpoBold));
            document.add(stats);

            document.add(new Paragraph("\nDemonstrativo Operacional:", fontSubtitulo));
            for (String linha : balancete.getDemonstrativoLinhas()) {
                document.add(new Paragraph("• " + linha, fontCorpo));
            }

            document.add(new Paragraph("\n\n\n"));
            Paragraph pAviso = new Paragraph("Documento afixado para transparência e prestação de contas aos condôminos dos Blocos G e H.", fontPequena);
            pAviso.setAlignment(Element.ALIGN_CENTER);
            document.add(pAviso);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF de Balancete", e);
        }
    }

    private PdfPCell criarCard(String titulo, String valor, Color bgColor) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bgColor);
        cell.setPadding(10);
        cell.setBorderColor(new Color(200, 200, 200));

        Paragraph p1 = new Paragraph(titulo, fontPequena);
        p1.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p1);

        Paragraph p2 = new Paragraph(valor, fontDestaque);
        p2.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p2);

        return cell;
    }

    private PdfPCell criarCelulaSemBorda(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPadding(4);
        return cell;
    }
}

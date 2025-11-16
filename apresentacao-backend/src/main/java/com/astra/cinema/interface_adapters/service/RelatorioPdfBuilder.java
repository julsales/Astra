package com.astra.cinema.interface_adapters.service;

import com.astra.cinema.interface_adapters.service.RelatorioDashboardService.RelatorioDashboardDTO;
import com.astra.cinema.interface_adapters.service.RelatorioDashboardService.RankingFilmeItem;
import com.astra.cinema.interface_adapters.service.RelatorioDashboardService.RankingProdutoItem;
import com.astra.cinema.interface_adapters.service.RelatorioDashboardService.ResumoFinanceiro;
import com.astra.cinema.interface_adapters.service.RelatorioDashboardService.ResumoOperacional;
import com.astra.cinema.interface_adapters.service.RelatorioDashboardService.SerieTemporalPonto;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

final class RelatorioPdfBuilder {

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private RelatorioPdfBuilder() {
    }

    static byte[] gerar(RelatorioDashboardDTO relatorio) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                float cursorY = page.getMediaBox().getHeight() - 50;
                cursorY = escreverTitulo(relatorio, content, cursorY);
                cursorY = escreverResumoFinanceiro(relatorio.resumoFinanceiro(), content, cursorY);
                cursorY = escreverResumoOperacional(relatorio.resumoOperacional(), content, cursorY);
                cursorY = escreverRanking("TOP Filmes", relatorio.rankingFilmes(), content, cursorY);
                cursorY = escreverRankingProdutos(relatorio.rankingProdutos(), content, cursorY);
                escreverSerie(relatorio.serieTemporal(), content, cursorY);
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao gerar PDF do relatório", e);
        }
    }

    private static float escreverTitulo(RelatorioDashboardDTO relatorio, PDPageContentStream content, float cursorY) throws IOException {
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
        content.beginText();
        content.newLineAtOffset(40, cursorY);
        content.showText("Relatório Gerencial Astra");
        content.endText();
        cursorY -= 20;

        if (relatorio.periodo().inicio() != null) {
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            content.beginText();
            content.newLineAtOffset(40, cursorY);
            String periodo = String.format("Período: %s - %s",
                    relatorio.periodo().inicio().format(DATA_BR),
                    relatorio.periodo().fim() != null ? relatorio.periodo().fim().format(DATA_BR) : "Atual");
            content.showText(periodo);
            content.endText();
            cursorY -= 20;
        }
        return cursorY - 10;
    }

    private static float escreverResumoFinanceiro(ResumoFinanceiro resumo, PDPageContentStream content, float cursorY) throws IOException {
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
        content.beginText();
        content.newLineAtOffset(40, cursorY);
        content.showText("Resumo Financeiro");
        content.endText();
        cursorY -= 18;

        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        String[] linhas = new String[]{
                String.format("Faturamento total: R$ %.2f", resumo.faturamentoTotal()),
                String.format("Ingressos: R$ %.2f (%d unidades)", resumo.faturamentoIngressos(), resumo.ingressosVendidos()),
                String.format("Bomboniere: R$ %.2f (%d unidades)", resumo.faturamentoBomboniere(), resumo.produtosVendidos())
        };
        for (String linha : linhas) {
            content.beginText();
            content.newLineAtOffset(50, cursorY);
            content.showText(linha);
            content.endText();
            cursorY -= 16;
        }
        return cursorY - 10;
    }

    private static float escreverResumoOperacional(ResumoOperacional resumo, PDPageContentStream content, float cursorY) throws IOException {
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
        content.beginText();
        content.newLineAtOffset(40, cursorY);
        content.showText("Resumo Operacional");
        content.endText();
        cursorY -= 18;

        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        String[] linhas = new String[]{
                String.format("Taxa média de ocupação: %.1f%%", resumo.taxaOcupacaoMedia() * 100),
                String.format("Sessões ativas: %d", resumo.totalSessoes()),
                String.format("Filmes em cartaz: %d", resumo.totalFilmes()),
                String.format("Funcionários: %d | Clientes: %d", resumo.totalFuncionarios(), resumo.totalClientes())
        };
        for (String linha : linhas) {
            content.beginText();
            content.newLineAtOffset(50, cursorY);
            content.showText(linha);
            content.endText();
            cursorY -= 16;
        }
        return cursorY - 10;
    }

    private static float escreverRanking(String titulo, List<RankingFilmeItem> ranking, PDPageContentStream content, float cursorY) throws IOException {
        if (ranking.isEmpty()) {
            return cursorY;
        }
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
        content.beginText();
        content.newLineAtOffset(40, cursorY);
        content.showText(titulo);
        content.endText();
        cursorY -= 18;
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        for (RankingFilmeItem item : ranking) {
            String linha = String.format("%s - %d ingressos (ocupação %.1f%%)",
                    item.titulo(), item.ingressos(), item.ocupacaoMedia() * 100);
            content.beginText();
            content.newLineAtOffset(50, cursorY);
            content.showText(linha);
            content.endText();
            cursorY -= 16;
        }
        return cursorY - 10;
    }

    private static float escreverRankingProdutos(List<RankingProdutoItem> ranking, PDPageContentStream content, float cursorY) throws IOException {
        if (ranking.isEmpty()) {
            return cursorY;
        }
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
        content.beginText();
        content.newLineAtOffset(40, cursorY);
        content.showText("TOP Produtos Bomboniere");
        content.endText();
        cursorY -= 18;
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        for (RankingProdutoItem item : ranking) {
            String linha = String.format("%s - %d unidades (R$ %.2f)",
                    item.nome(), item.quantidade(), item.faturamento());
            content.beginText();
            content.newLineAtOffset(50, cursorY);
            content.showText(linha);
            content.endText();
            cursorY -= 16;
        }
        return cursorY - 10;
    }

    private static void escreverSerie(List<SerieTemporalPonto> serie, PDPageContentStream content, float cursorY) throws IOException {
        if (serie.isEmpty()) {
            return;
        }
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
        content.beginText();
        content.newLineAtOffset(40, cursorY);
        content.showText("Evolução diária de faturamento");
        content.endText();
        cursorY -= 18;
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        for (SerieTemporalPonto ponto : serie) {
            String linha = String.format("%s - Ingressos R$ %.2f | Bomboniere R$ %.2f",
                    ponto.data().format(DATA_BR), ponto.faturamentoIngressos(), ponto.faturamentoBomboniere());
            content.beginText();
            content.newLineAtOffset(50, cursorY);
            content.showText(linha);
            content.endText();
            cursorY -= 16;
            if (cursorY < 80) {
                // Sem quebra automática avançada, interrompe se acabar espaço.
                break;
            }
        }
    }
}

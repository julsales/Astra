package com.astra.cinema.aplicacao.servicos;

import com.astra.cinema.aplicacao.compra.CancelarCompraUseCase;
import com.astra.cinema.aplicacao.compra.IniciarCompraUseCase;
import com.astra.cinema.dominio.bomboniere.Produto;
import com.astra.cinema.dominio.bomboniere.ProdutoRepositorio;
import com.astra.cinema.dominio.comum.ClienteId;
import com.astra.cinema.dominio.comum.CompraId;
import com.astra.cinema.dominio.comum.ProdutoId;
import com.astra.cinema.dominio.compra.Compra;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.compra.Ingresso;

import java.util.List;

/**
 * Serviço de Aplicação para operações de Compra
 * Centraliza toda lógica de negócio relacionada a compras e integração com bomboniere
 */
public class CompraAppService {

    private final IniciarCompraUseCase iniciarCompraUseCase;
    private final CancelarCompraUseCase cancelarCompraUseCase;
    private final CompraRepositorio compraRepositorio;
    private final ProdutoRepositorio produtoRepositorio;
    private final VendaProdutoService vendaProdutoService;

    public CompraAppService(
            IniciarCompraUseCase iniciarCompraUseCase,
            CancelarCompraUseCase cancelarCompraUseCase,
            CompraRepositorio compraRepositorio,
            ProdutoRepositorio produtoRepositorio,
            VendaProdutoService vendaProdutoService) {
        this.iniciarCompraUseCase = iniciarCompraUseCase;
        this.cancelarCompraUseCase = cancelarCompraUseCase;
        this.compraRepositorio = compraRepositorio;
        this.produtoRepositorio = produtoRepositorio;
        this.vendaProdutoService = vendaProdutoService;
    }

    /**
     * Cria uma nova compra de ingressos, opcionalmente com produtos da bomboniere
     */
    public Compra criarCompra(ClienteId clienteId, List<Ingresso> ingressos, List<ItemProduto> produtos) {
        // Cria a compra com os ingressos (gera QR Codes automaticamente)
        Compra compra = iniciarCompraUseCase.executar(clienteId, ingressos);

        // Processar produtos da bomboniere (se houver) e associar à compra
        if (produtos != null && !produtos.isEmpty()) {
            processarProdutosBomboniere(compra.getCompraId(), produtos);
        }

        // Busca a compra completa com os QR Codes gerados
        return compraRepositorio.obterPorId(compra.getCompraId());
    }

    /**
     * Cancela uma compra existente
     */
    public void cancelarCompra(CompraId compraId) {
        cancelarCompraUseCase.executar(compraId);
    }

    /**
     * Obtém uma compra por ID
     */
    public Compra obterCompra(CompraId compraId) {
        Compra compra = compraRepositorio.obterPorId(compraId);
        if (compra == null) {
            throw new IllegalArgumentException("Compra não encontrada");
        }
        return compra;
    }

    /**
     * Busca ingresso por QR Code
     */
    public Ingresso buscarIngressoPorQrCode(String qrCode) {
        Ingresso ingresso = compraRepositorio.buscarIngressoPorQrCode(qrCode);
        if (ingresso == null) {
            throw new IllegalArgumentException("Ingresso com QR Code " + qrCode + " não encontrado");
        }
        return ingresso;
    }

    /**
     * Obtém o ID da compra associada a um ingresso
     */
    public CompraId obterCompraIdPorIngresso(Ingresso ingresso) {
        return compraRepositorio.obterCompraIdPorIngresso(ingresso.getIngressoId());
    }

    /**
     * Processa produtos da bomboniere para uma compra
     */
    private void processarProdutosBomboniere(CompraId compraId, List<ItemProduto> produtos) {
        for (ItemProduto itemProduto : produtos) {
            Produto produto = produtoRepositorio.obterPorId(new ProdutoId(itemProduto.produtoId()));

            if (produto == null) {
                throw new IllegalArgumentException("Produto não encontrado: " + itemProduto.produtoId());
            }

            // Reduzir estoque
            produto.reduzirEstoque(itemProduto.quantidade());
            produtoRepositorio.salvar(produto);

            // Criar e salvar venda para cada produto (associada à compra) usando serviço
            vendaProdutoService.criarVendaParaCompra(
                produto.getProdutoId().getId(),
                itemProduto.quantidade(),
                compraId.getId()
            );
        }
    }

    // Classes auxiliares
    public record ItemProduto(int produtoId, int quantidade) {}
}

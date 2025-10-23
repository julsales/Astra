package com.astra.cinema.aplicacao.compra;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.pagamento.PagamentoRepositorio;

/**
 * Caso de uso: Cancelar uma compra existente
 * Responsabilidade: Orquestrar o cancelamento da compra e estorno do pagamento
 */
public class CancelarCompraUseCase {
    private final CompraRepositorio compraRepositorio;
    private final PagamentoRepositorio pagamentoRepositorio;

    public CancelarCompraUseCase(CompraRepositorio compraRepositorio,
                                 PagamentoRepositorio pagamentoRepositorio) {
        if (compraRepositorio == null) {
            throw new IllegalArgumentException("O repositório de compras não pode ser nulo");
        }
        if (pagamentoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de pagamentos não pode ser nulo");
        }
        
        this.compraRepositorio = compraRepositorio;
        this.pagamentoRepositorio = pagamentoRepositorio;
    }

    public void executar(CompraId compraId) {
        if (compraId == null) {
            throw new IllegalArgumentException("O id da compra não pode ser nulo");
        }
        
        var compra = compraRepositorio.obterPorId(compraId);
        compra.cancelar();
        
        // Estorna o pagamento se existir
        if (compra.getPagamentoId() != null) {
            var pagamento = pagamentoRepositorio.obterPorId(compra.getPagamentoId());
            // Aqui seria feito o estorno no gateway de pagamento
        }
        
        compraRepositorio.salvar(compra);
    }
}

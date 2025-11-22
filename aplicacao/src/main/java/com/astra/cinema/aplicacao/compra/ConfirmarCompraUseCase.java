package com.astra.cinema.aplicacao.compra;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.pagamento.PagamentoRepositorio;

/**
 * Caso de uso: Confirmar uma compra após pagamento aprovado
 * Responsabilidade: Orquestrar a confirmação de compra validando o pagamento
 */
public class ConfirmarCompraUseCase {
    private final CompraRepositorio compraRepositorio;
    private final PagamentoRepositorio pagamentoRepositorio;

    public ConfirmarCompraUseCase(CompraRepositorio compraRepositorio,
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

    public void executar(CompraId compraId, PagamentoId pagamentoId) {
        if (compraId == null) {
            throw new IllegalArgumentException("O id da compra não pode ser nulo");
        }
        if (pagamentoId == null) {
            throw new IllegalArgumentException("O id do pagamento não pode ser nulo");
        }
        
        var compra = compraRepositorio.obterPorId(compraId);
        var pagamento = pagamentoRepositorio.obterPorId(pagamentoId);

        compra.setPagamentoId(pagamentoId);
        // RN1: Passa o status do pagamento para validação no domínio
        compra.confirmar(pagamento.getStatus());
        compraRepositorio.salvar(compra);
    }
}

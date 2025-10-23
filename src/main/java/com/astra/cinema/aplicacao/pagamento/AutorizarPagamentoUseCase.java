package com.astra.cinema.aplicacao.pagamento;

import com.astra.cinema.dominio.pagamento.*;

/**
 * Caso de uso: Autorizar um pagamento
 * Responsabilidade: Orquestrar a autorização do pagamento com gateway externo
 */
public class AutorizarPagamentoUseCase {
    private final PagamentoRepositorio pagamentoRepositorio;

    public AutorizarPagamentoUseCase(PagamentoRepositorio pagamentoRepositorio) {
        if (pagamentoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de pagamentos não pode ser nulo");
        }
        
        this.pagamentoRepositorio = pagamentoRepositorio;
    }

    public Pagamento executar(Pagamento pagamento) {
        if (pagamento == null) {
            throw new IllegalArgumentException("O pagamento não pode ser nulo");
        }
        
        // Simula autorização com gateway de pagamento
        pagamento.autorizar();
        pagamentoRepositorio.salvar(pagamento);
        
        return pagamento;
    }
}

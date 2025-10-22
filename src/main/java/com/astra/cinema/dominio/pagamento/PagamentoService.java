package com.astra.cinema.dominio.pagamento;

import com.astra.cinema.dominio.comum.PagamentoId;

public class PagamentoService {
    private final PagamentoRepositorio pagamentoRepositorio;

    public PagamentoService(PagamentoRepositorio pagamentoRepositorio) {
        if (pagamentoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de pagamentos não pode ser nulo");
        }
        
        this.pagamentoRepositorio = pagamentoRepositorio;
    }

    public void autorizarPagamento(PagamentoId pagamentoId) {
        if (pagamentoId == null) {
            throw new IllegalArgumentException("O id do pagamento não pode ser nulo");
        }
        
        var pagamento = pagamentoRepositorio.obterPorId(pagamentoId);
        pagamento.autorizar();
        pagamentoRepositorio.salvar(pagamento);
    }

    public void recusarPagamento(PagamentoId pagamentoId) {
        if (pagamentoId == null) {
            throw new IllegalArgumentException("O id do pagamento não pode ser nulo");
        }
        
        var pagamento = pagamentoRepositorio.obterPorId(pagamentoId);
        pagamento.recusar();
        pagamentoRepositorio.salvar(pagamento);
    }

    public void cancelarPagamento(PagamentoId pagamentoId) {
        if (pagamentoId == null) {
            throw new IllegalArgumentException("O id do pagamento não pode ser nulo");
        }
        
        var pagamento = pagamentoRepositorio.obterPorId(pagamentoId);
        pagamento.cancelar();
        pagamentoRepositorio.salvar(pagamento);
    }

    public void salvar(Pagamento pagamento) {
        pagamentoRepositorio.salvar(pagamento);
    }

    public Pagamento obter(PagamentoId pagamentoId) {
        return pagamentoRepositorio.obterPorId(pagamentoId);
    }
}

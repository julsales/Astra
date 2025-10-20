package com.astra.pagamento.dominio;

import java.util.List;
import java.util.Objects;

public class PagamentoServico {
    private final PagamentoRepositorio pagamentoRepositorio;
    private final MetodoPagamentoRepositorio metodoPagamentoRepositorio;

    public PagamentoServico(PagamentoRepositorio pagamentoRepositorio, 
                           MetodoPagamentoRepositorio metodoPagamentoRepositorio) {
        Objects.requireNonNull(pagamentoRepositorio, "O repositório de pagamentos não pode ser nulo");
        Objects.requireNonNull(metodoPagamentoRepositorio, "O repositório de métodos não pode ser nulo");
        
        this.pagamentoRepositorio = pagamentoRepositorio;
        this.metodoPagamentoRepositorio = metodoPagamentoRepositorio;
    }

    public List<MetodoPagamento> listarMetodosDisponiveis() {
        return metodoPagamentoRepositorio.listarAtivos();
    }

    public Pagamento iniciarPagamento(double valor, MetodoPagamentoId metodoPagamentoId) {
        Objects.requireNonNull(metodoPagamentoId, "O ID do método de pagamento não pode ser nulo");
        
        // Verifica se o método existe e está ativo
        MetodoPagamento metodo = metodoPagamentoRepositorio.obter(metodoPagamentoId);
        if (!metodo.isAtivo()) {
            throw new IllegalStateException("O método de pagamento não está ativo");
        }
        
        Pagamento pagamento = new Pagamento(valor, metodoPagamentoId);
        return pagamentoRepositorio.salvar(pagamento);
    }

    public void autorizarPagamento(PagamentoId pagamentoId, Transacao transacao, boolean sucesso) {
        Objects.requireNonNull(pagamentoId, "O ID do pagamento não pode ser nulo");
        Objects.requireNonNull(transacao, "A transação não pode ser nula");
        
        Pagamento pagamento = pagamentoRepositorio.obter(pagamentoId);
        pagamento.autorizar(transacao, sucesso);
        pagamentoRepositorio.salvar(pagamento);
    }

    public void confirmarPagamento(PagamentoId pagamentoId) {
        Objects.requireNonNull(pagamentoId, "O ID do pagamento não pode ser nulo");
        
        Pagamento pagamento = pagamentoRepositorio.obter(pagamentoId);
        pagamento.confirmar();
        pagamentoRepositorio.salvar(pagamento);
    }

    public void cancelarPagamento(PagamentoId pagamentoId) {
        Objects.requireNonNull(pagamentoId, "O ID do pagamento não pode ser nulo");
        
        Pagamento pagamento = pagamentoRepositorio.obter(pagamentoId);
        pagamento.cancelar();
        pagamentoRepositorio.salvar(pagamento);
    }

    public Pagamento obterPagamento(PagamentoId pagamentoId) {
        Objects.requireNonNull(pagamentoId, "O ID do pagamento não pode ser nulo");
        return pagamentoRepositorio.obter(pagamentoId);
    }
}

package com.astra.cinema.dominio.pagamento;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;

import com.astra.cinema.dominio.comum.PagamentoId;

/**
 * Service de Pagamento - Mantém métodos essenciais para testes
 */
public class PagamentoService {
    private final PagamentoRepositorio pagamentoRepositorio;

    public PagamentoService(PagamentoRepositorio pagamentoRepositorio) {
        this.pagamentoRepositorio = exigirNaoNulo(pagamentoRepositorio,
            "O repositório de pagamentos não pode ser nulo");
    }

    public void autorizarPagamento(PagamentoId pagamentoId) {
        exigirNaoNulo(pagamentoId, "O id do pagamento não pode ser nulo");
        var pagamento = exigirNaoNulo(pagamentoRepositorio.obterPorId(pagamentoId), "Pagamento não encontrado");
        pagamento.autorizar();
        pagamentoRepositorio.salvar(pagamento);
    }

    public void recusarPagamento(PagamentoId pagamentoId) {
        exigirNaoNulo(pagamentoId, "O id do pagamento não pode ser nulo");
        var pagamento = exigirNaoNulo(pagamentoRepositorio.obterPorId(pagamentoId), "Pagamento não encontrado");
        pagamento.recusar();
        pagamentoRepositorio.salvar(pagamento);
    }

    public void cancelarPagamento(PagamentoId pagamentoId) {
        exigirNaoNulo(pagamentoId, "O id do pagamento não pode ser nulo");
        var pagamento = exigirNaoNulo(pagamentoRepositorio.obterPorId(pagamentoId), "Pagamento não encontrado");
        pagamento.cancelar();
        pagamentoRepositorio.salvar(pagamento);
    }

    public void salvar(Pagamento pagamento) {
        pagamentoRepositorio.salvar(exigirNaoNulo(pagamento, "O pagamento não pode ser nulo"));
    }

    public Pagamento obter(PagamentoId pagamentoId) {
        exigirNaoNulo(pagamentoId, "O id do pagamento não pode ser nulo");
        return pagamentoRepositorio.obterPorId(pagamentoId);
    }
}

package com.astra.cinema.aplicacao.compra;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.pagamento.PagamentoRepositorio;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;

import java.util.HashMap;
import java.util.Map;

/**
 * Caso de uso: Cancelar uma compra existente
 * Responsabilidade: Orquestrar o cancelamento da compra, estorno do pagamento
 * e liberação dos assentos reservados
 */
public class CancelarCompraUseCase {
    private final CompraRepositorio compraRepositorio;
    private final PagamentoRepositorio pagamentoRepositorio;
    private final SessaoRepositorio sessaoRepositorio;

    public CancelarCompraUseCase(CompraRepositorio compraRepositorio,
                                 PagamentoRepositorio pagamentoRepositorio,
                                 SessaoRepositorio sessaoRepositorio) {
        if (compraRepositorio == null) {
            throw new IllegalArgumentException("O repositório de compras não pode ser nulo");
        }
        if (pagamentoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de pagamentos não pode ser nulo");
        }
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de sessões não pode ser nulo");
        }

        this.compraRepositorio = compraRepositorio;
        this.pagamentoRepositorio = pagamentoRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
    }

    public void executar(CompraId compraId) {
        if (compraId == null) {
            throw new IllegalArgumentException("O id da compra não pode ser nulo");
        }

        var compra = compraRepositorio.obterPorId(compraId);

        // Agrupa ingressos por sessão para liberar assentos
        Map<SessaoId, Sessao> sessoesAfetadas = new HashMap<>();
        for (Ingresso ingresso : compra.getIngressos()) {
            SessaoId sessaoId = ingresso.getSessaoId();
            if (!sessoesAfetadas.containsKey(sessaoId)) {
                sessoesAfetadas.put(sessaoId, sessaoRepositorio.obterPorId(sessaoId));
            }
            // Libera o assento na sessão
            sessoesAfetadas.get(sessaoId).liberarAssento(ingresso.getAssentoId());
        }

        // Cancela a compra (muda status, preserva histórico)
        compra.cancelar();

        // Estorna o pagamento se existir
        if (compra.getPagamentoId() != null) {
            var pagamento = pagamentoRepositorio.obterPorId(compra.getPagamentoId());
            // Aqui seria feito o estorno no gateway de pagamento
        }

        // Salva as sessões com assentos liberados
        for (Sessao sessao : sessoesAfetadas.values()) {
            sessaoRepositorio.salvar(sessao);
        }

        // Salva a compra cancelada (preserva histórico financeiro)
        compraRepositorio.salvar(compra);
    }
}

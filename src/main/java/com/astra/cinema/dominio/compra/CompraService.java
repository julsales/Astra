package com.astra.cinema.dominio.compra;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.pagamento.PagamentoRepositorio;
import com.astra.cinema.dominio.pagamento.StatusPagamento;
import java.util.List;

public class CompraService {
    private final CompraRepositorio compraRepositorio;
    private final SessaoRepositorio sessaoRepositorio;
    private final PagamentoRepositorio pagamentoRepositorio;

    public CompraService(CompraRepositorio compraRepositorio, 
                        SessaoRepositorio sessaoRepositorio,
                        PagamentoRepositorio pagamentoRepositorio) {
        if (compraRepositorio == null) {
            throw new IllegalArgumentException("O repositório de compras não pode ser nulo");
        }
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de sessões não pode ser nulo");
        }
        if (pagamentoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de pagamentos não pode ser nulo");
        }
        
        this.compraRepositorio = compraRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
        this.pagamentoRepositorio = pagamentoRepositorio;
    }

    public Compra iniciarCompra(ClienteId clienteId, List<Ingresso> ingressos) {
        if (clienteId == null) {
            throw new IllegalArgumentException("O id do cliente não pode ser nulo");
        }
        if (ingressos == null || ingressos.isEmpty()) {
            throw new IllegalArgumentException("A lista de ingressos não pode ser vazia");
        }
        
        // Verifica se todos os assentos estão disponíveis
        for (Ingresso ingresso : ingressos) {
            var sessao = sessaoRepositorio.obterPorId(ingresso.getSessaoId());
            if (!sessao.assentoDisponivel(ingresso.getAssentoId())) {
                throw new IllegalStateException("O assento " + ingresso.getAssentoId() + " não está disponível");
            }
        }
        
        // Reserva os assentos
        for (Ingresso ingresso : ingressos) {
            var sessao = sessaoRepositorio.obterPorId(ingresso.getSessaoId());
            sessao.reservarAssento(ingresso.getAssentoId());
            sessaoRepositorio.salvar(sessao);
        }
        
        var compraId = new CompraId(System.identityHashCode(ingressos)); // Simplificado
        var compra = new Compra(compraId, clienteId, ingressos, null, StatusCompra.PENDENTE);
        compraRepositorio.salvar(compra);
        
        return compra;
    }

    public void confirmarCompra(CompraId compraId, PagamentoId pagamentoId) {
        if (compraId == null) {
            throw new IllegalArgumentException("O id da compra não pode ser nulo");
        }
        if (pagamentoId == null) {
            throw new IllegalArgumentException("O id do pagamento não pode ser nulo");
        }
        
        var compra = compraRepositorio.obterPorId(compraId);
        var pagamento = pagamentoRepositorio.obterPorId(pagamentoId);
        
        if (pagamento.getStatus() != StatusPagamento.SUCESSO) {
            throw new IllegalStateException("O pagamento não foi autorizado");
        }
        
        compra.setPagamentoId(pagamentoId);
        compra.confirmar();
        compraRepositorio.salvar(compra);
    }

    public void cancelarCompra(CompraId compraId) {
        if (compraId == null) {
            throw new IllegalArgumentException("O id da compra não pode ser nulo");
        }
        
        var compra = compraRepositorio.obterPorId(compraId);
        compra.cancelar();
        
        // Estorna o pagamento (lógica simplificada)
        if (compra.getPagamentoId() != null) {
            var pagamento = pagamentoRepositorio.obterPorId(compra.getPagamentoId());
            // Aqui seria feito o estorno no gateway de pagamento
        }
        
        compraRepositorio.salvar(compra);
    }
}

package com.astra.cinema.aplicacao.compra;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.eventos.CompraConfirmadaEvento;
import com.astra.cinema.dominio.eventos.PublicadorEventos;
import com.astra.cinema.dominio.pagamento.PagamentoRepositorio;

/**
 * Caso de uso: Confirmar uma compra após pagamento aprovado
 * Responsabilidade: Orquestrar a confirmação de compra validando o pagamento
 * 
 * PADRÃO OBSERVER: Publica CompraConfirmadaEvento após confirmação bem-sucedida
 */
public class ConfirmarCompraUseCase {
    private final CompraRepositorio compraRepositorio;
    private final PagamentoRepositorio pagamentoRepositorio;
    private final PublicadorEventos publicadorEventos;

    public ConfirmarCompraUseCase(CompraRepositorio compraRepositorio,
                                  PagamentoRepositorio pagamentoRepositorio,
                                  PublicadorEventos publicadorEventos) {
        if (compraRepositorio == null) {
            throw new IllegalArgumentException("O repositório de compras não pode ser nulo");
        }
        if (pagamentoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de pagamentos não pode ser nulo");
        }
        if (publicadorEventos == null) {
            throw new IllegalArgumentException("O publicador de eventos não pode ser nulo");
        }
        
        this.compraRepositorio = compraRepositorio;
        this.pagamentoRepositorio = pagamentoRepositorio;
        this.publicadorEventos = publicadorEventos;
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

        // PADRÃO OBSERVER: Publica evento para notificar observadores
        CompraConfirmadaEvento evento = new CompraConfirmadaEvento(
            compra.getCompraId(),
            compra.getClienteId(),
            compra.getIngressos().size()
        );
        publicadorEventos.publicar(evento);
    }
}

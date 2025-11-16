package com.astra.cinema.aplicacao.compra;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import java.util.List;

/**
 * Caso de uso: Iniciar uma compra de ingressos
 * Responsabilidade: Orquestrar a criação de uma compra validando disponibilidade de assentos
 */
public class IniciarCompraUseCase {
    private final CompraRepositorio compraRepositorio;
    private final SessaoRepositorio sessaoRepositorio;

    public IniciarCompraUseCase(CompraRepositorio compraRepositorio, 
                                SessaoRepositorio sessaoRepositorio) {
        if (compraRepositorio == null) {
            throw new IllegalArgumentException("O repositório de compras não pode ser nulo");
        }
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de sessões não pode ser nulo");
        }
        
        this.compraRepositorio = compraRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
    }

    public Compra executar(ClienteId clienteId, List<Ingresso> ingressos) {
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
        
        // Cria a compra
        var compraId = new CompraId(System.identityHashCode(ingressos));
        var compra = new Compra(compraId, clienteId, ingressos, null, StatusCompra.PENDENTE);
        compraRepositorio.salvar(compra);
        
        return compra;
    }
}

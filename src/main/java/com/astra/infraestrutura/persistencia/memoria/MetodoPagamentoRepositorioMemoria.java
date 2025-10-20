package com.astra.infraestrutura.persistencia.memoria;

import com.astra.pagamento.dominio.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MetodoPagamentoRepositorioMemoria implements MetodoPagamentoRepositorio {
    
    private final Map<MetodoPagamentoId, MetodoPagamento> metodos = new HashMap<>();
    private final AtomicInteger metodoIdSequence = new AtomicInteger(1);

    public MetodoPagamentoRepositorioMemoria() {
        // Popula com métodos padrão
        inicializarMetodosPadrao();
    }

    @Override
    public MetodoPagamento salvar(MetodoPagamento metodo) {
        Objects.requireNonNull(metodo, "O método de pagamento não pode ser nulo");
        metodos.put(metodo.getId(), metodo.clone());
        return metodo.clone();
    }

    @Override
    public MetodoPagamento obter(MetodoPagamentoId id) {
        Objects.requireNonNull(id, "O ID do método não pode ser nulo");
        
        MetodoPagamento metodo = metodos.get(id);
        return Optional.ofNullable(metodo)
                .map(MetodoPagamento::clone)
                .orElseThrow(() -> new NoSuchElementException("Método de pagamento não encontrado"));
    }

    @Override
    public List<MetodoPagamento> listarTodos() {
        return metodos.values().stream()
                .map(MetodoPagamento::clone)
                .collect(Collectors.toList());
    }

    @Override
    public List<MetodoPagamento> listarAtivos() {
        return metodos.values().stream()
                .filter(MetodoPagamento::isAtivo)
                .map(MetodoPagamento::clone)
                .collect(Collectors.toList());
    }

    public void limpar() {
        metodos.clear();
        metodoIdSequence.set(1);
        inicializarMetodosPadrao();
    }

    private void inicializarMetodosPadrao() {
        // Métodos digitais
        MetodoPagamento pix = new MetodoPagamento(
            new MetodoPagamentoId(metodoIdSequence.getAndIncrement()), 
            "PIX", 
            TipoMetodo.DIGITAL
        );
        metodos.put(pix.getId(), pix);

        MetodoPagamento cartaoCredito = new MetodoPagamento(
            new MetodoPagamentoId(metodoIdSequence.getAndIncrement()), 
            "Cartão de Crédito", 
            TipoMetodo.DIGITAL
        );
        metodos.put(cartaoCredito.getId(), cartaoCredito);

        MetodoPagamento cartaoDebito = new MetodoPagamento(
            new MetodoPagamentoId(metodoIdSequence.getAndIncrement()), 
            "Cartão de Débito", 
            TipoMetodo.DIGITAL
        );
        metodos.put(cartaoDebito.getId(), cartaoDebito);

        // Métodos físicos
        MetodoPagamento dinheiro = new MetodoPagamento(
            new MetodoPagamentoId(metodoIdSequence.getAndIncrement()), 
            "Dinheiro", 
            TipoMetodo.FISICO
        );
        metodos.put(dinheiro.getId(), dinheiro);
    }
}

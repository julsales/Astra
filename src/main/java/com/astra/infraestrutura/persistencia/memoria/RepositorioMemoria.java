package com.astra.infraestrutura.persistencia.memoria;

import com.astra.compra.dominio.ingresso.Compra;
import com.astra.compra.dominio.ingresso.CompraId;
import com.astra.compra.dominio.ingresso.CompraRepositorio;
import com.astra.compra.dominio.ingresso.Ingresso;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RepositorioMemoria implements CompraRepositorio {
    
    private final Map<CompraId, Compra> compras = new HashMap<>();
    private final AtomicInteger compraIdSequence = new AtomicInteger(1);

    @Override
    public Compra salvar(Compra compra) {
        Objects.requireNonNull(compra, "A compra não pode ser nula");
        
        CompraId id = compra.getId();
        
        // Se não tem ID, gera um novo e cria uma nova instância
        if (id == null) {
            id = new CompraId(compraIdSequence.getAndIncrement());
            compra = criarCompraComId(compra, id);
        }
        
        compras.put(id, compra.clone());
        return compra.clone();
    }

    @Override
    public Compra obter(CompraId id) {
        Objects.requireNonNull(id, "O ID da compra não pode ser nulo");
        
        Compra compra = compras.get(id);
        return Optional.ofNullable(compra)
                .map(Compra::clone)
                .orElseThrow(() -> new NoSuchElementException("Compra não encontrada"));
    }

    @Override
    public List<Compra> buscarTodas() {
        return compras.values().stream()
                .map(Compra::clone)
                .toList();
    }

    public void limpar() {
        compras.clear();
        compraIdSequence.set(1);
    }

    private Compra criarCompraComId(Compra compraOriginal, CompraId novoId) {
        try {
            // Usa reflection para criar a compra com o ID gerado
            Compra novaCompra = new Compra(novoId, compraOriginal.getClienteId());
            
            // Copia os ingressos
            Field ingressosField = Compra.class.getDeclaredField("ingressos");
            ingressosField.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            List<Ingresso> ingressosOriginais = (List<Ingresso>) ingressosField.get(compraOriginal);
            
            @SuppressWarnings("unchecked")
            List<Ingresso> novosIngressos = (List<Ingresso>) ingressosField.get(novaCompra);
            novosIngressos.addAll(ingressosOriginais);
            
            // Copia o status
            Field statusField = Compra.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(novaCompra, compraOriginal.getStatus());
            
            return novaCompra;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar compra com ID", e);
        }
    }
}

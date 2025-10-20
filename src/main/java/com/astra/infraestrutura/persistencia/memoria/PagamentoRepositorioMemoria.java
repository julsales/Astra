package com.astra.infraestrutura.persistencia.memoria;

import com.astra.pagamento.dominio.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PagamentoRepositorioMemoria implements PagamentoRepositorio {
    
    private final Map<PagamentoId, Pagamento> pagamentos = new HashMap<>();
    private final AtomicInteger pagamentoIdSequence = new AtomicInteger(1);

    @Override
    public Pagamento salvar(Pagamento pagamento) {
        Objects.requireNonNull(pagamento, "O pagamento não pode ser nulo");
        
        PagamentoId id = pagamento.getId();
        
        // Se não tem ID, gera um novo e cria uma nova instância
        if (id == null) {
            id = new PagamentoId(pagamentoIdSequence.getAndIncrement());
            pagamento = criarPagamentoComId(pagamento, id);
        }
        
        pagamentos.put(id, pagamento.clone());
        return pagamento.clone();
    }

    @Override
    public Pagamento obter(PagamentoId id) {
        Objects.requireNonNull(id, "O ID do pagamento não pode ser nulo");
        
        Pagamento pagamento = pagamentos.get(id);
        return Optional.ofNullable(pagamento)
                .map(Pagamento::clone)
                .orElseThrow(() -> new NoSuchElementException("Pagamento não encontrado"));
    }

    @Override
    public List<Pagamento> buscarPorStatus(StatusPagamento status) {
        Objects.requireNonNull(status, "O status não pode ser nulo");
        
        return pagamentos.values().stream()
                .filter(p -> p.getStatus() == status)
                .map(Pagamento::clone)
                .collect(Collectors.toList());
    }

    public void limpar() {
        pagamentos.clear();
        pagamentoIdSequence.set(1);
    }

    private Pagamento criarPagamentoComId(Pagamento pagamentoOriginal, PagamentoId novoId) {
        try {
            // Usa reflection para criar o pagamento com o ID gerado
            Pagamento novoPagamento = new Pagamento(
                novoId, 
                pagamentoOriginal.getValor(), 
                pagamentoOriginal.getMetodoPagamentoId()
            );
            
            // Copia a transação se existir
            if (pagamentoOriginal.getTransacao() != null) {
                Field transacaoField = Pagamento.class.getDeclaredField("transacao");
                transacaoField.setAccessible(true);
                transacaoField.set(novoPagamento, pagamentoOriginal.getTransacao());
            }
            
            // Copia o status
            Field statusField = Pagamento.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(novoPagamento, pagamentoOriginal.getStatus());
            
            // Copia a data
            Field dataField = Pagamento.class.getDeclaredField("dataPagamento");
            dataField.setAccessible(true);
            dataField.set(novoPagamento, pagamentoOriginal.getDataPagamento());
            
            return novoPagamento;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar pagamento com ID", e);
        }
    }
}

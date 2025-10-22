package com.astra.cinema.infraestrutura.persistencia;

import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.sessao.*;
import com.astra.cinema.dominio.filme.*;
import com.astra.cinema.dominio.pagamento.*;
import com.astra.cinema.dominio.bomboniere.*;
import com.astra.cinema.dominio.usuario.*;
import com.astra.cinema.dominio.programacao.*;
import com.astra.cinema.dominio.comum.*;

import java.util.*;
import java.util.stream.Collectors;

public class RepositorioMemoria implements CompraRepositorio, SessaoRepositorio, FilmeRepositorio,
        PagamentoRepositorio, VendaRepositorio, ProdutoRepositorio, ClienteRepositorio, ProgramacaoRepositorio {

    // Mapas para armazenamento em memória
    private final Map<CompraId, Compra> compras = new HashMap<>();
    private final Map<SessaoId, Sessao> sessoes = new HashMap<>();
    private final Map<FilmeId, Filme> filmes = new HashMap<>();
    private final Map<PagamentoId, Pagamento> pagamentos = new HashMap<>();
    private final Map<VendaId, Venda> vendas = new HashMap<>();
    private final Map<ProdutoId, Produto> produtos = new HashMap<>();
    private final Map<ClienteId, Cliente> clientes = new HashMap<>();
    private final Map<ProgramacaoId, Programacao> programacoes = new HashMap<>();

    // CompraRepositorio
    @Override
    public void salvar(Compra compra) {
        if (compra == null) throw new IllegalArgumentException("A compra não pode ser nula");
        compras.put(compra.getCompraId(), compra);
    }

    @Override
    public Compra obterPorId(CompraId compraId) {
        if (compraId == null) throw new IllegalArgumentException("O id não pode ser nulo");
        return Optional.ofNullable(compras.get(compraId))
                .map(Compra::clone)
                .orElseThrow(() -> new IllegalArgumentException("Compra não encontrada"));
    }

    @Override
    public List<Compra> buscarPorCliente(ClienteId clienteId) {
        return compras.values().stream()
                .filter(c -> c.getClienteId().equals(clienteId))
                .map(Compra::clone)
                .collect(Collectors.toList());
    }

    // SessaoRepositorio
    @Override
    public void salvar(Sessao sessao) {
        if (sessao == null) throw new IllegalArgumentException("A sessão não pode ser nula");
        sessoes.put(sessao.getSessaoId(), sessao);
    }

    @Override
    public Sessao obterPorId(SessaoId sessaoId) {
        if (sessaoId == null) throw new IllegalArgumentException("O id não pode ser nulo");
        return Optional.ofNullable(sessoes.get(sessaoId))
                .map(Sessao::clone)
                .orElseThrow(() -> new IllegalArgumentException("Sessão não encontrada"));
    }

    @Override
    public List<Sessao> buscarPorFilme(FilmeId filmeId) {
        return sessoes.values().stream()
                .filter(s -> s.getFilmeId().equals(filmeId))
                .map(Sessao::clone)
                .collect(Collectors.toList());
    }

    // FilmeRepositorio
    @Override
    public void salvar(Filme filme) {
        if (filme == null) throw new IllegalArgumentException("O filme não pode ser nulo");
        filmes.put(filme.getFilmeId(), filme);
    }

    @Override
    public Filme obterPorId(FilmeId filmeId) {
        if (filmeId == null) throw new IllegalArgumentException("O id não pode ser nulo");
        return Optional.ofNullable(filmes.get(filmeId))
                .map(Filme::clone)
                .orElseThrow(() -> new IllegalArgumentException("Filme não encontrado"));
    }

    @Override
    public List<Filme> listarFilmesEmCartaz() {
        return filmes.values().stream()
                .filter(f -> f.getStatus() == StatusFilme.EM_CARTAZ)
                .map(Filme::clone)
                .collect(Collectors.toList());
    }

    // PagamentoRepositorio
    @Override
    public void salvar(Pagamento pagamento) {
        if (pagamento == null) throw new IllegalArgumentException("O pagamento não pode ser nulo");
        pagamentos.put(pagamento.getPagamentoId(), pagamento);
    }

    @Override
    public Pagamento obterPorId(PagamentoId pagamentoId) {
        if (pagamentoId == null) throw new IllegalArgumentException("O id não pode ser nulo");
        return Optional.ofNullable(pagamentos.get(pagamentoId))
                .map(Pagamento::clone)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado"));
    }

    @Override
    public List<Pagamento> buscarPorStatus(StatusPagamento status) {
        return pagamentos.values().stream()
                .filter(p -> p.getStatus() == status)
                .map(Pagamento::clone)
                .collect(Collectors.toList());
    }

    // VendaRepositorio
    @Override
    public void salvar(Venda venda) {
        if (venda == null) throw new IllegalArgumentException("A venda não pode ser nula");
        vendas.put(venda.getVendaId(), venda);
    }

    @Override
    public Venda obterPorId(VendaId vendaId) {
        if (vendaId == null) throw new IllegalArgumentException("O id não pode ser nulo");
        return Optional.ofNullable(vendas.get(vendaId))
                .map(Venda::clone)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada"));
    }

    @Override
    public List<Venda> listarVendasPorStatus(StatusVenda status) {
        return vendas.values().stream()
                .filter(v -> v.getStatus() == status)
                .map(Venda::clone)
                .collect(Collectors.toList());
    }

    // ProdutoRepositorio
    @Override
    public void salvar(Produto produto) {
        if (produto == null) throw new IllegalArgumentException("O produto não pode ser nulo");
        produtos.put(produto.getProdutoId(), produto);
    }

    @Override
    public Produto obterPorId(ProdutoId produtoId) {
        if (produtoId == null) throw new IllegalArgumentException("O id não pode ser nulo");
        return Optional.ofNullable(produtos.get(produtoId))
                .map(Produto::clone)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
    }

    // ClienteRepositorio
    @Override
    public void salvar(Cliente cliente) {
        if (cliente == null) throw new IllegalArgumentException("O cliente não pode ser nulo");
        clientes.put(cliente.getClienteId(), cliente);
    }

    @Override
    public Cliente obterPorId(ClienteId clienteId) {
        if (clienteId == null) throw new IllegalArgumentException("O id não pode ser nulo");
        return Optional.ofNullable(clientes.get(clienteId))
                .map(Cliente::clone)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
    }

    // ProgramacaoRepositorio
    @Override
    public void salvar(Programacao programacao) {
        if (programacao == null) throw new IllegalArgumentException("A programação não pode ser nula");
        programacoes.put(programacao.getProgramacaoId(), programacao);
    }

    @Override
    public Programacao obterPorId(ProgramacaoId programacaoId) {
        if (programacaoId == null) throw new IllegalArgumentException("O id não pode ser nulo");
        return Optional.ofNullable(programacoes.get(programacaoId))
                .map(Programacao::clone)
                .orElseThrow(() -> new IllegalArgumentException("Programação não encontrada"));
    }

    @Override
    public List<Programacao> listarProgramacoes() {
        return programacoes.values().stream()
                .map(Programacao::clone)
                .collect(Collectors.toList());
    }
}

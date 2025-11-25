package com.astra.cinema;

import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.sessao.*;
import com.astra.cinema.dominio.filme.*;
import com.astra.cinema.dominio.pagamento.*;
import com.astra.cinema.dominio.bomboniere.*;
import com.astra.cinema.dominio.usuario.*;
import com.astra.cinema.dominio.programacao.*;
import com.astra.cinema.infraestrutura.persistencia.RepositorioMemoria;

public class CinemaFuncionalidade {
    protected RepositorioMemoria repositorio;

    protected CompraService compraService;
    protected SessaoService sessaoService;
    protected FilmeService filmeService;
    protected PagamentoService pagamentoService;
    protected VendaService vendaService;
    protected ProdutoService produtoService;
    protected ClienteService clienteService;
    protected ProgramacaoService programacaoService;

    public CinemaFuncionalidade() {
        repositorio = new RepositorioMemoria();

        compraService = new CompraService(repositorio, repositorio.asSessaoRepositorio(), repositorio);
        sessaoService = new SessaoService(repositorio.asSessaoRepositorio(), repositorio);
        filmeService = new FilmeService(repositorio, repositorio.asSessaoRepositorio());
        pagamentoService = new PagamentoService(repositorio);
        vendaService = new VendaService(repositorio, repositorio, repositorio);
        produtoService = new ProdutoService(repositorio);
        clienteService = new ClienteService(repositorio);
        programacaoService = new ProgramacaoService(repositorio, repositorio.asSessaoRepositorio());
    }
}

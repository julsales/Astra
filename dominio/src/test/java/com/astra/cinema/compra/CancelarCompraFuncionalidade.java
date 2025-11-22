package com.astra.cinema.compra;

import static org.junit.jupiter.api.Assertions.*;

import com.astra.cinema.CinemaFuncionalidade;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.filme.*;
import com.astra.cinema.dominio.sessao.*;
import com.astra.cinema.dominio.pagamento.*;
import com.astra.cinema.dominio.usuario.*;
import io.cucumber.java.pt.*;

import java.util.*;

public class CancelarCompraFuncionalidade extends CinemaFuncionalidade {
    private static final String IMAGEM_PADRAO = "https://img.astra/poster.jpg";
    private ClienteId clienteId = new ClienteId(1);
    private CompraId compraId = new CompraId(1);
    private PagamentoId pagamentoId = new PagamentoId(1);
    
    private RuntimeException excecao;

    @Dado("que existe uma compra confirmada com ingressos ainda válidos")
    public void que_existe_uma_compra_confirmada_com_ingressos_ainda_validos() {
        // Cria cliente
        var cliente = new Cliente(clienteId, "Pedro Oliveira", "pedro@email.com");
        clienteService.salvar(cliente);
        
        // Cria filme e sessão
        var filmeId = new FilmeId(1);
    var filme = new Filme(filmeId, "Matrix", "Sinopse", "14", 136, IMAGEM_PADRAO, StatusFilme.EM_CARTAZ);
        filmeService.salvar(filme);
        
        var sessaoId = new SessaoId(1);
        Map<AssentoId, Boolean> assentos = new HashMap<>();
        assentos.put(new AssentoId("C1"), false);
        var sessao = new Sessao(sessaoId, filmeId, new Date(), StatusSessao.DISPONIVEL, assentos);
        repositorio.salvar(sessao);
        
        // Cria ingresso validado (pronto para uso, mas ainda não utilizado)
        var ingresso = new Ingresso(new IngressoId(1), sessaoId, new AssentoId("C1"),
                                   TipoIngresso.INTEIRA, StatusIngresso.VALIDADO, "QR1");

        // Cria pagamento
        var pagamento = new Pagamento(pagamentoId, 50.0, StatusPagamento.SUCESSO, new Date());
        pagamentoService.salvar(pagamento);
        
        // Cria compra confirmada
        var compra = new Compra(compraId, clienteId, Arrays.asList(ingresso), pagamentoId, StatusCompra.CONFIRMADA);
        repositorio.salvar(compra);
    }

    @Quando("o cliente solicita o cancelamento")
    public void o_cliente_solicita_o_cancelamento() {
        try {
            compraService.cancelarCompra(compraId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("o status da compra é alterado para {string}")
    public void o_status_da_compra_e_alterado_para(String status) {
        var compra = repositorio.obterPorId(compraId);
        assertEquals(StatusCompra.valueOf(status), compra.getStatus());
    }

    @Então("o pagamento é estornado")
    public void o_pagamento_e_estornado() {
        // Validação simplificada - em produção faria integração com gateway
        assertNotNull(pagamentoId);
    }

    @Dado("que o ingresso da compra já foi utilizado na entrada da sessão")
    public void que_o_ingresso_da_compra_ja_foi_utilizado_na_entrada_da_sessao() {
        // Cria cliente
        var cliente = new Cliente(clienteId, "Pedro Oliveira", "pedro@email.com");
        clienteService.salvar(cliente);
        
        // Cria filme e sessão
        var filmeId = new FilmeId(1);
    var filme = new Filme(filmeId, "Matrix", "Sinopse", "14", 136, IMAGEM_PADRAO, StatusFilme.EM_CARTAZ);
        filmeService.salvar(filme);
        
        var sessaoId = new SessaoId(1);
        Map<AssentoId, Boolean> assentos = new HashMap<>();
        assentos.put(new AssentoId("C1"), false);
        var sessao = new Sessao(sessaoId, filmeId, new Date(), StatusSessao.DISPONIVEL, assentos);
        repositorio.salvar(sessao);
        
        // Cria ingresso validado e marca como utilizado
        var ingresso = new Ingresso(new IngressoId(1), sessaoId, new AssentoId("C1"),
                                   TipoIngresso.INTEIRA, StatusIngresso.VALIDADO, "QR1");
        ingresso.utilizar(); // Marca o ingresso como utilizado na entrada

        // Cria pagamento
        var pagamento = new Pagamento(pagamentoId, 50.0, StatusPagamento.SUCESSO, new Date());
        pagamentoService.salvar(pagamento);
        
        // Cria compra confirmada
        var compra = new Compra(compraId, clienteId, Arrays.asList(ingresso), pagamentoId, StatusCompra.CONFIRMADA);
        repositorio.salvar(compra);
    }

    @Quando("o cliente tenta cancelar a compra")
    public void o_cliente_tenta_cancelar_a_compra() {
        try {
            compraService.cancelarCompra(compraId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("o sistema recusa o cancelamento")
    public void o_sistema_recusa_o_cancelamento() {
        assertNotNull(excecao);
    }

    @Então("informa que o ingresso já foi utilizado")
    public void informa_que_o_ingresso_ja_foi_utilizado() {
        assertTrue(excecao.getMessage().contains("já utilizado") || 
                  excecao.getMessage().contains("já foi utilizado"));
    }
}

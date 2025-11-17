package com.astra.cinema.sessao;

import static org.junit.jupiter.api.Assertions.*;

import com.astra.cinema.CinemaFuncionalidade;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.filme.*;
import com.astra.cinema.dominio.sessao.*;
import io.cucumber.java.pt.*;

import java.util.*;

public class CriarSessaoFuncionalidade extends CinemaFuncionalidade {
    private static final String IMAGEM_PADRAO = "https://img.astra/poster.jpg";
    private FilmeId filmeId = new FilmeId(1);
    
    private Sessao sessaoCriada;
    private RuntimeException excecao;

    @Dado("que o filme {string} está com status {string}")
    public void que_o_filme_esta_com_status(String tituloFilme, String status) {
        var statusFilme = StatusFilme.valueOf(status.replace(" ", "_"));
    var filme = new Filme(filmeId, tituloFilme, "Sinopse do filme", "12", 150, IMAGEM_PADRAO, statusFilme);
        filmeService.salvar(filme);
    }

    @Quando("o gerente cria uma nova sessão para esse filme")
    public void o_gerente_cria_uma_nova_sessao_para_esse_filme() {
        try {
            Map<AssentoId, Boolean> assentos = new HashMap<>();
            assentos.put(new AssentoId("A1"), true);
            assentos.put(new AssentoId("A2"), true);
            
            sessaoCriada = sessaoService.criarSessao(filmeId, new Date(), assentos);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("a sessão é registrada com status {string}")
    public void a_sessao_e_registrada_com_status(String status) {
        assertNotNull(sessaoCriada);
        assertEquals(StatusSessao.valueOf(status), sessaoCriada.getStatus());
    }

    @Quando("o gerente tenta criar uma sessão para esse filme")
    public void o_gerente_tenta_criar_uma_sessao_para_esse_filme() {
        try {
            Map<AssentoId, Boolean> assentos = new HashMap<>();
            assentos.put(new AssentoId("A1"), true);
            
            sessaoCriada = sessaoService.criarSessao(filmeId, new Date(), assentos);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("o sistema recusa a criação da sessão")
    public void o_sistema_recusa_a_criacao_da_sessao() {
        assertNotNull(excecao);
        assertNull(sessaoCriada);
    }

    @Então("informa que o filme não está em cartaz")
    public void informa_que_o_filme_nao_esta_em_cartaz() {
        assertTrue(excecao.getMessage().contains("não está em cartaz"));
    }
}

package com.astra.cinema.sessao;

import static org.junit.jupiter.api.Assertions.*;

import com.astra.cinema.CinemaFuncionalidade;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.filme.*;
import com.astra.cinema.dominio.sessao.*;
import io.cucumber.java.pt.*;

import java.util.*;

public class MarcarSessaoEsgotadaFuncionalidade extends CinemaFuncionalidade {
    private static final String IMAGEM_PADRAO = "https://img.astra/poster.jpg";
    private SessaoId sessaoId = new SessaoId(1);
    private RuntimeException excecao;

    @Dado("que todos os assentos da sessão foram reservados")
    public void que_todos_os_assentos_da_sessao_foram_reservados() {
        // Cria filme
        var filmeId = new FilmeId(1);
    var filme = new Filme(filmeId, "Oppenheimer", "Sinopse", "14", 180, IMAGEM_PADRAO, StatusFilme.EM_CARTAZ);
        filmeService.salvar(filme);
        
        // Cria sessão com apenas um assento disponível
        Map<AssentoId, Boolean> assentos = new HashMap<>();
        assentos.put(new AssentoId("A1"), true);
        assentos.put(new AssentoId("A2"), false);
        assentos.put(new AssentoId("A3"), false);
        
        var sessao = new Sessao(sessaoId, filmeId, new Date(), StatusSessao.DISPONIVEL, assentos);
        sessaoService.salvar(sessao);
    }

    @Quando("o último assento é vendido")
    public void o_ultimo_assento_e_vendido() {
        try {
            var sessao = sessaoService.obter(sessaoId);
            sessao.reservarAssento(new AssentoId("A1")); // Reserva o último assento
            sessaoService.salvar(sessao);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("o status da sessão é atualizado para {string}")
    public void o_status_da_sessao_e_atualizado_para(String status) {
        var sessao = sessaoService.obter(sessaoId);
        assertEquals(StatusSessao.valueOf(status), sessao.getStatus());
    }

    @Dado("que ainda há assentos disponíveis na sessão")
    public void que_ainda_ha_assentos_disponiveis_na_sessao() {
        // Cria filme
        var filmeId = new FilmeId(1);
    var filme = new Filme(filmeId, "Barbie", "Sinopse", "Livre", 120, IMAGEM_PADRAO, StatusFilme.EM_CARTAZ);
        filmeService.salvar(filme);
        
        // Cria sessão com assentos disponíveis
        Map<AssentoId, Boolean> assentos = new HashMap<>();
        assentos.put(new AssentoId("B1"), true);
        assentos.put(new AssentoId("B2"), true);
        assentos.put(new AssentoId("B3"), false);
        
        var sessao = new Sessao(sessaoId, filmeId, new Date(), StatusSessao.DISPONIVEL, assentos);
        sessaoService.salvar(sessao);
    }

    @Quando("o sistema tenta alterar o status para {string}")
    public void o_sistema_tenta_alterar_o_status_para(String status) {
        try {
            var sessao = sessaoService.obter(sessaoId);
            sessao.marcarComoEsgotada();
            sessaoService.salvar(sessao);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("a alteração é rejeitada")
    public void a_alteracao_e_rejeitada() {
        assertNotNull(excecao);
    }
}

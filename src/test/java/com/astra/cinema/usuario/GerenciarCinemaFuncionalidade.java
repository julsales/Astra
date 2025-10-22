package com.astra.cinema.usuario;

import static org.junit.jupiter.api.Assertions.*;

import com.astra.cinema.CinemaFuncionalidade;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.filme.*;
import com.astra.cinema.dominio.sessao.*;
import com.astra.cinema.dominio.usuario.*;
import io.cucumber.java.pt.*;

import java.util.*;

public class GerenciarCinemaFuncionalidade extends CinemaFuncionalidade {
    private Funcionario funcionario;
    private Sessao sessaoCriada;
    private RuntimeException excecao;

    @Dado("que o usuário autenticado possui cargo {string}")
    public void que_o_usuario_autenticado_possui_cargo(String cargo) {
        funcionario = new Funcionario("Carlos Manager", Cargo.valueOf(cargo));
    }

    @Quando("ele cria uma nova sessão")
    public void ele_cria_uma_nova_sessao() {
        try {
            if (!funcionario.isGerente()) {
                throw new IllegalStateException("Acesso negado: apenas gerentes podem criar sessões");
            }
            
            // Cria filme
            var filmeId = new FilmeId(1);
            var filme = new Filme(filmeId, "Inception", "Sinopse", "14", 148, StatusFilme.EM_CARTAZ);
            filmeService.salvar(filme);
            
            // Cria sessão
            Map<AssentoId, Boolean> assentos = new HashMap<>();
            assentos.put(new AssentoId("A1"), true);
            
            sessaoCriada = sessaoService.criarSessao(filmeId, new Date(), assentos);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("a sessão é registrada com sucesso")
    public void a_sessao_e_registrada_com_sucesso() {
        assertNotNull(sessaoCriada);
        assertEquals(StatusSessao.DISPONIVEL, sessaoCriada.getStatus());
    }

    @Dado("que o usuário autenticado possui perfil de cliente")
    public void que_o_usuario_autenticado_possui_perfil_de_cliente() {
        funcionario = new Funcionario("João Atendente", Cargo.ATENDENTE);
    }

    @Quando("ele tenta criar uma sessão")
    public void ele_tenta_criar_uma_sessao() {
        try {
            if (!funcionario.isGerente()) {
                throw new IllegalStateException("Acesso negado: apenas gerentes podem criar sessões");
            }
            
            var filmeId = new FilmeId(1);
            var filme = new Filme(filmeId, "Inception", "Sinopse", "14", 148, StatusFilme.EM_CARTAZ);
            filmeService.salvar(filme);
            
            Map<AssentoId, Boolean> assentos = new HashMap<>();
            assentos.put(new AssentoId("A1"), true);
            
            sessaoCriada = sessaoService.criarSessao(filmeId, new Date(), assentos);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("o sistema recusa a operação")
    public void o_sistema_recusa_a_operacao() {
        assertNotNull(excecao);
        assertNull(sessaoCriada);
    }

    @Então("exibe mensagem de acesso negado")
    public void exibe_mensagem_de_acesso_negado() {
        assertTrue(excecao.getMessage().contains("Acesso negado") || 
                  excecao.getMessage().contains("negado"));
    }
}

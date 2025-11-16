package com.astra.cinema.programacao;

import static org.junit.jupiter.api.Assertions.*;

import com.astra.cinema.CinemaFuncionalidade;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.filme.*;
import com.astra.cinema.dominio.sessao.*;
import com.astra.cinema.dominio.programacao.*;
import io.cucumber.java.pt.*;

import java.util.*;

public class CriarProgramacaoFuncionalidade extends CinemaFuncionalidade {
    private List<SessaoId> sessoes = new ArrayList<>();
    private Programacao programacaoCriada;
    private RuntimeException excecao;

    @Dado("que há três sessões com status {string}")
    public void que_ha_tres_sessoes_com_status(String status) {
        // Cria filme
        var filmeId = new FilmeId(1);
        var filme = new Filme(filmeId, "Interstellar", "Sinopse", "12", 169, StatusFilme.EM_CARTAZ);
        filmeService.salvar(filme);
        
        var statusSessao = StatusSessao.valueOf(status);
        
        // Cria três sessões
        for (int i = 1; i <= 3; i++) {
            var sessaoId = new SessaoId(i);
            Map<AssentoId, Boolean> assentos = new HashMap<>();
            assentos.put(new AssentoId("A" + i), true);
            
            var sessao = new Sessao(sessaoId, filmeId, new Date(), statusSessao, assentos);
            sessaoService.salvar(sessao);
            sessoes.add(sessaoId);
        }
    }

    @Quando("o gerente cria uma nova programação para a semana")
    public void o_gerente_cria_uma_nova_programacao_para_a_semana() {
        try {
            var inicio = new Date();
            var calendario = Calendar.getInstance();
            calendario.setTime(inicio);
            calendario.add(Calendar.DAY_OF_MONTH, 7);
            var fim = calendario.getTime();
            
            programacaoCriada = programacaoService.criarProgramacao(inicio, fim, sessoes);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("a programação é registrada com sucesso")
    public void a_programacao_e_registrada_com_sucesso() {
        assertNotNull(programacaoCriada);
        assertEquals(3, programacaoCriada.getSessoes().size());
    }

    @Dado("que uma das sessões selecionadas está com status {string}")
    public void que_uma_das_sessoes_selecionadas_esta_com_status(String status) {
        // Cria filme
        var filmeId = new FilmeId(1);
        var filme = new Filme(filmeId, "The Batman", "Sinopse", "14", 176, StatusFilme.EM_CARTAZ);
        filmeService.salvar(filme);
        
        // Cria duas sessões disponíveis
        for (int i = 1; i <= 2; i++) {
            var sessaoId = new SessaoId(i);
            Map<AssentoId, Boolean> assentos = new HashMap<>();
            assentos.put(new AssentoId("A" + i), true);
            
            var sessao = new Sessao(sessaoId, filmeId, new Date(), StatusSessao.DISPONIVEL, assentos);
            sessaoService.salvar(sessao);
            sessoes.add(sessaoId);
        }
        
        // Cria uma sessão cancelada
        var sessaoIdCancelada = new SessaoId(3);
        Map<AssentoId, Boolean> assentos = new HashMap<>();
        assentos.put(new AssentoId("A3"), true);
        
        var sessaoCancelada = new Sessao(sessaoIdCancelada, filmeId, new Date(), 
                                        StatusSessao.valueOf(status), assentos);
        sessaoService.salvar(sessaoCancelada);
        sessoes.add(sessaoIdCancelada);
    }

    @Quando("o gerente tenta criar a programação")
    public void o_gerente_tenta_criar_a_programacao() {
        try {
            var inicio = new Date();
            var calendario = Calendar.getInstance();
            calendario.setTime(inicio);
            calendario.add(Calendar.DAY_OF_MONTH, 7);
            var fim = calendario.getTime();
            
            programacaoCriada = programacaoService.criarProgramacao(inicio, fim, sessoes);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("o sistema recusa a criação da programação")
    public void o_sistema_recusa_a_criacao_da_programacao() {
        assertNotNull(excecao);
        assertNull(programacaoCriada);
    }
}

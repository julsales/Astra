package com.astra.bdd;

import com.astra.bdd.TestContext;
import com.astra.model.*;
import com.astra.repository.*;
import io.cucumber.java.pt.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ProgramacaoSteps {

    @Autowired
    private TestContext context;

    @Autowired
    private ProgramacaoRepository programacaoRepository;

    @Autowired
    private FilmeRepository filmeRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private SessaoRepository sessaoRepository;

    // Regra de Negócio 12 - Programação com sessões válidas
    @Dado("que há três sessões com status {string}")
    public void queHaTresSessionsComStatus(String status) {
        context.getSessoes().clear();
        
        Filme filme = new Filme("Filme Programação", "Sinopse", 120);
        filme.setStatus(Filme.StatusFilme.EM_CARTAZ);
        filme = filmeRepository.save(filme);
        
        Sala sala = new Sala(context.gerarNumeroSalaUnico(), 100, Sala.TipoSala.STANDARD);
        sala = salaRepository.save(sala);
        
        for (int i = 0; i < 3; i++) {
            Sessao sessao = new Sessao(filme, sala, 
                LocalDateTime.now().plusDays(i + 1).withHour(20), 
                new BigDecimal("30.00"));
            sessao.setStatus(Sessao.StatusSessao.valueOf(status));
            sessao = sessaoRepository.save(sessao);
            context.adicionarSessao(sessao);
        }
    }

    @Quando("o gerente cria uma nova programação para a semana")
    public void oGerenteCriaUmaNovaProgramacaoParaASemana() {
        try {
            Programacao programacao = new Programacao(
                LocalDate.now(),
                LocalDate.now().plusDays(7)
            );
            
            for (Sessao sessao : context.getSessoes()) {
                programacao.adicionarSessao(sessao);
            }
            
            programacao.validarSessoes();
            programacao = programacaoRepository.save(programacao);
            context.setProgramacao(programacao);
        } catch (Exception e) {
            context.setException(e);
        }
    }

    @Então("a programação é registrada com sucesso")
    public void aProgramacaoERegistradaComSucesso() {
        assertNotNull(context.getProgramacao());
        assertNull(context.getException());
    }

    @Dado("que uma das sessões selecionadas está com status {string}")
    public void queUmasDasSessionsSelecionadasEstaComStatus(String status) {
        context.getSessoes().clear();
        
        Filme filme = new Filme("Filme Teste", "Sinopse", 120);
        filme.setStatus(Filme.StatusFilme.EM_CARTAZ);
        filme = filmeRepository.save(filme);
        
        Sala sala = new Sala(context.gerarNumeroSalaUnico(), 100, Sala.TipoSala.STANDARD);
        sala = salaRepository.save(sala);
        
        // Criar 2 sessões disponíveis
        for (int i = 0; i < 2; i++) {
            Sessao sessao = new Sessao(filme, sala, 
                LocalDateTime.now().plusDays(i + 1).withHour(20), 
                new BigDecimal("30.00"));
            sessao = sessaoRepository.save(sessao);
            context.adicionarSessao(sessao);
        }
        
        // Criar 1 sessão cancelada
        Sessao sessaoCancelada = new Sessao(filme, sala, 
            LocalDateTime.now().plusDays(3).withHour(20), 
            new BigDecimal("30.00"));
        sessaoCancelada.setStatus(Sessao.StatusSessao.valueOf(status));
        sessaoCancelada = sessaoRepository.save(sessaoCancelada);
        context.adicionarSessao(sessaoCancelada);
    }

    @Quando("o gerente tenta criar a programação")
    public void oGerenteTentaCriarAProgramacao() {
        try {
            Programacao programacao = new Programacao(
                LocalDate.now(),
                LocalDate.now().plusDays(7)
            );
            
            for (Sessao sessao : context.getSessoes()) {
                programacao.adicionarSessao(sessao);
            }
            
            programacao.validarSessoes();
            programacaoRepository.save(programacao);
        } catch (Exception e) {
            context.setException(e);
        }
    }
}

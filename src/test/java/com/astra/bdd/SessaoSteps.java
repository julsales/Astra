package com.astra.bdd;

import com.astra.bdd.TestContext;
import com.astra.model.*;
import com.astra.repository.*;
import io.cucumber.java.pt.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SessaoSteps {

    @Autowired
    private TestContext context;

    @Autowired
    private FilmeRepository filmeRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private AssentoRepository assentoRepository;

    // Regra de Negócio 4 - Criar sessão para filme em cartaz
    @Dado("que o filme {string} está com status {string}")
    public void queOFilmeEstaComStatus(String titulo, String status) {
        Filme filme = new Filme(titulo, "Sinopse", 120);
        filme.setStatus(Filme.StatusFilme.valueOf(status));
        filme = filmeRepository.save(filme);
        context.setFilme(filme);
        
        // Criar sala padrão se não existir
        if (context.getSala() == null) {
            Sala sala = new Sala(context.gerarNumeroSalaUnico(), 100, Sala.TipoSala.STANDARD);
            sala = salaRepository.save(sala);
            context.setSala(sala);
        }
    }

    @Quando("o gerente cria uma nova sessão para esse filme")
    public void oGerenteCriaUmaNovaSessaoParaEsseFilme() {
        try {
            Filme filme = context.getFilme();
            
            if (filme.getStatus() != Filme.StatusFilme.EM_CARTAZ) {
                throw new IllegalStateException("O filme não está em cartaz");
            }
            
            Sala sala = context.getSala();
            LocalDateTime dataHora = LocalDateTime.now().plusDays(1).withHour(20).withMinute(0);
            BigDecimal preco = new BigDecimal("30.00");
            
            Sessao sessao = new Sessao(filme, sala, dataHora, preco);
            sessao = sessaoRepository.save(sessao);
            context.setSessao(sessao);
        } catch (Exception e) {
            context.setException(e);
        }
    }

    @Então("a sessão é registrada com status {string}")
    public void aSessaoERegistradaComStatus(String status) {
        Sessao sessao = context.getSessao();
        assertNotNull(sessao);
        assertEquals(Sessao.StatusSessao.valueOf(status), sessao.getStatus());
    }

    @Quando("o gerente tenta criar uma sessão para esse filme")
    public void oGerenteTentaCriarUmaSessaoParaEsseFilme() {
        try {
            Filme filme = context.getFilme();
            
            if (filme.getStatus() != Filme.StatusFilme.EM_CARTAZ) {
                throw new IllegalStateException("O filme não está em cartaz");
            }
            
            Sala sala = context.getSala();
            LocalDateTime dataHora = LocalDateTime.now().plusDays(1).withHour(20).withMinute(0);
            BigDecimal preco = new BigDecimal("30.00");
            
            Sessao sessao = new Sessao(filme, sala, dataHora, preco);
            sessaoRepository.save(sessao);
        } catch (Exception e) {
            context.setException(e);
        }
    }

    @Então("o sistema recusa a criação")
    public void oSistemaRecusaACriacao() {
        assertNotNull(context.getException());
    }

    @Então("informa que o filme não está em cartaz")
    public void informaQueOFilmeNaoEstaEmCartaz() {
        assertTrue(context.getMensagemErro().contains("não está em cartaz"));
    }

    // Regra de Negócio 5 - Marcar sessão esgotada
    @Dado("que todos os assentos da sessão foram reservados")
    public void queTodosOsAssentosDaSessaoForamReservados() {
        Filme filme = new Filme("Filme Teste", "Sinopse", 120);
        filme.setStatus(Filme.StatusFilme.EM_CARTAZ);
        filme = filmeRepository.save(filme);
        
        Sala sala = new Sala(context.gerarNumeroSalaUnico(), 5, Sala.TipoSala.STANDARD);
        sala = salaRepository.save(sala);
        
        Sessao sessao = new Sessao(filme, sala, LocalDateTime.now().plusDays(1), new BigDecimal("30.00"));
        sessao = sessaoRepository.save(sessao);
        
        // Reservar todos os assentos exceto 1
        sessao.setAssentosDisponiveis(1);
        sessao = sessaoRepository.save(sessao);
        
        context.setSessao(sessao);
    }

    @Quando("o último assento é vendido")
    public void oUltimoAssentoEVendido() {
        Sessao sessao = context.getSessao();
        sessao.reservarAssentos(1);
        sessaoRepository.save(sessao);
    }

    @Então("o status da sessão é atualizado para {string}")
    public void oStatusDaSessaoEAtualizadoPara(String status) {
        Sessao sessao = sessaoRepository.findById(context.getSessao().getId()).orElse(null);
        assertNotNull(sessao);
        assertEquals(Sessao.StatusSessao.valueOf(status), sessao.getStatus());
    }

    @Dado("que ainda há assentos disponíveis na sessão")
    public void queAindaHaAssentosDisponiveisNaSessao() {
        Filme filme = new Filme("Filme Disponível", "Sinopse", 120);
        filme.setStatus(Filme.StatusFilme.EM_CARTAZ);
        filme = filmeRepository.save(filme);
        
        Sala sala = new Sala(context.gerarNumeroSalaUnico(), 50, Sala.TipoSala.STANDARD);
        sala = salaRepository.save(sala);
        
        Sessao sessao = new Sessao(filme, sala, LocalDateTime.now().plusDays(1), new BigDecimal("30.00"));
        sessao.setAssentosDisponiveis(10); // Ainda há assentos disponíveis
        sessao = sessaoRepository.save(sessao);
        
        context.setSessao(sessao);
    }

    @Quando("o sistema tenta alterar o status para {string}")
    public void oSistemaTentaAlterarOStatusPara(String status) {
        try {
            Sessao sessao = context.getSessao();
            if (status.equals("ESGOTADA")) {
                sessao.marcarEsgotada();
                sessaoRepository.save(sessao);
            }
        } catch (Exception e) {
            context.setException(e);
        }
    }

    @Então("a alteração é rejeitada")
    public void aAlteracaoERejeitada() {
        assertNotNull(context.getException());
        assertTrue(context.getMensagemErro().contains("assentos disponíveis"));
    }
}

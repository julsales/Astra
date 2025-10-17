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

public class FilmeSteps {

    @Autowired
    private TestContext context;

    @Autowired
    private FilmeRepository filmeRepository;

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private SalaRepository salaRepository;

    // Regra de Negócio 6 - Remover filme
    @Dado("que o filme {string} não possui sessões agendadas")
    public void queOFilmeNaoPossuiSessoesAgendadas(String titulo) {
        Filme filme = new Filme(titulo, "Sinopse", 120);
        filme.setStatus(Filme.StatusFilme.EM_CARTAZ);
        filme = filmeRepository.save(filme);
        context.setFilme(filme);
    }

    @Quando("o gerente remove o filme")
    public void oGerenteRemoveOFilme() {
        try {
            Filme filme = context.getFilme();
            List<Sessao> sessoesFuturas = sessaoRepository.findSessoesFuturasPorFilme(filme, LocalDateTime.now());
            
            if (!sessoesFuturas.isEmpty()) {
                throw new IllegalStateException("Há sessões ativas");
            }
            
            filme.setStatus(Filme.StatusFilme.RETIRADO);
            filmeRepository.save(filme);
        } catch (Exception e) {
            context.setException(e);
        }
    }

    @Então("o status do filme muda para {string}")
    public void oStatusDoFilmeMudaPara(String status) {
        Filme filme = filmeRepository.findById(context.getFilme().getId()).orElse(null);
        assertNotNull(filme);
        assertEquals(Filme.StatusFilme.valueOf(status), filme.getStatus());
    }

    @Dado("que o filme {string} ainda possui sessões futuras")
    public void queOFilmeAindaPossuiSessoesFuturas(String titulo) {
        Filme filme = new Filme(titulo, "Sinopse", 120);
        filme.setStatus(Filme.StatusFilme.EM_CARTAZ);
        filme = filmeRepository.save(filme);
        
        // Criar uma sala e sessão futura
        Sala sala = new Sala(context.gerarNumeroSalaUnico(), 100, Sala.TipoSala.STANDARD);
        sala = salaRepository.save(sala);
        
        Sessao sessao = new Sessao(filme, sala, LocalDateTime.now().plusDays(5), new BigDecimal("30.00"));
        sessaoRepository.save(sessao);
        
        context.setFilme(filme);
    }

    @Quando("o gerente tenta remover o filme")
    public void oGerenteTentaRemoverOFilme() {
        try {
            Filme filme = context.getFilme();
            List<Sessao> sessoesFuturas = sessaoRepository.findSessoesFuturasPorFilme(filme, LocalDateTime.now());
            
            if (!sessoesFuturas.isEmpty()) {
                throw new IllegalStateException("Há sessões ativas");
            }
            
            filme.setStatus(Filme.StatusFilme.RETIRADO);
            filmeRepository.save(filme);
        } catch (Exception e) {
            context.setException(e);
        }
    }

    @Então("o sistema impede a remoção")
    public void oSistemaImpedeARemocao() {
        assertNotNull(context.getException());
    }

    @Então("exibe mensagem informando que há sessões ativas")
    public void exibeMensagemInformandoQueHaSessoesAtivas() {
        assertTrue(context.getMensagemErro().contains("sessões ativas"));
    }
}

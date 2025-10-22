package com.astra.cinema.filme;

import static org.junit.jupiter.api.Assertions.*;

import com.astra.cinema.CinemaFuncionalidade;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.filme.*;
import com.astra.cinema.dominio.sessao.*;
import io.cucumber.java.pt.*;

import java.util.*;

public class RemoverFilmeFuncionalidade extends CinemaFuncionalidade {
    private FilmeId filmeId = new FilmeId(1);
    private RuntimeException excecao;

    @Dado("que o filme {string} não possui sessões agendadas")
    public void que_o_filme_nao_possui_sessoes_agendadas(String tituloFilme) {
        var filme = new Filme(filmeId, tituloFilme, "Sinopse do filme", "16", 130, StatusFilme.EM_CARTAZ);
        filmeService.salvar(filme);
        
        // Não cria nenhuma sessão
    }

    @Quando("o gerente remove o filme")
    public void o_gerente_remove_o_filme() {
        try {
            filmeService.removerFilme(filmeId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("o status do filme muda para {string}")
    public void o_status_do_filme_muda_para(String status) {
        var filme = filmeService.obter(filmeId);
        assertEquals(StatusFilme.valueOf(status), filme.getStatus());
    }

    @Dado("que o filme {string} ainda possui sessões futuras")
    public void que_o_filme_ainda_possui_sessoes_futuras(String tituloFilme) {
        var filme = new Filme(filmeId, tituloFilme, "Sinopse do filme", "12", 150, StatusFilme.EM_CARTAZ);
        filmeService.salvar(filme);
        
        // Cria uma sessão futura para o filme
        var sessaoId = new SessaoId(1);
        Map<AssentoId, Boolean> assentos = new HashMap<>();
        assentos.put(new AssentoId("A1"), true);
        
        var sessao = new Sessao(sessaoId, filmeId, new Date(), StatusSessao.DISPONIVEL, assentos);
        repositorio.salvar(sessao);
    }

    @Quando("o gerente tenta remover o filme")
    public void o_gerente_tenta_remover_o_filme() {
        try {
            filmeService.removerFilme(filmeId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Então("o sistema impede a remoção")
    public void o_sistema_impede_a_remocao() {
        assertNotNull(excecao);
    }

    @Então("exibe mensagem informando que há sessões ativas")
    public void exibe_mensagem_informando_que_ha_sessoes_ativas() {
        assertTrue(excecao.getMessage().contains("sessões ativas") || 
                  excecao.getMessage().contains("há sessões"));
    }
}

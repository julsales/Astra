package com.astra.cinema.dominio.sessao;

import com.astra.cinema.dominio.comum.*;
import java.util.List;

public interface SessaoRepositorio {
    void salvar(Sessao sessao);
    Sessao obterPorId(SessaoId sessaoId);
    List<Sessao> buscarPorFilme(FilmeId filmeId);
}

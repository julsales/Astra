package com.astra.cinema.dominio.filme;

import com.astra.cinema.dominio.comum.FilmeId;
import java.util.List;

public interface FilmeRepositorio {
    void salvar(Filme filme);
    Filme obterPorId(FilmeId filmeId);
    List<Filme> listarFilmesEmCartaz();
}

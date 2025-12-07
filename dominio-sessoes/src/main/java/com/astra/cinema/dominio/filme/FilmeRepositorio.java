package com.astra.cinema.dominio.filme;

import com.astra.cinema.dominio.comum.FilmeId;
import java.util.List;

public interface FilmeRepositorio {
    /**
     * Salva um filme no repositório.
     * @param filme Filme a ser salvo (pode ter ID null para novos filmes)
     * @return Filme salvo com ID preenchido pelo banco de dados
     */
    Filme salvar(Filme filme);
    
    Filme obterPorId(FilmeId filmeId);
    List<Filme> listarFilmesEmCartaz();
    List<Filme> listarTodos();
}

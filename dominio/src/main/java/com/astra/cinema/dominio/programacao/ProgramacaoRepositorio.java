package com.astra.cinema.dominio.programacao;

import com.astra.cinema.dominio.comum.ProgramacaoId;
import java.util.List;

public interface ProgramacaoRepositorio {
    void salvar(Programacao programacao);
    Programacao obterPorId(ProgramacaoId programacaoId);
    List<Programacao> listarProgramacoes();
}

package com.astra.cinema.dominio.sessao;

import com.astra.cinema.dominio.comum.*;
import java.util.List;

public interface SessaoRepositorio {
    /**
     * Salva uma sessão no repositório.
     * @param sessao Sessão a ser salva (pode ter ID null para novas sessões)
     * @return Sessão salva com ID preenchido pelo banco de dados
     */
    Sessao salvar(Sessao sessao);
    
    Sessao obterPorId(SessaoId sessaoId);
    List<Sessao> buscarPorFilme(FilmeId filmeId);
}

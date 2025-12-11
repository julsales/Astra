package com.astra.cinema.dominio.sessao;

import com.astra.cinema.dominio.comum.*;
import java.util.Date;
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
    List<Sessao> listarTodas();

    /**
     * Verifica se existe conflito de horário para uma sessão em uma sala específica.
     * @param salaId ID da sala
     * @param horario Horário da sessão
     * @param sessaoIdExcluir ID da sessão a excluir da verificação (para edição)
     * @return true se houver conflito, false caso contrário
     */
    boolean existeConflitoHorario(SalaId salaId, Date horario, SessaoId sessaoIdExcluir);
}

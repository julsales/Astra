package com.astra.cinema.aplicacao.sessao;

import com.astra.cinema.dominio.comum.SalaId;
import com.astra.cinema.dominio.comum.SessaoId;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.sessao.StatusSessao;

import java.util.Date;

/**
 * Caso de uso: Modificar horário de uma sessão
 * Responsabilidade: Orquestrar a alteração de horário de sessão
 * 
 * Padrão: Command (encapsula a operação de modificação)
 */
public class ModificarSessaoUseCase {
    private final SessaoRepositorio sessaoRepositorio;

    public ModificarSessaoUseCase(SessaoRepositorio sessaoRepositorio) {
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de sessões não pode ser nulo");
        }
        this.sessaoRepositorio = sessaoRepositorio;
    }

    /**
     * Modifica o horário e/ou sala de uma sessão
     * 
     * @param sessaoId ID da sessão
     * @param novoHorario Novo horário (ou null para manter)
     * @param novaSala Nova sala (ou null para manter)
     * @return Sessão modificada
     * @throws IllegalStateException se a sessão já passou ou está cancelada
     */
    public Sessao executar(SessaoId sessaoId, Date novoHorario, SalaId novaSala) {
        if (sessaoId == null) {
            throw new IllegalArgumentException("O ID da sessão não pode ser nulo");
        }

        // Busca a sessão
        Sessao sessao = sessaoRepositorio.obterPorId(sessaoId);
        if (sessao == null) {
            throw new IllegalArgumentException("Sessão não encontrada");
        }

        // Verifica se está cancelada
        if (sessao.getStatus() == StatusSessao.CANCELADA) {
            throw new IllegalStateException("Não é possível modificar uma sessão cancelada");
        }

        // Determina valores atualizados (aceita nulls --> mantém o valor atual)
        Date horarioAtualizado = (novoHorario != null) ? novoHorario : sessao.getHorario();
        SalaId salaAtualizada = (novaSala != null) ? novaSala : sessao.getSalaId();

        // Verificar conflito de horário (excluindo a própria sessão da verificação)
        if (sessaoRepositorio.existeConflitoHorario(salaAtualizada, horarioAtualizado, sessaoId)) {
            throw new IllegalStateException("Já existe uma sessão agendada para esta sala neste horário ou próximo a ele");
        }

        Sessao sessaoModificada = new Sessao(
            sessao.getSessaoId(),
            sessao.getFilmeId(),
            horarioAtualizado,
            sessao.getStatus(),
            sessao.getMapaAssentosDisponiveis(),
            salaAtualizada
        );

        // Persiste
        sessaoRepositorio.salvar(sessaoModificada);

        return sessaoModificada;
    }

    /**
     * Verifica se uma sessão pode ser modificada
     * 
     * @param sessaoId ID da sessão
     * @return true se pode ser modificada
     */
    public boolean podeModificar(SessaoId sessaoId) {
        if (sessaoId == null) {
            return false;
        }

        Sessao sessao = sessaoRepositorio.obterPorId(sessaoId);
        if (sessao == null) {
            return false;
        }

        return sessao.getStatus() != StatusSessao.CANCELADA;
    }
}

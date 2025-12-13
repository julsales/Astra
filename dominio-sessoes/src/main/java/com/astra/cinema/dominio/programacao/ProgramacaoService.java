package com.astra.cinema.dominio.programacao;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirColecaoNaoVazia;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirEstado;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.sessao.StatusSessao;
import com.astra.cinema.dominio.usuario.Funcionario;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de Programação - Fachada para manter compatibilidade com testes
 * Delega para os Use Cases da camada de aplicação
 */
public class ProgramacaoService {
    private final ProgramacaoRepositorio programacaoRepositorio;
    private final SessaoRepositorio sessaoRepositorio;

    public ProgramacaoService(ProgramacaoRepositorio programacaoRepositorio, 
                             SessaoRepositorio sessaoRepositorio) {
        this.programacaoRepositorio = exigirNaoNulo(programacaoRepositorio,
            "O repositório de programações não pode ser nulo");
        this.sessaoRepositorio = exigirNaoNulo(sessaoRepositorio, "O repositório de sessões não pode ser nulo");
    }

    /**
     * Cria uma nova programação semanal.
     * RN11: Apenas funcionários com cargo de GERENTE podem criar programações.
     * RN12: A programação só pode conter sessões com status "DISPONIVEL".
     *
     * @param funcionario Funcionário que está criando a programação (deve ser GERENTE)
     * @param periodoInicio Data de início da programação
     * @param periodoFim Data de fim da programação
     * @param sessoes Lista de IDs das sessões a incluir
     * @return Programação criada
     */
    public Programacao criarProgramacao(Funcionario funcionario, Date periodoInicio, Date periodoFim, List<SessaoId> sessoes) {
        // RN11: Verifica permissão de gerente
        exigirNaoNulo(funcionario, "O funcionário não pode ser nulo");
        exigirEstado(funcionario.isGerente(), "Apenas gerentes podem criar programações");

        Date inicio = exigirNaoNulo(periodoInicio, "Período inicial não pode ser nulo");
        Date fim = exigirNaoNulo(periodoFim, "Período final não pode ser nulo");
        exigirEstado(!inicio.after(fim), "Data inicial não pode ser posterior à final");

        // Valida que o período é no futuro ou presente
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date hoje = cal.getTime();
        exigirEstado(!fim.before(hoje), "Não é possível criar programação para períodos passados");

        var sessoesRequisitadas = exigirColecaoNaoVazia(sessoes, "A programação deve ter sessões");

        // Remove duplicatas
        var sessoesUnicas = sessoesRequisitadas.stream().distinct().collect(java.util.stream.Collectors.toList());
        exigirEstado(sessoesUnicas.size() == sessoesRequisitadas.size(),
            "A programação contém sessões duplicadas");

        // RN12: Verifica se todas as sessões estão DISPONIVEL e dentro do período
        List<Sessao> sessoesValidadas = new ArrayList<>();
        for (SessaoId sessaoId : sessoesRequisitadas) {
            var sessao = exigirNaoNulo(sessaoRepositorio.obterPorId(sessaoId),
                "Sessão não encontrada: " + sessaoId.getId());
            exigirEstado(sessao.getStatus() == StatusSessao.DISPONIVEL,
                "Apenas sessões com status DISPONIVEL podem ser incluídas. Sessão " + sessaoId.getId() + " está " + sessao.getStatus());

            // Valida que a sessão está dentro do período da programação
            Date horarioSessao = sessao.getHorario();
            Calendar calFim = Calendar.getInstance();
            calFim.setTime(fim);
            calFim.set(Calendar.HOUR_OF_DAY, 23);
            calFim.set(Calendar.MINUTE, 59);
            calFim.set(Calendar.SECOND, 59);
            Date fimComHorario = calFim.getTime();

            exigirEstado(!horarioSessao.before(inicio) && !horarioSessao.after(fimComHorario),
                "Sessão " + sessaoId.getId() + " está fora do período da programação");

            sessoesValidadas.add(sessao);
        }

        // Valida conflitos de horário na mesma sala
        validarConflitosDeHorario(sessoesValidadas);

        var programacao = new Programacao(new ProgramacaoId(), inicio, fim, sessoesRequisitadas);
        programacaoRepositorio.salvar(programacao);
        return programacao;
    }

    /**
     * Valida se há conflitos de horário entre sessões na mesma sala.
     * Sessões na mesma sala não podem se sobrepor.
     */
    private void validarConflitosDeHorario(List<Sessao> sessoes) {
        // Agrupa sessões por sala
        Map<SalaId, List<Sessao>> sessoesPorSala = sessoes.stream()
            .collect(Collectors.groupingBy(Sessao::getSalaId));

        // Para cada sala, verifica conflitos
        for (Map.Entry<SalaId, List<Sessao>> entrada : sessoesPorSala.entrySet()) {
            SalaId salaId = entrada.getKey();
            List<Sessao> sessoesDaSala = entrada.getValue();

            // Ordena por horário
            sessoesDaSala.sort(Comparator.comparing(Sessao::getHorario));

            // Verifica sobreposição entre sessões consecutivas
            for (int i = 0; i < sessoesDaSala.size() - 1; i++) {
                Sessao sessaoAtual = sessoesDaSala.get(i);
                Sessao proximaSessao = sessoesDaSala.get(i + 1);

                // Calcula o fim da sessão atual (horário + duração do filme + 30min limpeza)
                Calendar fimSessaoAtual = Calendar.getInstance();
                fimSessaoAtual.setTime(sessaoAtual.getHorario());
                fimSessaoAtual.add(Calendar.MINUTE, 150); // Assumindo ~120min de filme + 30min limpeza

                // Verifica se a próxima sessão começa antes do fim da atual
                if (!proximaSessao.getHorario().after(fimSessaoAtual.getTime())) {
                    throw new IllegalStateException(
                        String.format("Conflito de horário na sala ID %d: Sessão #%d termina depois do início da Sessão #%d",
                            salaId.getId(),
                            sessaoAtual.getSessaoId().getId(),
                            proximaSessao.getSessaoId().getId())
                    );
                }
            }
        }
    }

    public void salvar(Programacao programacao) {
        programacaoRepositorio.salvar(exigirNaoNulo(programacao, "A programação não pode ser nula"));
    }

    public Programacao obter(ProgramacaoId programacaoId) {
        exigirNaoNulo(programacaoId, "O id da programação não pode ser nulo");
        return programacaoRepositorio.obterPorId(programacaoId);
    }

    public List<Programacao> listarProgramacoes() {
        return programacaoRepositorio.listarProgramacoes();
    }

    /**
     * Remove uma programação existente.
     *
     * @param programacaoId ID da programação a ser removida
     */
    public void removerProgramacao(ProgramacaoId programacaoId) {
        exigirNaoNulo(programacaoId, "O id da programação não pode ser nulo");

        // Verifica se a programação existe
        Programacao programacao = programacaoRepositorio.obterPorId(programacaoId);
        exigirNaoNulo(programacao, "Programação não encontrada: " + programacaoId.getId());

        programacaoRepositorio.remover(programacaoId);
    }
}

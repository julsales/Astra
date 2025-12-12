package com.astra.cinema.aplicacao.servicos;

import com.astra.cinema.aplicacao.sessao.CriarSessaoUseCase;
import com.astra.cinema.aplicacao.sessao.ModificarSessaoUseCase;
import com.astra.cinema.aplicacao.sessao.RemarcarIngressosSessaoUseCase;
import com.astra.cinema.aplicacao.sessao.RemoverSessaoUseCase;
import com.astra.cinema.dominio.comum.AssentoId;
import com.astra.cinema.dominio.comum.FilmeId;
import com.astra.cinema.dominio.comum.SalaId;
import com.astra.cinema.dominio.comum.SessaoId;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.sessao.Sala;
import com.astra.cinema.dominio.sessao.SalaRepositorio;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.sessao.StatusSessao;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço de Aplicação para operações de Sessão
 * Centraliza toda lógica de negócio relacionada a sessões
 */
public class SessaoService {

    private final SessaoRepositorio sessaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;
    private final SalaRepositorio salaRepositorio;
    private final CriarSessaoUseCase criarSessaoUseCase;
    private final ModificarSessaoUseCase modificarSessaoUseCase;
    private final RemoverSessaoUseCase removerSessaoUseCase;
    private final RemarcarIngressosSessaoUseCase remarcarIngressosSessaoUseCase;

    public SessaoService(
            SessaoRepositorio sessaoRepositorio,
            FilmeRepositorio filmeRepositorio,
            SalaRepositorio salaRepositorio,
            CriarSessaoUseCase criarSessaoUseCase,
            ModificarSessaoUseCase modificarSessaoUseCase,
            RemoverSessaoUseCase removerSessaoUseCase,
            RemarcarIngressosSessaoUseCase remarcarIngressosSessaoUseCase) {
        this.sessaoRepositorio = sessaoRepositorio;
        this.filmeRepositorio = filmeRepositorio;
        this.salaRepositorio = salaRepositorio;
        this.criarSessaoUseCase = criarSessaoUseCase;
        this.modificarSessaoUseCase = modificarSessaoUseCase;
        this.removerSessaoUseCase = removerSessaoUseCase;
        this.remarcarIngressosSessaoUseCase = remarcarIngressosSessaoUseCase;
    }

    /**
     * Atualiza automaticamente o status da sessão para INDISPONIVEL se já passou
     */
    private Sessao atualizarStatusSeNecessario(Sessao sessao) {
        if (sessao.getStatus() == StatusSessao.DISPONIVEL || sessao.getStatus() == StatusSessao.ESGOTADA) {
            Date agora = new Date();
            if (sessao.getHorario().before(agora)) {
                // Sessão já começou, marcar como INDISPONIVEL
                Sessao sessaoAtualizada = new Sessao(
                    sessao.getSessaoId(),
                    sessao.getFilmeId(),
                    sessao.getHorario(),
                    StatusSessao.INDISPONIVEL,
                    sessao.getMapaAssentosDisponiveis(),
                    sessao.getSalaId()
                );
                sessaoRepositorio.salvar(sessaoAtualizada);
                return sessaoAtualizada;
            }
        }
        return sessao;
    }

    /**
     * Lista sessões com filtros opcionais
     */
    public List<SessaoDTO> listarSessoes(Integer filmeId, String status, boolean apenasAtivas) {
        List<Sessao> sessoes;

        if (filmeId != null) {
            sessoes = sessaoRepositorio.buscarPorFilme(new FilmeId(filmeId));
        } else {
            sessoes = sessaoRepositorio.listarTodas();
        }

        // Atualizar status de sessões que já passaram
        sessoes = sessoes.stream()
                .map(this::atualizarStatusSeNecessario)
                .collect(Collectors.toList());

        // Filtro por status
        if (status != null && !status.isEmpty()) {
            StatusSessao statusEnum = StatusSessao.valueOf(status.toUpperCase());
            sessoes = sessoes.stream()
                    .filter(s -> s.getStatus() == statusEnum)
                    .collect(Collectors.toList());
        }

        // Filtro apenas ativas (DISPONIVEL ou ESGOTADA, mas não CANCELADA)
        if (apenasAtivas) {
            sessoes = sessoes.stream()
                    .filter(s -> s.getStatus() != StatusSessao.CANCELADA)
                    .collect(Collectors.toList());
        }

        return sessoes.stream()
                .map(this::mapearSessaoParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtém indicadores do dashboard (total de sessões, ocupação média, etc.)
     */
    public IndicadoresSessao obterIndicadores() {
        List<Sessao> todasSessoes = sessaoRepositorio.listarTodas();
        
        // Atualizar status de sessões que já passaram
        todasSessoes = todasSessoes.stream()
                .map(this::atualizarStatusSeNecessario)
                .collect(Collectors.toList());

        long totalSessoes = todasSessoes.size();
        long sessoesDisponiveis = todasSessoes.stream()
                .filter(s -> s.getStatus() == StatusSessao.DISPONIVEL)
                .count();
        long sessoesEsgotadas = todasSessoes.stream()
                .filter(s -> s.getStatus() == StatusSessao.ESGOTADA)
                .count();
        long sessoesCanceladas = todasSessoes.stream()
                .filter(s -> s.getStatus() == StatusSessao.CANCELADA)
                .count();

        // Calcula ocupação média APENAS de sessões DISPONIVEL
        double ocupacaoMedia = todasSessoes.stream()
                .filter(s -> s.getStatus() == StatusSessao.DISPONIVEL)
                .mapToDouble(this::calcularOcupacao)
                .average()
                .orElse(0.0);

        // Sessões hoje
        long sessoesHoje = todasSessoes.stream()
                .filter(s -> {
                    Calendar now = Calendar.getInstance();
                    Calendar sh = Calendar.getInstance();
                    sh.setTime(s.getHorario());
                    return now.get(Calendar.YEAR) == sh.get(Calendar.YEAR)
                            && now.get(Calendar.DAY_OF_YEAR) == sh.get(Calendar.DAY_OF_YEAR);
                }).count();

        // Sessões esta semana
        long sessoesSemana = todasSessoes.stream()
                .filter(s -> {
                    Calendar now = Calendar.getInstance();
                    Calendar st = Calendar.getInstance();
                    st.setTime(s.getHorario());
                    return now.get(Calendar.YEAR) == st.get(Calendar.YEAR)
                            && now.get(Calendar.WEEK_OF_YEAR) == st.get(Calendar.WEEK_OF_YEAR);
                }).count();

        // Contar ingressos apenas de sessões DISPONIVEL
        long ingressosReservados = todasSessoes.stream()
                .filter(s -> s.getStatus() == StatusSessao.DISPONIVEL)
                .mapToLong(s -> s.getMapaAssentosDisponiveis().values().stream()
                        .filter(d -> !d)
                        .count()
                ).sum();

        long ingressosDisponiveis = todasSessoes.stream()
                .filter(s -> s.getStatus() == StatusSessao.DISPONIVEL)
                .mapToLong(s -> s.getMapaAssentosDisponiveis().values().stream()
                        .filter(d -> d)
                        .count()
                ).sum();

        return new IndicadoresSessao(
                totalSessoes,
                sessoesDisponiveis,
                sessoesEsgotadas,
                sessoesCanceladas,
                sessoesHoje,
                sessoesSemana,
                Math.round(ocupacaoMedia * 100.0) / 100.0,
                ingressosReservados,
                Math.max(0L, ingressosDisponiveis)
        );
    }

    /**
     * Lista sessões de um filme específico
     */
    public List<SessaoDTO> listarSessoesPorFilme(Integer filmeId) {
        List<Sessao> sessoes = sessaoRepositorio.buscarPorFilme(new FilmeId(filmeId));
        
        // Atualizar status de sessões que já passaram
        sessoes = sessoes.stream()
                .map(this::atualizarStatusSeNecessario)
                .collect(Collectors.toList());
        
        return sessoes.stream()
                .map(this::mapearSessaoParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtém uma sessão por ID
     */
    public SessaoDTO obterSessao(Integer id) {
        Sessao sessao = sessaoRepositorio.obterPorId(new SessaoId(id));
        if (sessao == null) {
            throw new IllegalArgumentException("Sessão não encontrada");
        }
        
        // Atualizar status se necessário
        sessao = atualizarStatusSeNecessario(sessao);
        
        return mapearSessaoParaDTO(sessao);
    }

    /**
     * Obtém mapa de assentos disponíveis de uma sessão
     */
    public AssentosInfo obterAssentos(Integer id) {
        Sessao sessao = sessaoRepositorio.obterPorId(new SessaoId(id));
        if (sessao == null) {
            throw new IllegalArgumentException("Sessão não encontrada");
        }

        Map<String, Boolean> assentosMap = new HashMap<>();
        sessao.getMapaAssentosDisponiveis().forEach((assentoId, disponivel) -> {
            assentosMap.put(assentoId.getValor(), disponivel);
        });

        long disponiveis = assentosMap.values().stream().filter(d -> d).count();

        return new AssentosInfo(id, assentosMap, sessao.getCapacidade(), disponiveis);
    }

    /**
     * Reserva assentos em uma sessão
     */
    public void reservarAssentos(Integer id, List<String> assentos) {
        Sessao sessao = sessaoRepositorio.obterPorId(new SessaoId(id));
        if (sessao == null) {
            throw new IllegalArgumentException("Sessão não encontrada");
        }
        
        // Atualizar status se necessário
        sessao = atualizarStatusSeNecessario(sessao);
        
        // Validar se sessão está disponível para venda
        if (sessao.getStatus() != StatusSessao.DISPONIVEL) {
            throw new IllegalStateException("Esta sessão não está mais disponível para compra de ingressos");
        }

        // Reserva cada assento
        for (String assentoStr : assentos) {
            AssentoId assentoId = new AssentoId(assentoStr);
            sessao.reservarAssento(assentoId);
        }

        // Salva a sessão atualizada
        sessaoRepositorio.salvar(sessao);
    }

    /**
     * Cria uma nova sessão
     */
    public SessaoDTO criarSessao(Integer filmeId, Date horario, Integer salaId) {
        SalaId salaIdObj = new SalaId(salaId);

        // Busca a capacidade real da sala
        Sala sala = salaRepositorio.obterPorId(salaIdObj);
        if (sala == null) {
            throw new IllegalArgumentException("Sala não encontrada com ID: " + salaId);
        }

        int capacidade = sala.getCapacidade();

        Sessao sessao = criarSessaoUseCase.executar(
                new FilmeId(filmeId),
                horario,
                salaIdObj,
                capacidade
        );

        return mapearSessaoParaDTO(sessao);
    }

    /**
     * Modifica uma sessão existente
     */
    public SessaoDTO modificarSessao(Integer id, Date novoHorario, Integer novaSalaId) {
        SalaId novaSala = novaSalaId != null ? new SalaId(novaSalaId) : null;

        modificarSessaoUseCase.executar(
                new SessaoId(id),
                novoHorario,
                novaSala
        );

        Sessao sessaoAtualizada = sessaoRepositorio.obterPorId(new SessaoId(id));
        return mapearSessaoParaDTO(sessaoAtualizada);
    }

    /**
     * Remarca os ingressos de uma sessão para um novo horário
     */
    public ResultadoRemarcacao remarcarIngressos(Integer id, Date novoHorario, List<String> assentosAfetados) {
        RemarcarIngressosSessaoUseCase.RemarcacaoResultado resultado =
                remarcarIngressosSessaoUseCase.executar(
                        new SessaoId(id),
                        novoHorario,
                        assentosAfetados
                );

        return new ResultadoRemarcacao(
                "Ingressos remarcados com sucesso",
                resultado.ingressosRemarcados(),
                resultado.novoHorario()
        );
    }

    /**
     * Remove (cancela) uma sessão
     */
    public void removerSessao(Integer id) {
        removerSessaoUseCase.executar(new SessaoId(id));
    }

    /**
     * Calcula a ocupação percentual de uma sessão baseada na capacidade real da sala
     */
    private double calcularOcupacao(Sessao sessao) {
        // Busca a capacidade real da sala
        Sala sala = salaRepositorio.obterPorId(sessao.getSalaId());
        if (sala == null || sala.getCapacidade() == 0) return 0.0;

        long assentosOcupados = sessao.getMapaAssentosDisponiveis().values().stream()
                .filter(disponivel -> !disponivel)
                .count();

        // Retorna fração entre 0.0 e 1.0 (ex: 0.08 para 8%) usando capacidade real da sala
        return (double) assentosOcupados / (double) sala.getCapacidade();
    }

    /**
     * Mapeia Sessao para DTO
     */
    private SessaoDTO mapearSessaoParaDTO(Sessao sessao) {
        // Busca a capacidade real da sala
        Sala sala = salaRepositorio.obterPorId(sessao.getSalaId());
        int capacidadeReal = sala != null ? sala.getCapacidade() : sessao.getCapacidade();
        String nomeSala = sala != null ? sala.getNome() : "Sala " + sessao.getSalaId().getId();

        // Adiciona informações do filme
        FilmeInfo filmeInfo = null;
        String filmeTitulo = "Filme #" + sessao.getFilmeId().getId();
        try {
            Filme filme = filmeRepositorio.obterPorId(sessao.getFilmeId());
            if (filme != null) {
                filmeInfo = new FilmeInfo(
                        filme.getFilmeId().getId(),
                        filme.getTitulo(),
                        filme.getDuracao(),
                        filme.getClassificacaoEtaria(),
                        filme.getImagemUrl()
                );
                filmeTitulo = filme.getTitulo();
            }
        } catch (Exception ignored) {}

        // Adiciona estatísticas de ocupação baseadas nos assentos realmente cadastrados
        long assentosDisponiveis = sessao.getMapaAssentosDisponiveis().values().stream()
                .filter(disponivel -> disponivel)
                .count();
        long assentosOcupados = sessao.getMapaAssentosDisponiveis().values().stream()
                .filter(disponivel -> !disponivel)
                .count();

        return new SessaoDTO(
                sessao.getSessaoId().getId(),
                sessao.getFilmeId().getId(),
                sessao.getHorario(),
                sessao.getStatus().name(),
                sessao.getSalaId().getId(),
                nomeSala,
                capacidadeReal,
                filmeTitulo,
                filmeInfo,
                assentosDisponiveis,
                assentosOcupados,
                Math.round(calcularOcupacao(sessao) * 100.0) / 100.0
        );
    }

    // Classes de resultado
    public record SessaoDTO(
            int id,
            int filmeId,
            Date horario,
            String status,
            int salaId,
            String sala,
            int capacidade,
            String filmeTitulo,
            FilmeInfo filme,
            long assentosDisponiveis,
            long assentosOcupados,
            double ocupacao
    ) {}

    public record FilmeInfo(int id, String titulo, int duracao, String classificacaoEtaria, String imagemUrl) {}

    public record IndicadoresSessao(
            long total,
            long ativas,
            long esgotadas,
            long canceladas,
            long sessoesHoje,
            long sessoesSemana,
            double ocupacaoMedia,
            long ingressosReservados,
            long ingressosDisponiveis
    ) {}

    public record AssentosInfo(int sessaoId, Map<String, Boolean> assentos, int capacidade, long disponiveis) {}

    public record ResultadoRemarcacao(String mensagem, int ingressosRemarcados, Date novoHorario) {}
}

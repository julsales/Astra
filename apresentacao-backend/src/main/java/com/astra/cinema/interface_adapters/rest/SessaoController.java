package com.astra.cinema.interface_adapters.rest;

import com.astra.cinema.aplicacao.sessao.CriarSessaoUseCase;
import com.astra.cinema.aplicacao.sessao.ModificarSessaoUseCase;
import com.astra.cinema.aplicacao.sessao.RemarcarIngressosSessaoUseCase;
import com.astra.cinema.aplicacao.sessao.RemoverSessaoUseCase;
import com.astra.cinema.aplicacao.usuario.GerenciarCinemaUseCase;
import com.astra.cinema.dominio.comum.FilmeId;
import com.astra.cinema.dominio.comum.SessaoId;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.sessao.StatusSessao;
import com.astra.cinema.dominio.usuario.Cargo;
import com.astra.cinema.dominio.usuario.Funcionario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**

    @PostMapping
    public ResponseEntity<Map<String, Object>> criarSessao(@RequestBody CriarSessaoRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Funcionario funcionario = resolverFuncionario(request.funcionario, request.nomeFuncionario, request.cargoFuncionario);
            gerenciarCinemaUseCase.validarPermissaoGerencial(funcionario, "criar sessões");

            Date horario = converterHorario(request.horario);
            int capacidade = request.capacidadeSala != null ? request.capacidadeSala : 100;
            String sala = request.sala != null && !request.sala.isBlank() ? request.sala : "Sala 1";

            Sessao sessao = criarSessaoUseCase.executar(new FilmeId(request.filmeId), horario, capacidade, sala);
            Filme filme = filmeRepositorio.obterPorId(sessao.getFilmeId());

            response.put("mensagem", "Sessão criada com sucesso");
            response.put("status", "sucesso");
            response.put("sessao", converterParaDTO(sessao, filme));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (SecurityException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_permissao");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (IllegalArgumentException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_validacao");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("mensagem", "Erro interno ao criar sessão: " + e.getMessage());
            response.put("status", "erro_interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> modificarSessao(
            @PathVariable Integer id,
            @RequestBody ModificarSessaoRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Funcionario funcionario = resolverFuncionario(request.funcionario, request.nomeFuncionario, request.cargoFuncionario);
            gerenciarCinemaUseCase.validarPermissaoGerencial(funcionario, "modificar sessões");

            Date novoHorario = converterHorario(request.novoHorario);
            Sessao sessao = modificarSessaoUseCase.executar(new SessaoId(id), novoHorario, request.novaSala);
            Filme filme = filmeRepositorio.obterPorId(sessao.getFilmeId());

            response.put("mensagem", "Sessão modificada com sucesso");
            response.put("status", "sucesso");
            response.put("sessao", converterParaDTO(sessao, filme));
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_permissao");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_validacao");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("mensagem", "Erro interno: " + e.getMessage());
            response.put("status", "erro_interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/{id}/remarcar")
    public ResponseEntity<Map<String, Object>> remarcarIngressos(
            @PathVariable Integer id,
            @RequestBody RemarcarSessaoRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Funcionario funcionario = resolverFuncionario(request.funcionario, request.nomeFuncionario, request.cargoFuncionario);
            gerenciarCinemaUseCase.validarPermissaoGerencial(funcionario, "remarcar ingressos");

            RemarcarIngressosSessaoUseCase.EstrategiaRemarcacao estrategia = converterEstrategia(request.estrategia);
            List<String> assentos = request.assentos != null ? request.assentos : new ArrayList<>();

            RemarcarIngressosSessaoUseCase.RemarcacaoResultado resultado = remarcarIngressosSessaoUseCase.executar(
                    new SessaoId(id),
                    converterHorario(request.novoHorario),
                    estrategia,
                    assentos
            );

            response.put("mensagem", "Ingressos remarcados com sucesso");
            response.put("status", "sucesso");
            response.put("resultado", resultado);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_permissao");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_validacao");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("mensagem", "Erro interno ao remarcar ingressos: " + e.getMessage());
            response.put("status", "erro_interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> removerSessao(
            @PathVariable Integer id,
            @RequestBody(required = false) OperacaoSessaoRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            Funcionario funcionario = resolverFuncionario(
                    request != null ? request.funcionario : null,
                    primeiroNaoVazio(request != null ? request.nomeFuncionario : null, request != null ? request.nome : null),
                    primeiroNaoVazio(request != null ? request.cargoFuncionario : null, request != null ? request.cargo : null));

            gerenciarCinemaUseCase.validarPermissaoGerencial(funcionario, "cancelar sessões");
            removerSessaoUseCase.executar(new SessaoId(id));

            response.put("mensagem", "Sessão cancelada com sucesso");
            response.put("status", "sucesso");
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_permissao");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_validacao");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("mensagem", "Erro interno: " + e.getMessage());
            response.put("status", "erro_interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/filme/{filmeId}/pode-criar")
    public ResponseEntity<Map<String, Boolean>> podeCriar(@PathVariable Integer filmeId) {
        try {
            boolean podeCriar = criarSessaoUseCase.podeCriar(new FilmeId(filmeId));
            Map<String, Boolean> response = new HashMap<>();
            response.put("podeCriar", podeCriar);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public static class CriarSessaoRequest {
        public Integer filmeId;
        public String horario;
        public String sala;
        public Integer capacidadeSala;
        public FuncionarioRequestDTO funcionario;
        public String nomeFuncionario;
        public String cargoFuncionario;
    }

    public static class ModificarSessaoRequest {
        public String novoHorario;
        public String novaSala;
        public FuncionarioRequestDTO funcionario;
        public String nomeFuncionario;
        public String cargoFuncionario;
    }

    public static class RemarcarSessaoRequest {
        public String novoHorario;
        public String estrategia;
        public List<String> assentos;
        public FuncionarioRequestDTO funcionario;
        public String nomeFuncionario;
        public String cargoFuncionario;
    }

    public static class OperacaoSessaoRequest {
        public FuncionarioRequestDTO funcionario;
        public String nomeFuncionario;
        public String cargoFuncionario;
        public String nome;
        public String cargo;
    }

    public record FuncionarioRequestDTO(String nome, String cargo) {}

    public record SessaoDetalhadaDTO(
            Integer id,
            Integer filmeId,
            String filmeTitulo,
            String classificacao,
            Integer duracaoFilme,
            Date horario,
            String sala,
            Integer capacidade,
            Integer assentosDisponiveis,
            Integer assentosReservados,
            double ocupacao,
            String status
    ) {}

    public record IndicadoresSessaoDTO(
            int total,
            int ativas,
            int canceladas,
            int esgotadas,
            int sessoesHoje,
            int sessoesSemana,
            double ocupacaoMedia,
            int ingressosReservados,
            int ingressosDisponiveis
    ) {}

    private SessaoDetalhadaDTO converterParaDTO(Sessao sessao, Filme filme) {
        int assentosDisponiveis = calcularAssentosDisponiveis(sessao);
        int reservados = sessao.getCapacidade() - assentosDisponiveis;
        double ocupacao = sessao.getCapacidade() == 0 ? 0d : (double) reservados / sessao.getCapacidade();

        return new SessaoDetalhadaDTO(
                sessao.getSessaoId().getId(),
                sessao.getFilmeId().getId(),
                filme != null ? filme.getTitulo() : null,
                filme != null ? filme.getClassificacaoEtaria() : null,
                filme != null ? filme.getDuracao() : null,
                sessao.getHorario(),
                sessao.getSala(),
                sessao.getCapacidade(),
                assentosDisponiveis,
                reservados,
                ocupacao,
                sessao.getStatus().toString()
        );
    }

    private int calcularAssentosDisponiveis(Sessao sessao) {
        return (int) sessao.getMapaAssentosDisponiveis().values().stream().filter(Boolean::booleanValue).count();
    }

    private boolean mesmoDia(Date data, LocalDate dia) {
        return data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(dia);
    }

    private StatusSessao converterStatus(String status) {
        if (status == null || status.isBlank() || "TODOS".equalsIgnoreCase(status)) {
            return null;
        }
        try {
            return StatusSessao.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + status, e);
        }
    }

    private RemarcarIngressosSessaoUseCase.EstrategiaRemarcacao converterEstrategia(String estrategia) {
        if (estrategia == null || estrategia.isBlank()) {
            return RemarcarIngressosSessaoUseCase.EstrategiaRemarcacao.MASSA;
        }
        try {
            return RemarcarIngressosSessaoUseCase.EstrategiaRemarcacao.valueOf(estrategia.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estratégia inválida: " + estrategia, e);
        }
    }

    private Date converterHorario(String horario) {
        if (horario == null || horario.isBlank()) {
            throw new IllegalArgumentException("O horário é obrigatório");
        }
        try {
            Instant instant = Instant.parse(horario);
            return Date.from(instant.truncatedTo(ChronoUnit.SECONDS));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de horário inválido. Use ISO 8601", e);
        }
    }

    private Funcionario resolverFuncionario(FuncionarioRequestDTO funcionarioRequestDTO, String nomeAlternativo, String cargoAlternativo) {
        String nome = nomeAlternativo;
        String cargo = cargoAlternativo;

        if (funcionarioRequestDTO != null) {
            if (funcionarioRequestDTO.nome() != null && !funcionarioRequestDTO.nome().isBlank()) {
                nome = funcionarioRequestDTO.nome();
            }
            if (funcionarioRequestDTO.cargo() != null && !funcionarioRequestDTO.cargo().isBlank()) {
                cargo = funcionarioRequestDTO.cargo();
            }
        }

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do funcionário é obrigatório");
        }
        if (cargo == null || cargo.isBlank()) {
            throw new IllegalArgumentException("Cargo do funcionário é obrigatório");
        }

        try {
            return new Funcionario(nome, Cargo.valueOf(cargo.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cargo do funcionário inválido", e);
        }
    }

    private String primeiroNaoVazio(String... valores) {
        if (valores == null) {
            return null;
        }
        for (String valor : valores) {
            if (valor != null && !valor.isBlank()) {
                return valor;
            }
        }
        return null;
    }
}package com.astra.cinema.interface_adapters.rest;

import com.astra.cinema.aplicacao.sessao.CriarSessaoUseCase;
import com.astra.cinema.aplicacao.sessao.ModificarSessaoUseCase;
import com.astra.cinema.aplicacao.sessao.RemarcarIngressosSessaoUseCase;
import com.astra.cinema.aplicacao.sessao.RemoverSessaoUseCase;
import com.astra.cinema.aplicacao.usuario.GerenciarCinemaUseCase;
import com.astra.cinema.dominio.comum.FilmeId;
import com.astra.cinema.dominio.comum.SessaoId;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.sessao.StatusSessao;
import com.astra.cinema.dominio.usuario.Cargo;
import com.astra.cinema.dominio.usuario.Funcionario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para operações de Sessão com foco na jornada do gerente
 */
@RestController
@RequestMapping("/api/sessoes")
@CrossOrigin(origins = "*")
public class SessaoController {

    @Autowired
    private CriarSessaoUseCase criarSessaoUseCase;

    @Autowired
    private ModificarSessaoUseCase modificarSessaoUseCase;

    @Autowired
    private RemoverSessaoUseCase removerSessaoUseCase;

    @Autowired
    private RemarcarIngressosSessaoUseCase remarcarIngressosSessaoUseCase;

    @Autowired
    private SessaoRepositorio sessaoRepositorio;

    @Autowired
    private FilmeRepositorio filmeRepositorio;

    @Autowired
    private GerenciarCinemaUseCase gerenciarCinemaUseCase;

    /**
     * Lista todas as sessões aplicando filtros para o dashboard do gerente
     */
    @GetMapping
    public ResponseEntity<List<SessaoDetalhadaDTO>> listarTodas(
            @RequestParam(value = "filmeId", required = false) Integer filmeId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "apenasAtivas", defaultValue = "false") boolean apenasAtivas) {
        try {
            StatusSessao statusFiltro = converterStatus(status);
            List<Sessao> sessoes = sessaoRepositorio.listarTodas();
            Map<Integer, Filme> filmes = filmeRepositorio.listarTodos().stream()
                    .collect(Collectors.toMap(filme -> filme.getFilmeId().getId(), filme -> filme));

            List<SessaoDetalhadaDTO> resposta = sessoes.stream()
                    .filter(sessao -> filmeId == null || sessao.getFilmeId().getId() == filmeId)
                    .filter(sessao -> statusFiltro == null || sessao.getStatus() == statusFiltro)
                    .filter(sessao -> !apenasAtivas || sessao.getStatus() != StatusSessao.CANCELADA)
                    .sorted(Comparator.comparing(Sessao::getHorario))
                    .map(sessao -> converterParaDTO(sessao, filmes.get(sessao.getFilmeId().getId())))
                    .toList();

            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Indicadores operacionais para cards do dashboard
     */
    @GetMapping("/indicadores")
    public ResponseEntity<IndicadoresSessaoDTO> indicadores() {
        try {
            List<Sessao> sessoes = sessaoRepositorio.listarTodas();
            if (sessoes.isEmpty()) {
                return ResponseEntity.ok(new IndicadoresSessaoDTO(0, 0, 0, 0, 0, 0, 0, 0, 0));
            }

            LocalDate hoje = LocalDate.now();
            LocalDate daquiUmaSemana = hoje.plusDays(7);

            int total = sessoes.size();
            int canceladas = (int) sessoes.stream().filter(s -> s.getStatus() == StatusSessao.CANCELADA).count();
            int esgotadas = (int) sessoes.stream().filter(s -> s.getStatus() == StatusSessao.ESGOTADA).count();
            int ativas = total - canceladas;
            int hojeCount = (int) sessoes.stream().filter(s -> mesmoDia(s.getHorario(), hoje)).count();
            int semanaCount = (int) sessoes.stream()
                    .filter(s -> !s.getHorario().before(Date.from(hoje.atStartOfDay(ZoneId.systemDefault()).toInstant())))
                    .filter(s -> s.getHorario().before(Date.from(daquiUmaSemana.atStartOfDay(ZoneId.systemDefault()).toInstant())))
                    .count();

            int ingressosDisponiveis = sessoes.stream().mapToInt(this::calcularAssentosDisponiveis).sum();
            int capacidadeTotal = sessoes.stream().mapToInt(Sessao::getCapacidade).sum();
            int ingressosReservados = capacidadeTotal - ingressosDisponiveis;
            double ocupacaoMedia = capacidadeTotal == 0 ? 0d : (double) ingressosReservados / capacidadeTotal;

            IndicadoresSessaoDTO dto = new IndicadoresSessaoDTO(
                    total,
                    ativas,
                    canceladas,
                    esgotadas,
                    hojeCount,
                    semanaCount,
                    ocupacaoMedia,
                    ingressosReservados,
                    ingressosDisponiveis
            );
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista sessões por filme (legado)
     */
    @GetMapping("/filme/{filmeId}")
    public ResponseEntity<List<SessaoDetalhadaDTO>> listarPorFilme(@PathVariable Integer filmeId) {
        try {
            List<Sessao> sessoes = sessaoRepositorio.buscarPorFilme(new FilmeId(filmeId));
            Filme filme = filmeRepositorio.obterPorId(new FilmeId(filmeId));
            List<SessaoDetalhadaDTO> resposta = sessoes.stream()
                    .map(sessao -> converterParaDTO(sessao, filme))
                    .toList();
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca sessão por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessaoDetalhadaDTO> buscarPorId(@PathVariable Integer id) {
        try {
            Sessao sessao = sessaoRepositorio.obterPorId(new SessaoId(id));
            if (sessao == null) {
                return ResponseEntity.notFound().build();
            }
            Filme filme = filmeRepositorio.obterPorId(sessao.getFilmeId());
            return ResponseEntity.ok(converterParaDTO(sessao, filme));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cria nova sessão - apenas gerentes
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> criarSessao(@RequestBody CriarSessaoRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Funcionario funcionario = resolverFuncionario(request.funcionario, request.nomeFuncionario, request.cargoFuncionario);
            gerenciarCinemaUseCase.validarPermissaoGerencial(funcionario, "criar sessões");

            Date horario = converterHorario(request.horario);
            int capacidade = request.capacidadeSala != null ? request.capacidadeSala : 100;
            String sala = request.sala != null && !request.sala.isBlank() ? request.sala : "Sala 1";

            Sessao sessao = criarSessaoUseCase.executar(new FilmeId(request.filmeId), horario, capacidade, sala);
            Filme filme = filmeRepositorio.obterPorId(sessao.getFilmeId());

            response.put("mensagem", "Sessão criada com sucesso");
            response.put("status", "sucesso");
            response.put("sessao", converterParaDTO(sessao, filme));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (SecurityException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_permissao");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (IllegalArgumentException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_validacao");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("mensagem", "Erro interno ao criar sessão: " + e.getMessage());
            response.put("status", "erro_interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Modifica horário/sala da sessão
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> modificarSessao(
            @PathVariable Integer id,
            @RequestBody ModificarSessaoRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Funcionario funcionario = resolverFuncionario(request.funcionario, request.nomeFuncionario, request.cargoFuncionario);
            gerenciarCinemaUseCase.validarPermissaoGerencial(funcionario, "modificar sessões");

            Date novoHorario = converterHorario(request.novoHorario);
            Sessao sessao = modificarSessaoUseCase.executar(new SessaoId(id), novoHorario, request.novaSala);
            Filme filme = filmeRepositorio.obterPorId(sessao.getFilmeId());

            response.put("mensagem", "Sessão modificada com sucesso");
            response.put("status", "sucesso");
            response.put("sessao", converterParaDTO(sessao, filme));
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_permissao");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_validacao");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("mensagem", "Erro interno: " + e.getMessage());
            response.put("status", "erro_interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Remarca ingressos de uma sessão afetada
     */
    @PostMapping("/{id}/remarcar")
    public ResponseEntity<Map<String, Object>> remarcarIngressos(
            @PathVariable Integer id,
            @RequestBody RemarcarSessaoRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Funcionario funcionario = resolverFuncionario(request.funcionario, request.nomeFuncionario, request.cargoFuncionario);
            gerenciarCinemaUseCase.validarPermissaoGerencial(funcionario, "remarcar ingressos");

            RemarcarIngressosSessaoUseCase.EstrategiaRemarcacao estrategia = converterEstrategia(request.estrategia);
            List<String> assentos = request.assentos != null ? request.assentos : new ArrayList<>();

            RemarcarIngressosSessaoUseCase.RemarcacaoResultado resultado = remarcarIngressosSessaoUseCase.executar(
                    new SessaoId(id),
                    converterHorario(request.novoHorario),
                    estrategia,
                    assentos
            );

            response.put("mensagem", "Ingressos remarcados com sucesso");
            response.put("status", "sucesso");
            response.put("resultado", resultado);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_permissao");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_validacao");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("mensagem", "Erro interno ao remarcar ingressos: " + e.getMessage());
            response.put("status", "erro_interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Cancela uma sessão
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> removerSessao(
            @PathVariable Integer id,
            @RequestBody(required = false) OperacaoSessaoRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            Funcionario funcionario = resolverFuncionario(
                    request != null ? request.funcionario : null,
                    primeiroNaoVazio(request != null ? request.nomeFuncionario : null, request != null ? request.nome : null),
                    primeiroNaoVazio(request != null ? request.cargoFuncionario : null, request != null ? request.cargo : null));

            gerenciarCinemaUseCase.validarPermissaoGerencial(funcionario, "cancelar sessões");
            removerSessaoUseCase.executar(new SessaoId(id));

            response.put("mensagem", "Sessão cancelada com sucesso");
            response.put("status", "sucesso");
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_permissao");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            response.put("mensagem", e.getMessage());
            response.put("status", "erro_validacao");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("mensagem", "Erro interno: " + e.getMessage());
            response.put("status", "erro_interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Verifica se sessão pode ser criada para filme
     */
    @GetMapping("/filme/{filmeId}/pode-criar")
    public ResponseEntity<Map<String, Boolean>> podeCriar(@PathVariable Integer filmeId) {
        try {
            boolean podeCriar = criarSessaoUseCase.podeCriar(new FilmeId(filmeId));
            Map<String, Boolean> response = new HashMap<>();
            response.put("podeCriar", podeCriar);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== DTOs ====================

    public static class CriarSessaoRequest {
        public Integer filmeId;
        public String horario;
        public String sala;
        public Integer capacidadeSala;
        public FuncionarioRequestDTO funcionario;
        public String nomeFuncionario;
        public String cargoFuncionario;
    }

    public static class ModificarSessaoRequest {
        public String novoHorario;
        public String novaSala;
        public FuncionarioRequestDTO funcionario;
        public String nomeFuncionario;
        public String cargoFuncionario;
    }

    public static class RemarcarSessaoRequest {
        public String novoHorario;
        public String estrategia;
        public List<String> assentos;
        public FuncionarioRequestDTO funcionario;
        public String nomeFuncionario;
        public String cargoFuncionario;
    }

    public static class OperacaoSessaoRequest {
        public FuncionarioRequestDTO funcionario;
        public String nomeFuncionario;
        public String cargoFuncionario;
        public String nome;
        public String cargo;
    }

    public record FuncionarioRequestDTO(String nome, String cargo) {}

    public record SessaoDetalhadaDTO(
            Integer id,
            Integer filmeId,
            String filmeTitulo,
            String classificacao,
            Integer duracaoFilme,
            Date horario,
            String sala,
            Integer capacidade,
            Integer assentosDisponiveis,
            Integer assentosReservados,
            double ocupacao,
            String status
    ) {}

    public record IndicadoresSessaoDTO(
            int total,
            int ativas,
            int canceladas,
            int esgotadas,
            int sessoesHoje,
            int sessoesSemana,
            double ocupacaoMedia,
            int ingressosReservados,
            int ingressosDisponiveis
    ) {}

    // ==================== Helpers ====================

    private SessaoDetalhadaDTO converterParaDTO(Sessao sessao, Filme filme) {
        int assentosDisponiveis = calcularAssentosDisponiveis(sessao);
        int reservados = sessao.getCapacidade() - assentosDisponiveis;
        double ocupacao = sessao.getCapacidade() == 0 ? 0d : (double) reservados / sessao.getCapacidade();

        return new SessaoDetalhadaDTO(
                sessao.getSessaoId().getId(),
                sessao.getFilmeId().getId(),
                filme != null ? filme.getTitulo() : null,
                filme != null ? filme.getClassificacaoEtaria() : null,
                filme != null ? filme.getDuracao() : null,
                sessao.getHorario(),
                sessao.getSala(),
                sessao.getCapacidade(),
                assentosDisponiveis,
                reservados,
                ocupacao,
                sessao.getStatus().toString()
        );
    }

    private int calcularAssentosDisponiveis(Sessao sessao) {
        return (int) sessao.getMapaAssentosDisponiveis().values().stream().filter(Boolean::booleanValue).count();
    }

    private boolean mesmoDia(Date data, LocalDate dia) {
        return data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(dia);
    }

    private StatusSessao converterStatus(String status) {
        if (status == null || status.isBlank() || "TODOS".equalsIgnoreCase(status)) {
            return null;
        }
        try {
            return StatusSessao.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + status, e);
        }
    }

    private RemarcarIngressosSessaoUseCase.EstrategiaRemarcacao converterEstrategia(String estrategia) {
        if (estrategia == null || estrategia.isBlank()) {
            return RemarcarIngressosSessaoUseCase.EstrategiaRemarcacao.MASSA;
        }
        try {
            return RemarcarIngressosSessaoUseCase.EstrategiaRemarcacao.valueOf(estrategia.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estratégia inválida: " + estrategia, e);
        }
    }

    private Date converterHorario(String horario) {
        if (horario == null || horario.isBlank()) {
            throw new IllegalArgumentException("O horário é obrigatório");
        }
        try {
            Instant instant = Instant.parse(horario);
            return Date.from(instant.truncatedTo(ChronoUnit.SECONDS));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de horário inválido. Use ISO 8601", e);
        }
    }

    private Funcionario resolverFuncionario(FuncionarioRequestDTO funcionarioRequestDTO, String nomeAlternativo, String cargoAlternativo) {
        String nome = nomeAlternativo;
        String cargo = cargoAlternativo;

        if (funcionarioRequestDTO != null) {
            if (funcionarioRequestDTO.nome() != null && !funcionarioRequestDTO.nome().isBlank()) {
                nome = funcionarioRequestDTO.nome();
            }
            if (funcionarioRequestDTO.cargo() != null && !funcionarioRequestDTO.cargo().isBlank()) {
                cargo = funcionarioRequestDTO.cargo();
            }
        }

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do funcionário é obrigatório");
        }
        if (cargo == null || cargo.isBlank()) {
            throw new IllegalArgumentException("Cargo do funcionário é obrigatório");
        }

        try {
            return new Funcionario(nome, Cargo.valueOf(cargo.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cargo do funcionário inválido", e);
        }
    }

    private String primeiroNaoVazio(String... valores) {
        if (valores == null) {
            return null;
        }
        for (String valor : valores) {
            if (valor != null && !valor.isBlank()) {
                return valor;
            }
        }
        return null;
    }
}

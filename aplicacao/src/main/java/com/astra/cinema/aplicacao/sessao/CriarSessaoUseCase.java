package com.astra.cinema.aplicacao.sessao;

import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.dominio.sessao.*;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.filme.StatusFilme;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Caso de uso: Criar uma nova sessão de cinema
 * Responsabilidade: Orquestrar a criação de sessão validando o filme
 * 
 * Padrão: Strategy (através das interfaces de repositório)
 * Padrão: Template Method (estrutura do fluxo de criação)
 */
public class CriarSessaoUseCase {
    private final SessaoRepositorio sessaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;
    private static final int CAPACIDADE_PADRAO_SALA = 100;

    public CriarSessaoUseCase(SessaoRepositorio sessaoRepositorio,
                              FilmeRepositorio filmeRepositorio) {
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("O repositório de sessões não pode ser nulo");
        }
        if (filmeRepositorio == null) {
            throw new IllegalArgumentException("O repositório de filmes não pode ser nulo");
        }
        
        this.sessaoRepositorio = sessaoRepositorio;
        this.filmeRepositorio = filmeRepositorio;
    }

    /**
     * Cria uma nova sessão para um filme (versão legacy com mapa de assentos)
     */
    public Sessao executar(FilmeId filmeId, Date horario, Map<AssentoId, Boolean> assentos) {
        if (filmeId == null) {
            throw new IllegalArgumentException("O id do filme não pode ser nulo");
        }
        if (horario == null) {
            throw new IllegalArgumentException("O horário não pode ser nulo");
        }
        
        var filme = filmeRepositorio.obterPorId(filmeId);
        if (filme == null) {
            throw new IllegalArgumentException("Filme não encontrado");
        }
        
        validarStatusFilme(filme);
        
        // ID será gerado automaticamente pelo banco via IDENTITY
        var sessao = new Sessao(null, filmeId, horario, StatusSessao.DISPONIVEL, assentos,
            "Sala 1", assentos != null ? assentos.size() : 0);
        
        // Persiste a sessão e retorna com o ID gerado pelo banco
        return sessaoRepositorio.salvar(sessao);
    }

    /**
     * Cria uma nova sessão para um filme com capacidade específica
     * 
     * @param filmeId ID do filme
     * @param horario Horário da sessão
     * @param capacidadeSala Número de assentos da sala
     * @return Sessão criada
     * @throws IllegalStateException se o filme não estiver em cartaz
     * @throws IllegalArgumentException se os parâmetros forem inválidos
     */
    public Sessao executar(FilmeId filmeId, Date horario, int capacidadeSala) {
        return executar(filmeId, horario, capacidadeSala, "Sala 1");
    }

    public Sessao executar(FilmeId filmeId, Date horario, int capacidadeSala, String sala) {
        // Validação de parâmetros
        validarParametros(filmeId, horario, capacidadeSala);

        // Busca o filme
        Filme filme = filmeRepositorio.obterPorId(filmeId);
        if (filme == null) {
            throw new IllegalArgumentException("Filme não encontrado");
        }

        // Verifica se o filme está em cartaz
        validarStatusFilme(filme);

        // Cria o mapa de assentos disponíveis
        Map<AssentoId, Boolean> mapaAssentos = criarMapaAssentosDisponiveis(capacidadeSala);

        // Cria a sessão (ID será gerado automaticamente pelo banco via IDENTITY)
        Sessao sessao = new Sessao(null, filmeId, horario, StatusSessao.DISPONIVEL, mapaAssentos,
            sala != null && !sala.isBlank() ? sala : "Sala 1", capacidadeSala);

        // Persiste a sessão e retorna com o ID gerado pelo banco
        return sessaoRepositorio.salvar(sessao);
    }

    /**
     * Template Method - Validação de parâmetros
     */
    private void validarParametros(FilmeId filmeId, Date horario, int capacidadeSala) {
        if (filmeId == null) {
            throw new IllegalArgumentException("O id do filme não pode ser nulo");
        }
        if (horario == null) {
            throw new IllegalArgumentException("O horário não pode ser nulo");
        }
        if (capacidadeSala <= 0) {
            throw new IllegalArgumentException("A capacidade da sala deve ser maior que zero");
        }
        
        // Valida se o horário não está no passado
        Date agora = new Date();
        if (horario.before(agora)) {
            throw new IllegalArgumentException("Não é possível criar sessão com horário no passado");
        }
    }

    /**
     * Template Method - Validação do status do filme
     */
    private void validarStatusFilme(Filme filme) {
        if (filme.getStatus() != StatusFilme.EM_CARTAZ) {
            throw new IllegalStateException("Não é possível criar sessão. O filme não está em cartaz (status: " + filme.getStatus() + ")");
        }
    }

    /**
     * Template Method - Criação do mapa de assentos
     */
    private Map<AssentoId, Boolean> criarMapaAssentosDisponiveis(int capacidadeSala) {
        Map<AssentoId, Boolean> mapaAssentos = new HashMap<>();
        
        // Gera assentos no formato A1, A2, ..., B1, B2, etc.
        int assentosPorFileira = 10;
        int numFileiras = (int) Math.ceil((double) capacidadeSala / assentosPorFileira);
        
        for (int fileira = 0; fileira < numFileiras; fileira++) {
            char letraFileira = (char) ('A' + fileira);
            int assentosNestaFileira = Math.min(assentosPorFileira, capacidadeSala - (fileira * assentosPorFileira));
            
            for (int numero = 1; numero <= assentosNestaFileira; numero++) {
                String codigoAssento = letraFileira + String.valueOf(numero);
                mapaAssentos.put(new AssentoId(codigoAssento), true); // true = disponível
            }
        }
        
        return mapaAssentos;
    }

    /**
     * Verifica se uma sessão pode ser criada para um filme
     * 
     * @param filmeId ID do filme
     * @return true se a sessão pode ser criada
     */
    public boolean podeCriar(FilmeId filmeId) {
        if (filmeId == null) {
            return false;
        }

        Filme filme = filmeRepositorio.obterPorId(filmeId);
        return filme != null && filme.getStatus() == StatusFilme.EM_CARTAZ;
    }
}

package com.astra.cinema.dominio.sessao;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirEstado;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirPositivo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirTexto;

import com.astra.cinema.dominio.comum.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Sessao implements Cloneable {
    private final SessaoId sessaoId;
    private FilmeId filmeId;
    private Date horario;
    private StatusSessao status;
    private Map<AssentoId, Boolean> mapaAssentosDisponiveis;
    private final String sala;
    private final int capacidade;

    public Sessao(SessaoId sessaoId, FilmeId filmeId, Date horario, StatusSessao status,
                  Map<AssentoId, Boolean> mapaAssentosDisponiveis, String sala, int capacidade) {
        // NOTA: sessaoId pode ser null durante a criação de uma nova sessão
        // O ID será gerado automaticamente pelo banco de dados (IDENTITY)
        // e preenchido após a persistência
        
        this.sessaoId = sessaoId;
        this.filmeId = exigirNaoNulo(filmeId, "O id do filme não pode ser nulo");
        this.horario = horario;
        this.status = exigirNaoNulo(status, "O status não pode ser nulo");
        this.mapaAssentosDisponiveis = new HashMap<>(mapaAssentosDisponiveis != null ? mapaAssentosDisponiveis : new HashMap<>());
        this.sala = exigirTexto(sala != null ? sala : "Sala 1", "A sala não pode ser vazia");
        int capacidadeDerivada = this.mapaAssentosDisponiveis.size();
        int capacidadeCalculada = capacidade > 0 ? capacidade : capacidadeDerivada;
        this.capacidade = exigirPositivo(capacidadeCalculada > 0 ? capacidadeCalculada : capacidadeDerivada,
            "A capacidade deve ser positiva");
    }

    public Sessao(SessaoId sessaoId, FilmeId filmeId, Date horario, StatusSessao status,
                  Map<AssentoId, Boolean> mapaAssentosDisponiveis) {
        this(
            sessaoId,
            filmeId,
            horario,
            status,
            mapaAssentosDisponiveis,
            "Sala 1",
            mapaAssentosDisponiveis != null && !mapaAssentosDisponiveis.isEmpty()
                    ? mapaAssentosDisponiveis.size()
                    : 100
        );
    }

    public SessaoId getSessaoId() {
        return sessaoId;
    }

    public FilmeId getFilmeId() {
        return filmeId;
    }

    public Date getHorario() {
        return horario;
    }

    public StatusSessao getStatus() {
        return status;
    }

    public Map<AssentoId, Boolean> getMapaAssentosDisponiveis() {
        return new HashMap<>(mapaAssentosDisponiveis);
    }

    public String getSala() {
        return sala;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public boolean assentoDisponivel(AssentoId assentoId) {
        return mapaAssentosDisponiveis.getOrDefault(assentoId, false);
    }

    public void reservarAssento(AssentoId assentoId) {
        exigirEstado(assentoDisponivel(assentoId), "O assento não está disponível");
        mapaAssentosDisponiveis.put(assentoId, false);
        
        // Verifica se todos os assentos foram reservados
        if (mapaAssentosDisponiveis.values().stream().noneMatch(disponivel -> disponivel)) {
            marcarComoEsgotada();
        }
    }

    public void marcarComoEsgotada() {
        // Verifica se ainda há assentos disponíveis
        exigirEstado(mapaAssentosDisponiveis.values().stream().noneMatch(Boolean::booleanValue),
            "Não é possível marcar como esgotada enquanto houver assentos disponíveis");
        this.status = StatusSessao.ESGOTADA;
    }

    @Override
    public Sessao clone() {
        try {
            Sessao cloned = (Sessao) super.clone();
            cloned.mapaAssentosDisponiveis = new HashMap<>(this.mapaAssentosDisponiveis);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}

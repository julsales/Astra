package com.astra.cinema.dominio.sessao;

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

    public Sessao(SessaoId sessaoId, FilmeId filmeId, Date horario, StatusSessao status,
                  Map<AssentoId, Boolean> mapaAssentosDisponiveis) {
        if (sessaoId == null) {
            throw new IllegalArgumentException("O id da sessão não pode ser nulo");
        }
        if (filmeId == null) {
            throw new IllegalArgumentException("O id do filme não pode ser nulo");
        }
        if (status == null) {
            throw new IllegalArgumentException("O status não pode ser nulo");
        }
        
        this.sessaoId = sessaoId;
        this.filmeId = filmeId;
        this.horario = horario;
        this.status = status;
        this.mapaAssentosDisponiveis = new HashMap<>(mapaAssentosDisponiveis != null ? mapaAssentosDisponiveis : new HashMap<>());
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

    public boolean assentoDisponivel(AssentoId assentoId) {
        return mapaAssentosDisponiveis.getOrDefault(assentoId, false);
    }

    public void reservarAssento(AssentoId assentoId) {
        if (!assentoDisponivel(assentoId)) {
            throw new IllegalStateException("O assento não está disponível");
        }
        mapaAssentosDisponiveis.put(assentoId, false);
        
        // Verifica se todos os assentos foram reservados
        if (mapaAssentosDisponiveis.values().stream().noneMatch(disponivel -> disponivel)) {
            marcarComoEsgotada();
        }
    }

    public void marcarComoEsgotada() {
        // Verifica se ainda há assentos disponíveis
        if (mapaAssentosDisponiveis.values().stream().anyMatch(disponivel -> disponivel)) {
            throw new IllegalStateException("Não é possível marcar como esgotada enquanto houver assentos disponíveis");
        }
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

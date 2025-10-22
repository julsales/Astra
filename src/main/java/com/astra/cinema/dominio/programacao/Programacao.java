package com.astra.cinema.dominio.programacao;

import com.astra.cinema.dominio.comum.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Programacao implements Cloneable {
    private final ProgramacaoId programacaoId;
    private Date periodoInicio;
    private Date periodoFim;
    private List<SessaoId> sessoes;

    public Programacao(ProgramacaoId programacaoId, Date periodoInicio, Date periodoFim, List<SessaoId> sessoes) {
        if (programacaoId == null) {
            throw new IllegalArgumentException("O id da programação não pode ser nulo");
        }
        if (periodoInicio == null || periodoFim == null) {
            throw new IllegalArgumentException("O período não pode ser nulo");
        }
        if (sessoes == null || sessoes.isEmpty()) {
            throw new IllegalArgumentException("A programação deve ter pelo menos uma sessão");
        }
        
        this.programacaoId = programacaoId;
        this.periodoInicio = periodoInicio;
        this.periodoFim = periodoFim;
        this.sessoes = new ArrayList<>(sessoes);
    }

    public ProgramacaoId getProgramacaoId() {
        return programacaoId;
    }

    public Date getPeriodoInicio() {
        return periodoInicio;
    }

    public Date getPeriodoFim() {
        return periodoFim;
    }

    public List<SessaoId> getSessoes() {
        return new ArrayList<>(sessoes);
    }

    @Override
    public Programacao clone() {
        try {
            Programacao cloned = (Programacao) super.clone();
            cloned.sessoes = new ArrayList<>(this.sessoes);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}

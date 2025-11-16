package com.astra.cinema.dominio.programacao;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirColecaoNaoVazia;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;

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
        this.programacaoId = exigirNaoNulo(programacaoId, "O id da programação não pode ser nulo");
        this.periodoInicio = exigirNaoNulo(periodoInicio, "O período inicial não pode ser nulo");
        this.periodoFim = exigirNaoNulo(periodoFim, "O período final não pode ser nulo");
        this.sessoes = new ArrayList<>(exigirColecaoNaoVazia(sessoes, "A programação deve ter pelo menos uma sessão"));
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

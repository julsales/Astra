package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.comum.ProgramacaoId;
import com.astra.cinema.dominio.comum.SessaoId;
import com.astra.cinema.dominio.programacao.Programacao;
import com.astra.cinema.dominio.programacao.ProgramacaoRepositorio;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Entidade JPA para Programação
 */
@Entity
@Table(name = "PROGRAMACAO")
class ProgramacaoJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Temporal(TemporalType.DATE)
    @Column(name = "periodo_inicio", nullable = false)
    private Date periodoInicio;

    @Temporal(TemporalType.DATE)
    @Column(name = "periodo_fim", nullable = false)
    private Date periodoFim;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "PROGRAMACAO_SESSAO", joinColumns = @JoinColumn(name = "programacao_id"))
    @Column(name = "sessao_id")
    private List<Integer> sessaoIds = new ArrayList<>();

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(Date periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public Date getPeriodoFim() {
        return periodoFim;
    }

    public void setPeriodoFim(Date periodoFim) {
        this.periodoFim = periodoFim;
    }

    public List<Integer> getSessaoIds() {
        return sessaoIds;
    }

    public void setSessaoIds(List<Integer> sessaoIds) {
        this.sessaoIds = sessaoIds;
    }
}

/**
 * Interface Spring Data JPA para Programação
 */

/**
 * Implementação do repositório de domínio usando JPA
 */
@Repository
class ProgramacaoRepositorioJpaImpl implements ProgramacaoRepositorio {

    private final ProgramacaoJpaRepository repository;

    public ProgramacaoRepositorioJpaImpl(ProgramacaoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void salvar(Programacao programacao) {
        if (programacao == null) {
            throw new IllegalArgumentException("A programação não pode ser nula");
        }

        ProgramacaoJpa jpa = new ProgramacaoJpa();
        if (programacao.getProgramacaoId() != null && programacao.getProgramacaoId().getId() > 0) {
            jpa.setId(programacao.getProgramacaoId().getId());
        }
        jpa.setPeriodoInicio(programacao.getPeriodoInicio());
        jpa.setPeriodoFim(programacao.getPeriodoFim());
        jpa.setSessaoIds(programacao.getSessoes().stream()
                .map(SessaoId::getId)
                .collect(Collectors.toList()));

        repository.save(jpa);
    }

    @Override
    public Programacao obterPorId(ProgramacaoId programacaoId) {
        if (programacaoId == null) {
            throw new IllegalArgumentException("O id da programação não pode ser nulo");
        }

        return repository.findById(programacaoId.getId())
                .map(this::mapearParaDominio)
                .orElse(null);
    }

    @Override
    public List<Programacao> listarProgramacoes() {
        return repository.findAll().stream()
                .map(this::mapearParaDominio)
                .collect(Collectors.toList());
    }

    @Override
    public void remover(ProgramacaoId programacaoId) {
        if (programacaoId == null) {
            throw new IllegalArgumentException("O id da programação não pode ser nulo");
        }
        repository.deleteById(programacaoId.getId());
    }

    private Programacao mapearParaDominio(ProgramacaoJpa jpa) {
        List<SessaoId> sessoes = jpa.getSessaoIds().stream()
                .map(SessaoId::new)
                .collect(Collectors.toList());

        return new Programacao(
                new ProgramacaoId(jpa.getId()),
                jpa.getPeriodoInicio(),
                jpa.getPeriodoFim(),
                sessoes
        );
    }
}

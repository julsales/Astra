package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.comum.FilmeId;
import com.astra.cinema.dominio.comum.SessaoId;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.sessao.StatusSessao;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entidade JPA para Sessão
 */
@Entity
@Table(name = "SESSAO")
class SessaoJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "filme_id", nullable = false)
    private Integer filmeId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date horario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSessao status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sala_id", nullable = false)
    private SalaJpa sala;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "SESSAO_ASSENTO", joinColumns = @JoinColumn(name = "sessao_id"))
    @MapKeyColumn(name = "assento_id")
    @Column(name = "disponivel")
    private Map<String, Boolean> assentosDisponiveis = new HashMap<>();

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFilmeId() {
        return filmeId;
    }

    public void setFilmeId(Integer filmeId) {
        this.filmeId = filmeId;
    }

    public Date getHorario() {
        return horario;
    }

    public void setHorario(Date horario) {
        this.horario = horario;
    }

    public StatusSessao getStatus() {
        return status;
    }

    public void setStatus(StatusSessao status) {
        this.status = status;
    }

    public SalaJpa getSala() {
        return sala;
    }

    public void setSala(SalaJpa sala) {
        this.sala = sala;
    }

    public Map<String, Boolean> getAssentosDisponiveis() {
        return assentosDisponiveis;
    }

    public void setAssentosDisponiveis(Map<String, Boolean> assentosDisponiveis) {
        this.assentosDisponiveis = assentosDisponiveis;
    }
}

/**
 * Implementação do repositório de domínio usando JPA
 * Padrão: Adapter (adapta Spring Data JPA para interface de domínio)
 */
@Repository
class SessaoRepositorioJpaImpl implements SessaoRepositorio {
    
    @Autowired
    private SessaoJpaRepository repository;

    @Autowired
    private SalaJpaRepository salaRepository;

    @Autowired
    private CinemaMapeador mapeador;

    @Override
    public Sessao salvar(Sessao sessao) {
        if (sessao == null) {
            throw new IllegalArgumentException("A sessão não pode ser nula");
        }
        
        // Busca a sala no banco
        SalaJpa sala = salaRepository.findById(sessao.getSalaId().getId())
            .orElseThrow(() -> new IllegalArgumentException("Sala não encontrada: " + sessao.getSalaId().getId()));
        
        SessaoJpa sessaoJpa;
        
        // Se a sessão já existe, busca do banco para manter o contexto JPA
        if (sessao.getSessaoId() != null && sessao.getSessaoId().getId() > 0) {
            sessaoJpa = repository.findById(sessao.getSessaoId().getId())
                    .orElse(new SessaoJpa());
            
            // Atualiza os campos da entidade existente
            sessaoJpa.setFilmeId(sessao.getFilmeId().getId());
            sessaoJpa.setHorario(sessao.getHorario());
            sessaoJpa.setStatus(sessao.getStatus());
            sessaoJpa.setSala(sala);
            
            // IMPORTANTE: Limpa o mapa antigo e adiciona o novo
            sessaoJpa.getAssentosDisponiveis().clear();
            Map<String, Boolean> assentosJpa = sessao.getMapaAssentosDisponiveis().entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                        entry -> entry.getKey().getValor(),
                        java.util.Map.Entry::getValue
                    ));
            sessaoJpa.getAssentosDisponiveis().putAll(assentosJpa);
        } else {
            // Nova sessão - usa o mapeador normalmente
            sessaoJpa = mapeador.mapearParaSessaoJpa(sessao, sala);
        }
        
        SessaoJpa sessaoSalva = repository.save(sessaoJpa);
        
        // Retorna a sessão com o ID gerado pelo banco
        return mapeador.mapearParaSessao(sessaoSalva);
    }

    @Override
    public Sessao obterPorId(SessaoId sessaoId) {
        if (sessaoId == null) {
            throw new IllegalArgumentException("O id da sessão não pode ser nulo");
        }
        
        return repository.findById(sessaoId.getId())
                .map(mapeador::mapearParaSessao)
                .orElse(null);
    }

    @Override
    public List<Sessao> buscarPorFilme(FilmeId filmeId) {
        if (filmeId == null) {
            throw new IllegalArgumentException("O id do filme não pode ser nulo");
        }
        
        return repository.findByFilmeId(filmeId.getId()).stream()
                .map(mapeador::mapearParaSessao)
                .toList();
    }

    /**
     * Busca sessões futuras para um filme específico
     */
    public List<Sessao> buscarSessoesFuturas(FilmeId filmeId) {
        if (filmeId == null) {
            throw new IllegalArgumentException("O id do filme não pode ser nulo");
        }
        
        Date agora = new Date();
        return repository.findSessoesFuturasPorFilme(filmeId.getId(), agora).stream()
                .map(mapeador::mapearParaSessao)
                .toList();
    }

    @Override
    public List<Sessao> listarTodas() {
        return repository.findAll().stream()
                .map(mapeador::mapearParaSessao)
                .toList();
    }
}

package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.comum.AssentoId;
import com.astra.cinema.dominio.comum.FilmeId;
import com.astra.cinema.dominio.comum.SessaoId;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.sessao.StatusSessao;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
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

    public Map<String, Boolean> getAssentosDisponiveis() {
        return assentosDisponiveis;
    }

    public void setAssentosDisponiveis(Map<String, Boolean> assentosDisponiveis) {
        this.assentosDisponiveis = assentosDisponiveis;
    }
}

/**
 * Interface Spring Data JPA para Sessão
 */
interface SessaoJpaRepository extends JpaRepository<SessaoJpa, Integer> {
    List<SessaoJpa> findByFilmeId(Integer filmeId);
    
    @Query("SELECT s FROM SessaoJpa s WHERE s.filmeId = :filmeId AND s.horario > :dataAtual")
    List<SessaoJpa> findSessoesFuturasPorFilme(@Param("filmeId") Integer filmeId, @Param("dataAtual") Date dataAtual);
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
    private CinemaMapeador mapeador;

    @Override
    public Sessao salvar(Sessao sessao) {
        if (sessao == null) {
            throw new IllegalArgumentException("A sessão não pode ser nula");
        }
        
        SessaoJpa sessaoJpa = mapeador.mapearParaSessaoJpa(sessao);
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
}

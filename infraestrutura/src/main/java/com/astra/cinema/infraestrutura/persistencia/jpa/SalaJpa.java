package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.comum.SalaId;
import com.astra.cinema.dominio.sessao.Sala;
import com.astra.cinema.dominio.sessao.SalaRepositorio;
import com.astra.cinema.dominio.sessao.TipoSala;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Entidade JPA para Sala
 */
@Entity
@Table(name = "SALA")
class SalaJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private Integer capacidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSala tipo;

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public TipoSala getTipo() {
        return tipo;
    }

    public void setTipo(TipoSala tipo) {
        this.tipo = tipo;
    }
}

/**
 * Repositório Spring Data JPA para Sala
 */

/**
 * Implementação do repositório de domínio para Sala usando JPA
 * Padrão: Adapter (adapta Spring Data JPA para interface de domínio)
 */
@Repository
class SalaRepositorioJpa implements SalaRepositorio {
    
    @Autowired
    private SalaJpaRepository repository;

    @Override
    public Sala salvar(Sala sala) {
        if (sala == null) {
            throw new IllegalArgumentException("A sala não pode ser nula");
        }
        
        SalaJpa salaJpa = new SalaJpa();
        if (sala.getSalaId() != null) {
            salaJpa.setId(sala.getSalaId().getId());
        }
        salaJpa.setNome(sala.getNome());
        salaJpa.setCapacidade(sala.getCapacidade());
        salaJpa.setTipo(sala.getTipo());
        
        SalaJpa salva = repository.save(salaJpa);
        return mapearParaDominio(salva);
    }

    @Override
    public Sala obterPorId(SalaId salaId) {
        if (salaId == null) {
            throw new IllegalArgumentException("O id da sala não pode ser nulo");
        }
        
        return repository.findById(salaId.getId())
                .map(this::mapearParaDominio)
                .orElse(null);
    }

    @Override
    public Sala obterPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome da sala não pode ser nulo ou vazio");
        }
        
        return repository.findByNome(nome)
                .map(this::mapearParaDominio)
                .orElse(null);
    }

    @Override
    public List<Sala> listarTodas() {
        return repository.findAll().stream()
                .map(this::mapearParaDominio)
                .toList();
    }

    @Override
    public void remover(SalaId salaId) {
        if (salaId == null) {
            throw new IllegalArgumentException("O id da sala não pode ser nulo");
        }
        
        repository.deleteById(salaId.getId());
    }

    /**
     * Mapeia entidade JPA para entidade de domínio
     */
    private Sala mapearParaDominio(SalaJpa salaJpa) {
        SalaId salaId = new SalaId(salaJpa.getId());
        return new Sala(salaId, salaJpa.getNome(), salaJpa.getCapacidade(), salaJpa.getTipo());
    }
}

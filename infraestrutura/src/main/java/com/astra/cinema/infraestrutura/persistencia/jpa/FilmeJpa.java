package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.comum.FilmeId;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.filme.StatusFilme;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Entidade JPA para Filme
 * Separada da entidade de domínio para manter a arquitetura limpa
 */
@Entity
@Table(name = "FILME")
class FilmeJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 1000)
    private String sinopse;

    @Column(name = "classificacao_etaria")
    private String classificacaoEtaria;

    @Column(nullable = false)
    private Integer duracao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusFilme status;

    @Column(name = "imagem_url", length = 1000)
    private String imagemUrl;

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public String getClassificacaoEtaria() {
        return classificacaoEtaria;
    }

    public void setClassificacaoEtaria(String classificacaoEtaria) {
        this.classificacaoEtaria = classificacaoEtaria;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public StatusFilme getStatus() {
        return status;
    }

    public void setStatus(StatusFilme status) {
        this.status = status;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }
}

/**
 * Interface Spring Data JPA para Filme
 */

/**
 * Implementação do repositório de domínio usando JPA
 * Padrão: Adapter (adapta Spring Data JPA para interface de domínio)
 */
@Repository
class FilmeRepositorioJpaImpl implements FilmeRepositorio {
    
    @Autowired
    private FilmeJpaRepository repository;

    @Autowired
    private CinemaMapeador mapeador;

    @Override
    public Filme salvar(Filme filme) {
        if (filme == null) {
            throw new IllegalArgumentException("O filme não pode ser nulo");
        }
        
        FilmeJpa filmeJpa = mapeador.mapearParaFilmeJpa(filme);
        FilmeJpa filmeSalvo = repository.save(filmeJpa);
        
        // Retorna o filme com o ID gerado pelo banco
        return mapeador.mapearParaFilme(filmeSalvo);
    }

    @Override
    public Filme obterPorId(FilmeId filmeId) {
        if (filmeId == null) {
            throw new IllegalArgumentException("O id do filme não pode ser nulo");
        }
        
        return repository.findById(filmeId.getId())
                .map(mapeador::mapearParaFilme)
                .orElse(null);
    }

    @Override
    public List<Filme> listarFilmesEmCartaz() {
        return repository.findByStatus(StatusFilme.EM_CARTAZ).stream()
                .map(mapeador::mapearParaFilme)
                .toList();
    }

    @Override
    public List<Filme> listarTodos() {
        return repository.findAll().stream()
                .map(mapeador::mapearParaFilme)
                .toList();
    }
}

package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.usuario.Cargo;
import jakarta.persistence.*;

/**
 * Entidade JPA para Funcionário
 */
@Entity
@Table(name = "FUNCIONARIO")
class FuncionarioJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cargo cargo;

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

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }
}

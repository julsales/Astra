package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.usuario.TipoUsuario;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class UsuarioJpa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipo;

    // Construtor padrão JPA
    protected UsuarioJpa() {}

    public UsuarioJpa(Integer id, String email, String senha, String nome, TipoUsuario tipo) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.nome = nome;
        this.tipo = tipo;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }
}

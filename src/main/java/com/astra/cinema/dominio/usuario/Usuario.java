package com.astra.cinema.dominio.usuario;

public class Usuario {
    private UsuarioId id;
    private String email;
    private String senha;
    private String nome;
    private TipoUsuario tipo;

    public Usuario(UsuarioId id, String email, String senha, String nome, TipoUsuario tipo) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O e-mail do usuário não pode ser nulo ou vazio");
        }
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("A senha do usuário não pode ser nula ou vazia");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do usuário não pode ser nulo ou vazio");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo do usuário não pode ser nulo");
        }

        this.id = id;
        this.email = email;
        this.senha = senha;
        this.nome = nome;
        this.tipo = tipo;
    }

    // Getters
    public UsuarioId getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getNome() {
        return nome;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public boolean verificarSenha(String senhaFornecida) {
        return this.senha.equals(senhaFornecida);
    }
}

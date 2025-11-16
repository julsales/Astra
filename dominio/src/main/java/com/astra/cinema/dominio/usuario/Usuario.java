package com.astra.cinema.dominio.usuario;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirTexto;

public class Usuario {
    private UsuarioId id;
    private String email;
    private String senha;
    private String nome;
    private TipoUsuario tipo;

    public Usuario(UsuarioId id, String email, String senha, String nome, TipoUsuario tipo) {
        this.id = id;
        this.email = exigirTexto(email, "O e-mail do usuário não pode ser nulo ou vazio");
        this.senha = exigirTexto(senha, "A senha do usuário não pode ser nula ou vazia");
        this.nome = exigirTexto(nome, "O nome do usuário não pode ser nulo ou vazio");
        this.tipo = exigirNaoNulo(tipo, "O tipo do usuário não pode ser nulo");
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
        return this.senha.equals(exigirTexto(senhaFornecida, "A senha informada não pode ser nula ou vazia"));
    }
}

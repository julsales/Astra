package com.astra.cinema.aplicacao.usuario;

public class UsuarioDTO {
    private Integer id;
    private String email;
    private String nome;
    private String tipo;

    public UsuarioDTO(Integer id, String email, String nome, String tipo) {
        this.id = id;
        this.email = email;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}

package com.astra.cinema.dominio.filme;

import com.astra.cinema.dominio.comum.FilmeId;

public class Filme implements Cloneable {
    private final FilmeId filmeId;
    private String titulo;
    private String sinopse;
    private String classificacaoEtaria;
    private int duracao;
    private StatusFilme status;

    public Filme(FilmeId filmeId, String titulo, String sinopse, String classificacaoEtaria,
                int duracao, StatusFilme status) {
        if (filmeId == null) {
            throw new IllegalArgumentException("O id do filme não pode ser nulo");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título não pode ser nulo ou vazio");
        }
        if (status == null) {
            throw new IllegalArgumentException("O status não pode ser nulo");
        }
        
        this.filmeId = filmeId;
        this.titulo = titulo;
        this.sinopse = sinopse;
        this.classificacaoEtaria = classificacaoEtaria;
        this.duracao = duracao;
        this.status = status;
    }

    public FilmeId getFilmeId() {
        return filmeId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSinopse() {
        return sinopse;
    }

    public String getClassificacaoEtaria() {
        return classificacaoEtaria;
    }

    public int getDuracao() {
        return duracao;
    }

    public StatusFilme getStatus() {
        return status;
    }

    public void remover() {
        if (status == StatusFilme.RETIRADO) {
            throw new IllegalStateException("O filme já foi retirado");
        }
        this.status = StatusFilme.RETIRADO;
    }

    @Override
    public Filme clone() {
        try {
            return (Filme) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}

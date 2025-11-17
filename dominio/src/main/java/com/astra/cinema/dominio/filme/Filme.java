package com.astra.cinema.dominio.filme;

import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirEstado;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirNaoNulo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirPositivo;
import static com.astra.cinema.dominio.comum.ValidacaoDominio.exigirTexto;

import com.astra.cinema.dominio.comum.FilmeId;

public class Filme implements Cloneable {
    private final FilmeId filmeId;
    private String titulo;
    private String sinopse;
    private String classificacaoEtaria;
    private int duracao;
    private String imagemUrl;
    private StatusFilme status;

    public Filme(FilmeId filmeId, String titulo, String sinopse, String classificacaoEtaria,
                int duracao, String imagemUrl, StatusFilme status) {
        // NOTA: filmeId pode ser null durante a criação de um novo filme
        // O ID será gerado automaticamente pelo banco de dados (IDENTITY)
        // e preenchido após a persistência
        
        this.filmeId = filmeId;
        this.titulo = exigirTexto(titulo, "O título não pode ser nulo ou vazio");
        this.sinopse = sinopse;
        this.classificacaoEtaria = classificacaoEtaria;
        this.duracao = exigirPositivo(duracao, "A duração deve ser positiva");
        this.imagemUrl = normalizarImagem(imagemUrl);
        this.status = exigirNaoNulo(status, "O status não pode ser nulo");
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

    public String getImagemUrl() {
        return imagemUrl;
    }

    public StatusFilme getStatus() {
        return status;
    }

    public void remover() {
        exigirEstado(status != StatusFilme.RETIRADO, "O filme já foi retirado");
        this.status = StatusFilme.RETIRADO;
    }

    public void retirarDeCartaz() {
        this.remover();
    }

    private String normalizarImagem(String imagemUrl) {
        if (imagemUrl == null) {
            return null;
        }
        String valor = imagemUrl.trim();
        return valor.isEmpty() ? null : valor;
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

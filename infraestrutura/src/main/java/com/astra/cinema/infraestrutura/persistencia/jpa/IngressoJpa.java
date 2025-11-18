package com.astra.cinema.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;

/**
 * Entidade JPA para Ingresso
 * Representa um ingresso no banco de dados
 */
@Entity
@Table(name = "ingresso")
public class IngressoJpa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "compra_id", nullable = false)
    private Integer compraId;
    
    @Column(name = "sessao_id", nullable = false)
    private Integer sessaoId;
    
    @Column(name = "assento", nullable = false, length = 10)
    private String assento;
    
    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    
    @Column(name = "qr_code", length = 100, unique = true)
    private String qrCode;

    // Construtor padrão (obrigatório para JPA)
    public IngressoJpa() {
    }

    // Construtor com parâmetros
    public IngressoJpa(Integer id, Integer compraId, Integer sessaoId, String assento, String tipo, String status, String qrCode) {
        this.id = id;
        this.compraId = compraId;
        this.sessaoId = sessaoId;
        this.assento = assento;
        this.tipo = tipo;
        this.status = status;
        this.qrCode = qrCode;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompraId() {
        return compraId;
    }

    public void setCompraId(Integer compraId) {
        this.compraId = compraId;
    }

    public Integer getSessaoId() {
        return sessaoId;
    }

    public void setSessaoId(Integer sessaoId) {
        this.sessaoId = sessaoId;
    }

    public String getAssento() {
        return assento;
    }

    public void setAssento(String assento) {
        this.assento = assento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}


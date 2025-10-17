package com.astra.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendas_bomboniere")
public class VendaBomboniere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL)
    private List<ItemVenda> itens = new ArrayList<>();

    @OneToOne(mappedBy = "vendaBomboniere", cascade = CascadeType.ALL)
    private Pagamento pagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVenda status = StatusVenda.PENDENTE;

    @Column(name = "data_venda", nullable = false)
    private LocalDateTime dataVenda;

    @Column(nullable = false)
    private BigDecimal valorTotal;

    @PrePersist
    protected void onCreate() {
        dataVenda = LocalDateTime.now();
    }

    // Construtores
    public VendaBomboniere() {
    }

    public VendaBomboniere(Funcionario funcionario, BigDecimal valorTotal) {
        this.funcionario = funcionario;
        this.valorTotal = valorTotal;
    }

    // Métodos de negócio
    public void confirmar() {
        if (this.pagamento == null || this.pagamento.getStatus() != Pagamento.StatusPagamento.SUCESSO) {
            throw new IllegalStateException("Pagamento não foi autorizado");
        }
        this.status = StatusVenda.CONFIRMADA;
    }

    public void adicionarItem(ItemVenda item) {
        this.itens.add(item);
        item.setVenda(this);
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
        if (pagamento != null) {
            pagamento.setVendaBomboniere(this);
        }
    }

    public StatusVenda getStatus() {
        return status;
    }

    public void setStatus(StatusVenda status) {
        this.status = status;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public enum StatusVenda {
        PENDENTE,
        CONFIRMADA,
        CANCELADA
    }
}

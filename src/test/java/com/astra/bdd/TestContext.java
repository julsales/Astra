package com.astra.bdd;

import com.astra.model.*;
import com.astra.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestContext {

    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private FuncionarioRepository funcionarioRepository;

    // Estado compartilhado entre steps
    private Cliente cliente;
    private Funcionario funcionario;
    private Filme filme;
    private Sala sala;
    private Sessao sessao;
    private Compra compra;
    private Pagamento pagamento;
    private List<Assento> assentos = new ArrayList<>();
    private Produto produto;
    private VendaBomboniere vendaBomboniere;
    private Programacao programacao;
    private List<Sessao> sessoes = new ArrayList<>();
    private Exception exception;
    private String mensagemErro;

    // Métodos para inicializar dados padrão
    public void inicializarClientePadrao() {
        if (cliente == null) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String cpf = timestamp.substring(timestamp.length() - 11); // últimos 11 dígitos
            cliente = new Cliente("cliente" + timestamp + "@teste.com", "senha123", "João Silva", cpf);
            cliente = clienteRepository.save(cliente);
        }
    }

    public void inicializarFuncionarioPadrao() {
        if (funcionario == null) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String matricula = "FUNC" + timestamp.substring(timestamp.length() - 6);
            funcionario = new Funcionario("gerente" + timestamp + "@teste.com", "senha123", "Maria Gerente", matricula, "GERENTE");
            funcionario.setSalario(5000.0);
            funcionario = funcionarioRepository.save(funcionario);
        }
    }

    // Getters e Setters
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

    public Filme getFilme() {
        return filme;
    }

    public void setFilme(Filme filme) {
        this.filme = filme;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Sessao getSessao() {
        return sessao;
    }

    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public List<Assento> getAssentos() {
        return assentos;
    }

    public void setAssentos(List<Assento> assentos) {
        this.assentos = assentos;
    }

    public Assento getAssentoPorIdentificacao(String identificacao) {
        return assentos.stream()
            .filter(a -> a.getIdentificacao().equals(identificacao))
            .findFirst()
            .orElse(null);
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public VendaBomboniere getVendaBomboniere() {
        return vendaBomboniere;
    }

    public void setVendaBomboniere(VendaBomboniere vendaBomboniere) {
        this.vendaBomboniere = vendaBomboniere;
    }

    public Programacao getProgramacao() {
        return programacao;
    }

    public void setProgramacao(Programacao programacao) {
        this.programacao = programacao;
    }

    public List<Sessao> getSessoes() {
        return sessoes;
    }

    public void setSessoes(List<Sessao> sessoes) {
        this.sessoes = sessoes;
    }

    public void adicionarSessao(Sessao sessao) {
        this.sessoes.add(sessao);
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
        if (exception != null) {
            this.mensagemErro = exception.getMessage();
        }
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }

    public void limparContexto() {
        cliente = null;
        funcionario = null;
        filme = null;
        sala = null;
        sessao = null;
        compra = null;
        pagamento = null;
        assentos.clear();
        produto = null;
        vendaBomboniere = null;
        programacao = null;
        sessoes.clear();
        exception = null;
        mensagemErro = null;
    }
    
    // Método auxiliar para gerar IDs únicos baseados em timestamp
    public String gerarIdUnico() {
        return String.valueOf(System.currentTimeMillis());
    }
    
    public String gerarNumeroSalaUnico() {
        return "S" + System.currentTimeMillis();
    }
}

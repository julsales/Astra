package com.astra.cinema.aplicacao.usuario;

import com.astra.cinema.dominio.comum.ClienteId;
import com.astra.cinema.dominio.usuario.Cliente;
import com.astra.cinema.dominio.usuario.ClienteRepositorio;
import com.astra.cinema.dominio.usuario.Usuario;
import com.astra.cinema.dominio.usuario.UsuarioRepositorio;
import com.astra.cinema.dominio.usuario.TipoUsuario;

import java.util.Optional;

/**
 * Caso de uso para registrar um novo cliente no sistema.
 * Cria tanto o usuário (para login) quanto o cliente (para compras).
 */
public class RegistrarClienteUseCase {

    private final UsuarioRepositorio usuarioRepositorio;
    private final ClienteRepositorio clienteRepositorio;

    public RegistrarClienteUseCase(
            UsuarioRepositorio usuarioRepositorio,
            ClienteRepositorio clienteRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.clienteRepositorio = clienteRepositorio;
    }

    public ResultadoRegistro executar(DadosRegistro dados) {
        // Validar dados
        validarDados(dados);

        // Verificar se o email já está em uso
        Optional<Usuario> usuarioExistente = usuarioRepositorio.buscarPorEmail(dados.getEmail());
        if (usuarioExistente.isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Verificar se o CPF já está em uso (se fornecido)
        if (dados.getCpf() != null && !dados.getCpf().trim().isEmpty()) {
            Cliente clienteExistente = clienteRepositorio.obterPorCpf(dados.getCpf());
            if (clienteExistente != null) {
                throw new IllegalArgumentException("CPF já cadastrado");
            }
        }

        // Criar o usuário para login
        Usuario novoUsuario = new Usuario(
                null, // ID será gerado pelo banco
                dados.getEmail(),
                dados.getSenha(), // Em produção, deve ser criptografada
                dados.getNome(),
                TipoUsuario.CLIENTE
        );

        // Salvar o usuário
        usuarioRepositorio.salvar(novoUsuario);

        // Buscar o usuário salvo para pegar o ID gerado
        Optional<Usuario> usuarioSalvoOpt = usuarioRepositorio.buscarPorEmail(dados.getEmail());
        if (!usuarioSalvoOpt.isPresent()) {
            throw new IllegalStateException("Erro ao criar usuário");
        }
        
        Usuario usuarioSalvo = usuarioSalvoOpt.get();

        // Criar o cliente (para compras)
        Cliente novoCliente = new Cliente(
                new ClienteId(usuarioSalvo.getId().getValor()),
                dados.getNome(),
                dados.getEmail(),
                dados.getCpf()
        );

        // Salvar o cliente
        clienteRepositorio.salvar(novoCliente);

        return new ResultadoRegistro(usuarioSalvo, novoCliente);
    }

    private void validarDados(DadosRegistro dados) {
        if (dados.getNome() == null || dados.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        if (dados.getEmail() == null || dados.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }

        if (!dados.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido");
        }

        if (dados.getSenha() == null || dados.getSenha().length() < 6) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
        }

        // Validar CPF se fornecido
        if (dados.getCpf() != null && !dados.getCpf().trim().isEmpty()) {
            String cpfLimpo = dados.getCpf().replaceAll("[^0-9]", "");
            if (cpfLimpo.length() != 11) {
                throw new IllegalArgumentException("CPF deve conter 11 dígitos");
            }
            if (!validarCpf(cpfLimpo)) {
                throw new IllegalArgumentException("CPF inválido");
            }
        }
    }

    private boolean validarCpf(String cpf) {
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");

        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Verifica se todos os dígitos são iguais (CPF inválido)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Validação do primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) {
            primeiroDigito = 0;
        }
        if (primeiroDigito != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }

        // Validação do segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) {
            segundoDigito = 0;
        }
        return segundoDigito == Character.getNumericValue(cpf.charAt(10));
    }

    // Classes auxiliares
    public static class DadosRegistro {
        private final String nome;
        private final String email;
        private final String senha;
        private final String cpf;

        public DadosRegistro(String nome, String email, String senha, String cpf) {
            this.nome = nome;
            this.email = email;
            this.senha = senha;
            this.cpf = cpf;
        }

        // Construtor para compatibilidade (sem CPF)
        public DadosRegistro(String nome, String email, String senha) {
            this(nome, email, senha, null);
        }

        public String getNome() {
            return nome;
        }

        public String getEmail() {
            return email;
        }

        public String getSenha() {
            return senha;
        }

        public String getCpf() {
            return cpf;
        }
    }

    public static class ResultadoRegistro {
        private final Usuario usuario;
        private final Cliente cliente;

        public ResultadoRegistro(Usuario usuario, Cliente cliente) {
            this.usuario = usuario;
            this.cliente = cliente;
        }

        public Usuario getUsuario() {
            return usuario;
        }

        public Cliente getCliente() {
            return cliente;
        }
    }
}

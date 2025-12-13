package com.astra.cinema.aplicacao.usuario;

import com.astra.cinema.dominio.usuario.Usuario;
import com.astra.cinema.dominio.usuario.UsuarioRepositorio;
import com.astra.cinema.dominio.usuario.ClienteRepositorio;
import com.astra.cinema.dominio.usuario.Cliente;
import com.astra.cinema.dominio.usuario.FuncionarioRepositorio;
import com.astra.cinema.dominio.usuario.Funcionario;

import java.util.Optional;

public class AutenticarUsuarioUseCase {

    private final UsuarioRepositorio usuarioRepositorio;
    private final FuncionarioRepositorio funcionarioRepositorio;
    private final ClienteRepositorio clienteRepositorio;

    public AutenticarUsuarioUseCase(UsuarioRepositorio usuarioRepositorio) {
        this(usuarioRepositorio, null, null);
    }

    public AutenticarUsuarioUseCase(UsuarioRepositorio usuarioRepositorio, FuncionarioRepositorio funcionarioRepositorio) {
        this(usuarioRepositorio, funcionarioRepositorio, null);
    }

    public AutenticarUsuarioUseCase(UsuarioRepositorio usuarioRepositorio, 
                                   FuncionarioRepositorio funcionarioRepositorio,
                                   ClienteRepositorio clienteRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.funcionarioRepositorio = funcionarioRepositorio;
        this.clienteRepositorio = clienteRepositorio;
    }

    public ResultadoAutenticacao executar(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.buscarPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioOpt.get();
        
        if (!usuario.verificarSenha(senha)) {
            return null;
        }

        String cargo = null;
        if (usuario.getTipo().name().equalsIgnoreCase("FUNCIONARIO") && funcionarioRepositorio != null) {
            try {
                Optional<Funcionario> funcionarioOpt = funcionarioRepositorio.buscarPorNome(usuario.getNome());
                if (funcionarioOpt.isPresent()) {
                    Funcionario f = funcionarioOpt.get();
                    cargo = f.getCargo() != null ? f.getCargo().name() : null;
                }
            } catch (Exception e) {
                // não falhar a autenticação por erro na busca de cargo
            }
        }

        // Busca clienteId se for cliente
        Integer clienteId = null;
        if (usuario.getTipo() == com.astra.cinema.dominio.usuario.TipoUsuario.CLIENTE && clienteRepositorio != null) {
            try {
                Cliente cliente = clienteRepositorio.obterPorEmail(usuario.getEmail());
                if (cliente != null && cliente.getClienteId() != null) {
                    clienteId = cliente.getClienteId().getId();
                }
            } catch (Exception e) {
                // Não falhar autenticação por erro na busca de cliente
            }
        }

        return new ResultadoAutenticacao(usuario, cargo, clienteId);
    }
    
    public static class ResultadoAutenticacao {
        private final Usuario usuario;
        private final String cargo;
        private final Integer clienteId;
        
        public ResultadoAutenticacao(Usuario usuario, String cargo, Integer clienteId) {
            this.usuario = usuario;
            this.cargo = cargo;
            this.clienteId = clienteId;
        }
        
        public Usuario getUsuario() {
            return usuario;
        }
        
        public String getCargo() {
            return cargo;
        }
        
        public Integer getClienteId() {
            return clienteId;
        }
    }
}

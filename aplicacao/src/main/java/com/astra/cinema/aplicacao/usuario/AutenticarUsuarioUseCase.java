package com.astra.cinema.aplicacao.usuario;

import com.astra.cinema.dominio.usuario.Usuario;
import com.astra.cinema.dominio.usuario.UsuarioRepositorio;
import com.astra.cinema.dominio.usuario.FuncionarioRepositorio;
import com.astra.cinema.dominio.usuario.Funcionario;

import java.util.Optional;

public class AutenticarUsuarioUseCase {

    private final UsuarioRepositorio usuarioRepositorio;
    private final FuncionarioRepositorio funcionarioRepositorio;

    public AutenticarUsuarioUseCase(UsuarioRepositorio usuarioRepositorio) {
        this(usuarioRepositorio, null);
    }

    public AutenticarUsuarioUseCase(UsuarioRepositorio usuarioRepositorio, FuncionarioRepositorio funcionarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.funcionarioRepositorio = funcionarioRepositorio;
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

        return new ResultadoAutenticacao(usuario, cargo);
    }
    
    public static class ResultadoAutenticacao {
        private final Usuario usuario;
        private final String cargo;
        
        public ResultadoAutenticacao(Usuario usuario, String cargo) {
            this.usuario = usuario;
            this.cargo = cargo;
        }
        
        public Usuario getUsuario() {
            return usuario;
        }
        
        public String getCargo() {
            return cargo;
        }
    }
}

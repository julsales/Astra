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

    public Optional<UsuarioDTO> executar(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.buscarPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = usuarioOpt.get();
        
        if (!usuario.verificarSenha(senha)) {
            return Optional.empty();
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

        return Optional.of(new UsuarioDTO(
                usuario.getId().getValor(),
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getTipo().name(),
                cargo
        ));
    }
}

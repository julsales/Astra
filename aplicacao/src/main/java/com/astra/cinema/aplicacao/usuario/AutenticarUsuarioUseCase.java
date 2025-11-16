package com.astra.cinema.aplicacao.usuario;

import com.astra.cinema.dominio.usuario.Usuario;
import com.astra.cinema.dominio.usuario.UsuarioRepositorio;

import java.util.Optional;

public class AutenticarUsuarioUseCase {

    private final UsuarioRepositorio usuarioRepositorio;

    public AutenticarUsuarioUseCase(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
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

        return Optional.of(new UsuarioDTO(
                usuario.getId().getValor(),
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getTipo().name()
        ));
    }
}

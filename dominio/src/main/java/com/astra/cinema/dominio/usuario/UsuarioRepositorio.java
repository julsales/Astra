package com.astra.cinema.dominio.usuario;

import java.util.Optional;

public interface UsuarioRepositorio {
    Usuario salvar(Usuario usuario);
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorId(UsuarioId id);
}

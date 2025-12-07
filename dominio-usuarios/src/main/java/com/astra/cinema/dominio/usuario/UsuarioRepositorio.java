package com.astra.cinema.dominio.usuario;

import com.astra.cinema.dominio.comum.UsuarioId;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositorio {
    Usuario salvar(Usuario usuario);
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorId(UsuarioId id);
    List<Usuario> listarTodos();
    List<Usuario> listarPorTipo(TipoUsuario tipo);
}

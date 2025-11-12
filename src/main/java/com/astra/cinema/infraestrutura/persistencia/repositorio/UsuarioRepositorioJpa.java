package com.astra.cinema.infraestrutura.persistencia.repositorio;

import com.astra.cinema.dominio.usuario.*;
import com.astra.cinema.infraestrutura.persistencia.jpa.UsuarioJpa;
import com.astra.cinema.infraestrutura.persistencia.jpa.UsuarioJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsuarioRepositorioJpa implements UsuarioRepositorio {

    private final UsuarioJpaRepository repository;

    public UsuarioRepositorioJpa(UsuarioJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        UsuarioJpa usuarioJpa = mapearParaJpa(usuario);
        UsuarioJpa salvo = repository.save(usuarioJpa);
        return mapearParaDominio(salvo);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(email)
                .map(this::mapearParaDominio);
    }

    @Override
    public Optional<Usuario> buscarPorId(UsuarioId id) {
        return repository.findById(id.getValor())
                .map(this::mapearParaDominio);
    }

    private UsuarioJpa mapearParaJpa(Usuario usuario) {
        Integer id = usuario.getId() != null ? usuario.getId().getValor() : null;
        return new UsuarioJpa(
                id,
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getNome(),
                usuario.getTipo()
        );
    }

    private Usuario mapearParaDominio(UsuarioJpa jpa) {
        return new Usuario(
                new UsuarioId(jpa.getId()),
                jpa.getEmail(),
                jpa.getSenha(),
                jpa.getNome(),
                jpa.getTipo()
        );
    }
}

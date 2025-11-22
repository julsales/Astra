package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.usuario.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpa, Integer> {
    Optional<UsuarioJpa> findByEmail(String email);
    List<UsuarioJpa> findByTipo(TipoUsuario tipo);
}

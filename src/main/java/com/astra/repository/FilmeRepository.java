package com.astra.repository;

import com.astra.model.Filme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilmeRepository extends JpaRepository<Filme, Long> {
    
    Optional<Filme> findByTitulo(String titulo);
    
    boolean existsByTitulo(String titulo);
}

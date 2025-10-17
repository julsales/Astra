package com.astra.repository;

import com.astra.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    
    Optional<Sala> findByNumero(String numero);
    
    boolean existsByNumero(String numero);
}

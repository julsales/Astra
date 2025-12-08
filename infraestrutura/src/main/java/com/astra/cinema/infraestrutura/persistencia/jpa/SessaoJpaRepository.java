package com.astra.cinema.infraestrutura.persistencia.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Interface Spring Data JPA para Sessão
 */
@Repository
public interface SessaoJpaRepository extends JpaRepository<SessaoJpa, Integer> {
    List<SessaoJpa> findByFilmeId(Integer filmeId);

    @Query("SELECT s FROM SessaoJpa s WHERE s.filmeId = :filmeId AND s.horario > :dataAtual")
    List<SessaoJpa> findSessoesFuturasPorFilme(@Param("filmeId") Integer filmeId, @Param("dataAtual") Date dataAtual);
}

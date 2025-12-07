package com.astra.cinema.infraestrutura.persistencia.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório Spring Data JPA para Ingresso
 */
@Repository
public interface IngressoJpaRepository extends JpaRepository<IngressoJpa, Integer> {
    List<IngressoJpa> findByCompraId(Integer compraId);
    List<IngressoJpa> findByStatus(String status);
    List<IngressoJpa> findByStatusIn(java.util.List<String> statuses);
    java.util.Optional<IngressoJpa> findByQrCode(String qrCode);
    
    /**
     * Busca ingressos com status específicos pertencentes a um cliente.
     * Faz JOIN com a tabela compra para filtrar por cliente_id.
     */
    @Query("SELECT i FROM IngressoJpa i JOIN CompraJpa c ON i.compraId = c.id " +
           "WHERE c.clienteId = :clienteId AND i.status IN :statuses")
    List<IngressoJpa> findByClienteIdAndStatusIn(@Param("clienteId") Integer clienteId, 
                                                  @Param("statuses") List<String> statuses);
}


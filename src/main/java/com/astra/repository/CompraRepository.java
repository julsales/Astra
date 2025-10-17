package com.astra.repository;

import com.astra.model.Compra;
import com.astra.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    
    List<Compra> findByCliente(Cliente cliente);
    
    List<Compra> findByStatus(Compra.StatusCompra status);
}

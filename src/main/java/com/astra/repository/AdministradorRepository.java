package com.astra.repository;

import com.astra.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    
    List<Administrador> findByNivelAcesso(Integer nivelAcesso);
    
    List<Administrador> findByDepartamento(String departamento);
}

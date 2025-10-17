package com.astra.repository;

import com.astra.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    
    Optional<Funcionario> findByMatricula(String matricula);
    
    List<Funcionario> findBySetor(String setor);
    
    boolean existsByMatricula(String matricula);
}

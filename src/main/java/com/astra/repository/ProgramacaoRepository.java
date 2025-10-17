package com.astra.repository;

import com.astra.model.Programacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProgramacaoRepository extends JpaRepository<Programacao, Long> {
    
    List<Programacao> findByDataInicioBetween(LocalDate inicio, LocalDate fim);
    
    List<Programacao> findByAtiva(Boolean ativa);
}

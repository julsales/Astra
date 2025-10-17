package com.astra.repository;

import com.astra.model.Sessao;
import com.astra.model.Filme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, Long> {
    
    List<Sessao> findByFilme(Filme filme);
    
    @Query("SELECT s FROM Sessao s WHERE s.filme = :filme AND s.dataHora > :dataHora")
    List<Sessao> findSessoesFuturasPorFilme(Filme filme, LocalDateTime dataHora);
    
    List<Sessao> findByStatus(Sessao.StatusSessao status);
}

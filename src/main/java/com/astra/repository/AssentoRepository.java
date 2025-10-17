package com.astra.repository;

import com.astra.model.Assento;
import com.astra.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssentoRepository extends JpaRepository<Assento, Long> {
    
    List<Assento> findBySessao(Sessao sessao);
    
    Optional<Assento> findBySessaoAndIdentificacao(Sessao sessao, String identificacao);
    
    List<Assento> findBySessaoAndStatus(Sessao sessao, Assento.StatusAssento status);
}

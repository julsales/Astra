package com.astra.repository;

import com.astra.model.VendaBomboniere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendaBomboniereRepository extends JpaRepository<VendaBomboniere, Long> {
    
    List<VendaBomboniere> findByStatus(VendaBomboniere.StatusVenda status);
}

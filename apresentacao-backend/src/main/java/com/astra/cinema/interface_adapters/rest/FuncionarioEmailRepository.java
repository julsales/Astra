package com.astra.cinema.interface_adapters.rest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FuncionarioEmailRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public FuncionarioEmailRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public String buscarEmailPorFuncionarioId(Integer funcionarioId) {
        String sql = """
            SELECT u.email 
            FROM usuario u
            JOIN funcionario f ON f.nome = u.nome
            WHERE f.id = ? AND u.tipo = 'FUNCIONARIO'
            LIMIT 1
            """;
        
        try {
            return jdbcTemplate.queryForObject(sql, String.class, funcionarioId);
        } catch (Exception e) {
            return null;
        }
    }
}

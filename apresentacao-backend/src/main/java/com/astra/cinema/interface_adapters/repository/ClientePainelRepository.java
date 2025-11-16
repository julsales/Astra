package com.astra.cinema.interface_adapters.repository;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ClientePainelRepository {

    private final JdbcTemplate jdbcTemplate;

    public ClientePainelRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ClientePainelDTO> listarClientes() {
        String sql = "SELECT id, nome, email FROM cliente ORDER BY nome";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new ClientePainelDTO(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("email")));
    }

    public record ClientePainelDTO(Integer id, String nome, String email) {}
}

package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.comum.ClienteId;
import com.astra.cinema.dominio.usuario.Cliente;
import com.astra.cinema.dominio.usuario.ClienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementação JPA do ClienteRepositorio
 */
@Component
public class ClienteRepositorioJpa implements ClienteRepositorio {

    @Autowired
    private ClienteJpaRepository clienteJpaRepository;

    @Override
    @Transactional
    public void salvar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("O cliente não pode ser nulo");
        }

        ClienteJpa clienteJpa = new ClienteJpa();

        // Se o cliente já tem ID, busca o existente para atualizar
        if (cliente.getClienteId() != null && cliente.getClienteId().getId() > 0) {
            clienteJpa = clienteJpaRepository.findById(cliente.getClienteId().getId())
                    .orElse(new ClienteJpa());
            clienteJpa.setId(cliente.getClienteId().getId());
        }

        clienteJpa.setNome(cliente.getNome());
        clienteJpa.setEmail(cliente.getEmail());

        // Define criadoEm apenas se for novo
        if (clienteJpa.getId() == null) {
            clienteJpa.setCriadoEm(LocalDateTime.now());
        }

        clienteJpaRepository.save(clienteJpa);
    }

    @Override
    public Cliente obterPorId(ClienteId clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("O ID do cliente não pode ser nulo");
        }

        ClienteJpa clienteJpa = clienteJpaRepository.findById(clienteId.getId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        return new Cliente(
            new ClienteId(clienteJpa.getId()),
            clienteJpa.getNome(),
            clienteJpa.getEmail()
        );
    }

    @Override
    public Cliente obterPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("O email não pode ser nulo ou vazio");
        }

        ClienteJpa clienteJpa = clienteJpaRepository.findByEmail(email)
                .orElse(null);

        if (clienteJpa == null) {
            return null;
        }

        return new Cliente(
            new ClienteId(clienteJpa.getId()),
            clienteJpa.getNome(),
            clienteJpa.getEmail()
        );
    }
}

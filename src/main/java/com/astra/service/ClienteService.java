package com.astra.service;

import com.astra.model.Cliente;
import com.astra.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf);
    }

    public Cliente salvar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void deletar(Long id) {
        clienteRepository.deleteById(id);
    }

    public boolean existeCpf(String cpf) {
        return clienteRepository.existsByCpf(cpf);
    }

    public boolean existeEmail(String email) {
        return clienteRepository.findAll().stream()
            .anyMatch(c -> c.getEmail().equalsIgnoreCase(email));
    }

    public void adicionarPontos(Long clienteId, Integer pontos) {
        Optional<Cliente> cliente = clienteRepository.findById(clienteId);
        if (cliente.isPresent()) {
            cliente.get().adicionarPontos(pontos);
            clienteRepository.save(cliente.get());
        }
    }
}

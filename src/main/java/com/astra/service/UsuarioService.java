package com.astra.service;

import com.astra.model.Usuario;
import com.astra.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deletar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public Optional<Usuario> autenticar(String email, String senha) {
        Optional<Usuario> usuario = usuarioRepository.findByEmailAndAtivo(email, true);
        
        if (usuario.isPresent() && usuario.get().getSenha().equals(senha)) {
            return usuario;
        }
        
        return Optional.empty();
    }

    public void desativarUsuario(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            usuario.get().setAtivo(false);
            usuarioRepository.save(usuario.get());
        }
    }

    public void ativarUsuario(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            usuario.get().setAtivo(true);
            usuarioRepository.save(usuario.get());
        }
    }
}

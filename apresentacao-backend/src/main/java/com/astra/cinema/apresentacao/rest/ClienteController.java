package com.astra.cinema.apresentacao.rest;

import com.astra.cinema.dominio.comum.ClienteId;
import com.astra.cinema.dominio.compra.Compra;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.usuario.TipoUsuario;
import com.astra.cinema.dominio.usuario.Usuario;
import com.astra.cinema.dominio.usuario.UsuarioRepositorio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final UsuarioRepositorio usuarioRepositorio;
    private final CompraRepositorio compraRepositorio;
    private final SessaoRepositorio sessaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;

    public ClienteController(
            UsuarioRepositorio usuarioRepositorio,
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio,
            FilmeRepositorio filmeRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.compraRepositorio = compraRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
        this.filmeRepositorio = filmeRepositorio;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<Usuario> clientes = usuarioRepositorio.listarPorTipo(TipoUsuario.CLIENTE);

            List<Map<String, Object>> response = clientes.stream()
                    .map(this::mapearCliente)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("mensagem", "Erro ao listar clientes: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/compras")
    public ResponseEntity<?> listarCompras(@PathVariable Integer id) {
        try {
            List<Compra> compras = compraRepositorio.buscarPorCliente(new ClienteId(id));

            List<Map<String, Object>> response = compras.stream()
                    .map(this::mapearCompraComDetalhes)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("mensagem", "Erro ao listar compras: " + e.getMessage()));
        }
    }

    private Map<String, Object> mapearCliente(Usuario usuario) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", usuario.getId() != null ? usuario.getId().getValor() : null);
        map.put("nome", usuario.getNome());
        map.put("email", usuario.getEmail());
        map.put("tipo", "CLIENTE");
        return map;
    }

    private Map<String, Object> mapearCompraComDetalhes(Compra compra) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", compra.getCompraId() != null ? compra.getCompraId().getId() : null);
        map.put("clienteId", compra.getClienteId() != null ? compra.getClienteId().getId() : null);
        map.put("status", compra.getStatus().name());
        map.put("pagamentoId", compra.getPagamentoId() != null ? compra.getPagamentoId().getId() : null);

        // Mapear ingressos
        List<Map<String, Object>> ingressos = compra.getIngressos().stream()
                .map(ingresso -> {
                    Map<String, Object> ingressoMap = new HashMap<>();
                    ingressoMap.put("id", ingresso.getIngressoId().getId());
                    ingressoMap.put("qrCode", ingresso.getQrCode());
                    ingressoMap.put("assento", ingresso.getAssentoId().getValor());
                    ingressoMap.put("tipo", ingresso.getTipo().name());
                    ingressoMap.put("status", ingresso.getStatus().name());
                    ingressoMap.put("sessaoId", ingresso.getSessaoId().getId());

                    // Buscar detalhes da sessão e filme
                    try {
                        var sessao = sessaoRepositorio.obterPorId(ingresso.getSessaoId());
                        if (sessao != null) {
                            ingressoMap.put("horario", sessao.getHorario());
                            ingressoMap.put("sala", sessao.getSala());

                            var filme = filmeRepositorio.obterPorId(sessao.getFilmeId());
                            if (filme != null) {
                                ingressoMap.put("filmeTitulo", filme.getTitulo());
                            }
                        }
                    } catch (Exception ignored) {
                    }

                    return ingressoMap;
                })
                .collect(Collectors.toList());

        map.put("ingressos", ingressos);
        map.put("quantidadeIngressos", ingressos.size());

        return map;
    }
}

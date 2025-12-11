package com.astra.cinema.aplicacao.servicos;

import com.astra.cinema.dominio.comum.ClienteId;
import com.astra.cinema.dominio.compra.Compra;
import com.astra.cinema.dominio.compra.CompraRepositorio;
import com.astra.cinema.dominio.filme.Filme;
import com.astra.cinema.dominio.filme.FilmeRepositorio;
import com.astra.cinema.dominio.sessao.Sessao;
import com.astra.cinema.dominio.sessao.SessaoRepositorio;
import com.astra.cinema.dominio.usuario.TipoUsuario;
import com.astra.cinema.dominio.usuario.Usuario;
import com.astra.cinema.dominio.usuario.UsuarioRepositorio;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de Aplicação para operações de Cliente
 * Centraliza toda lógica de negócio relacionada a clientes
 */
public class ClienteService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final CompraRepositorio compraRepositorio;
    private final SessaoRepositorio sessaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;

    public ClienteService(
            UsuarioRepositorio usuarioRepositorio,
            CompraRepositorio compraRepositorio,
            SessaoRepositorio sessaoRepositorio,
            FilmeRepositorio filmeRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.compraRepositorio = compraRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
        this.filmeRepositorio = filmeRepositorio;
    }

    /**
     * Lista todos os clientes
     */
    public List<ClienteDTO> listarClientes() {
        List<Usuario> clientes = usuarioRepositorio.listarPorTipo(TipoUsuario.CLIENTE);

        return clientes.stream()
                .map(this::mapearCliente)
                .collect(Collectors.toList());
    }

    /**
     * Lista compras de um cliente com detalhes
     */
    public List<CompraDetalhadaDTO> listarComprasCliente(Integer clienteId) {
        List<Compra> compras = compraRepositorio.buscarPorCliente(new ClienteId(clienteId));

        return compras.stream()
                .map(this::mapearCompraComDetalhes)
                .collect(Collectors.toList());
    }

    private ClienteDTO mapearCliente(Usuario usuario) {
        return new ClienteDTO(
                usuario.getId() != null ? usuario.getId().getValor() : null,
                usuario.getNome(),
                usuario.getEmail(),
                "CLIENTE"
        );
    }

    private CompraDetalhadaDTO mapearCompraComDetalhes(Compra compra) {
        List<IngressoDetalhadoDTO> ingressos = compra.getIngressos().stream()
                .map(ingresso -> {
                    String filmeTitulo = null;
                    String horario = null;
                    Integer salaId = null;
                    String sala = null;

                    // Buscar detalhes da sessão e filme
                    try {
                        Sessao sessao = sessaoRepositorio.obterPorId(ingresso.getSessaoId());
                        if (sessao != null) {
                            horario = sessao.getHorario().toString();
                            salaId = sessao.getSalaId().getId();
                            sala = "Sala " + sessao.getSalaId().getId();

                            Filme filme = filmeRepositorio.obterPorId(sessao.getFilmeId());
                            if (filme != null) {
                                filmeTitulo = filme.getTitulo();
                            }
                        }
                    } catch (Exception ignored) {
                    }

                    return new IngressoDetalhadoDTO(
                            ingresso.getIngressoId().getId(),
                            ingresso.getQrCode(),
                            ingresso.getAssentoId().getValor(),
                            ingresso.getTipo().name(),
                            ingresso.getStatus().name(),
                            ingresso.getSessaoId().getId(),
                            horario,
                            salaId,
                            sala,
                            filmeTitulo
                    );
                })
                .collect(Collectors.toList());

        return new CompraDetalhadaDTO(
                compra.getCompraId() != null ? compra.getCompraId().getId() : null,
                compra.getClienteId() != null ? compra.getClienteId().getId() : null,
                compra.getStatus().name(),
                compra.getPagamentoId() != null ? compra.getPagamentoId().getId() : null,
                ingressos,
                ingressos.size()
        );
    }

    // Classes de resultado
    public record ClienteDTO(Integer id, String nome, String email, String tipo) {}

    public record CompraDetalhadaDTO(
            Integer id,
            Integer clienteId,
            String status,
            Integer pagamentoId,
            List<IngressoDetalhadoDTO> ingressos,
            int quantidadeIngressos
    ) {}

    public record IngressoDetalhadoDTO(
            int id,
            String qrCode,
            String assento,
            String tipo,
            String status,
            int sessaoId,
            String horario,
            Integer salaId,
            String sala,
            String filmeTitulo
    ) {}
}

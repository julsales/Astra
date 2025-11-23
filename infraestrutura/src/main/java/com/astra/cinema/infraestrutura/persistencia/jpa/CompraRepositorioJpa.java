package com.astra.cinema.infraestrutura.persistencia.jpa;

import com.astra.cinema.dominio.compra.*;
import com.astra.cinema.dominio.comum.*;
import com.astra.cinema.infraestrutura.util.QrCodeGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementação JPA do CompraRepositorio
 * Padrão: Adapter (adapta interface do domínio para JPA)
 */
@Component
public class CompraRepositorioJpa implements CompraRepositorio {

    @Autowired
    private CompraJpaRepository compraJpaRepository;

    @Autowired
    private IngressoJpaRepository ingressoJpaRepository;

    @Autowired
    private CinemaMapeador mapeador;

    @Autowired
    private QrCodeGenerator qrCodeGenerator;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void salvar(Compra compra) {
        if (compra == null) {
            throw new IllegalArgumentException("A compra não pode ser nula");
        }

        // Cria novo CompraJpa sem ID (para forçar INSERT em vez de MERGE)
        CompraJpa compraJpa = new CompraJpa();
        // NÃO seta o ID - deixa null para o JPA gerar com @GeneratedValue
        compraJpa.setClienteId(compra.getClienteId().getId());
        compraJpa.setStatus(compra.getStatus().name());
        compraJpa.setPagamentoId(compra.getPagamentoId() != null ? compra.getPagamentoId().getId() : null);
        compraJpa.setCriadoEm(java.time.LocalDateTime.now());
        
        // Usa persist() para forçar INSERT (não faz merge)
        entityManager.persist(compraJpa);
        entityManager.flush(); // Força o INSERT imediatamente para obter o ID gerado
        
        CompraJpa compraSalva = compraJpa; // Agora tem o ID gerado pelo banco

        // Salva os ingressos associados e gera QR Code para cada um
        List<Ingresso> ingressos = compra.getIngressos();
        for (Ingresso ingresso : ingressos) {
            // Gera QR Code único no backend
            String qrCode = qrCodeGenerator.gerarQrCode();
            
            // Garante que o QR Code seja único (tenta novamente se já existir)
            int tentativas = 0;
            while (ingressoJpaRepository.findByQrCode(qrCode).isPresent() && tentativas < 5) {
                qrCode = qrCodeGenerator.gerarQrCode();
                tentativas++;
            }
            
            // Cria IngressoJpa diretamente (o ID será gerado pelo banco)
            IngressoJpa ingressoJpa = new IngressoJpa();
            ingressoJpa.setCompraId(compraSalva.getId());
            ingressoJpa.setSessaoId(ingresso.getSessaoId().getId());
            ingressoJpa.setAssento(ingresso.getAssentoId().getValor());
            ingressoJpa.setTipo(ingresso.getTipo().name());
            ingressoJpa.setStatus(ingresso.getStatus().name());
            ingressoJpa.setQrCode(qrCode); // QR Code gerado no backend
            
            // Salva e obtém o ID gerado
            IngressoJpa ingressoSalvo = ingressoJpaRepository.save(ingressoJpa);
            
            // Atualiza o ingresso do domínio com o ID gerado (se necessário)
            // Isso garante que o ingresso tenha o ID correto após ser salvo
        }
    }

    @Override
    public Compra obterPorId(CompraId compraId) {
        if (compraId == null) {
            throw new IllegalArgumentException("O ID da compra não pode ser nulo");
        }

        CompraJpa compraJpa = compraJpaRepository.findById(compraId.getId()).orElse(null);
        if (compraJpa == null) {
            return null;
        }

        // Busca os ingressos associados
        List<IngressoJpa> ingressosJpa = ingressoJpaRepository.findByCompraId(compraId.getId());
        List<Ingresso> ingressos = ingressosJpa.stream()
                .map(mapeador::mapearParaIngresso)
                .collect(Collectors.toList());

        return mapeador.mapearParaCompra(compraJpa, ingressos);
    }

    @Override
    public List<Compra> buscarPorCliente(ClienteId clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("O ID do cliente não pode ser nulo");
        }

        List<CompraJpa> comprasJpa = compraJpaRepository.findByClienteId(clienteId.getId());
        List<Compra> compras = new ArrayList<>();

        for (CompraJpa compraJpa : comprasJpa) {
            List<IngressoJpa> ingressosJpa = ingressoJpaRepository.findByCompraId(compraJpa.getId());
            List<Ingresso> ingressos = ingressosJpa.stream()
                    .map(mapeador::mapearParaIngresso)
                    .collect(Collectors.toList());
            compras.add(mapeador.mapearParaCompra(compraJpa, ingressos));
        }

        return compras;
    }

    @Override
    public List<Compra> listarTodas() {
        List<CompraJpa> comprasJpa = compraJpaRepository.findAll();
        List<Compra> compras = new ArrayList<>();

        for (CompraJpa compraJpa : comprasJpa) {
            List<IngressoJpa> ingressosJpa = ingressoJpaRepository.findByCompraId(compraJpa.getId());
            List<Ingresso> ingressos = ingressosJpa.stream()
                    .map(mapeador::mapearParaIngresso)
                    .collect(Collectors.toList());
            compras.add(mapeador.mapearParaCompra(compraJpa, ingressos));
        }

        return compras;
    }

    @Override
    public Ingresso buscarIngressoPorQrCode(String qrCode) {
        if (qrCode == null || qrCode.isEmpty()) {
            throw new IllegalArgumentException("O QR Code não pode ser nulo ou vazio");
        }

        // Remove espaços e converte para maiúsculo
        String codigoLimpo = qrCode.trim().toUpperCase();

        // Busca direta por QR Code no banco (solução definitiva)
        return ingressoJpaRepository.findByQrCode(codigoLimpo)
                .map(mapeador::mapearParaIngresso)
                .orElse(null);
    }

    @Override
    public Ingresso buscarIngressoPorId(IngressoId ingressoId) {
        if (ingressoId == null) {
            throw new IllegalArgumentException("O ID do ingresso não pode ser nulo");
        }

        return ingressoJpaRepository.findById(ingressoId.getId())
                .map(mapeador::mapearParaIngresso)
                .orElse(null);
    }

    @Override
    public Compra buscarCompraPorQrCode(String qrCode) {
        if (qrCode == null || qrCode.isEmpty()) {
            throw new IllegalArgumentException("O QR Code não pode ser nulo ou vazio");
        }

        // Remove espaços e converte para maiúsculo
        String codigoLimpo = qrCode.trim().toUpperCase();

        // Busca o ingresso pelo QR Code
        IngressoJpa ingressoJpa = ingressoJpaRepository.findByQrCode(codigoLimpo).orElse(null);
        if (ingressoJpa == null) {
            return null;
        }

        // Usa o compraId do ingresso para buscar a compra completa
        return obterPorId(new CompraId(ingressoJpa.getCompraId()));
    }

    @Override
    public void atualizarIngresso(Ingresso ingresso) {
        if (ingresso == null) {
            throw new IllegalArgumentException("O ingresso não pode ser nulo");
        }

        IngressoJpa ingressoJpa = ingressoJpaRepository.findById(ingresso.getIngressoId().getId())
                .orElse(null);

        if (ingressoJpa == null) {
            throw new IllegalArgumentException("Ingresso não encontrado");
        }

        // Atualiza os campos do ingresso
        ingressoJpa.setSessaoId(ingresso.getSessaoId().getId());
        ingressoJpa.setAssento(ingresso.getAssentoId().getValor());
        ingressoJpa.setTipo(ingresso.getTipo().name());
        ingressoJpa.setStatus(ingresso.getStatus().name());
        // Preserva o QR Code (não atualiza se já existir)
        if (ingresso.getQrCode() != null && !ingresso.getQrCode().isEmpty()) {
            ingressoJpa.setQrCode(ingresso.getQrCode());
        }

        ingressoJpaRepository.save(ingressoJpa);
    }

    @Override
    public List<Ingresso> buscarIngressosAtivos() {
        // Busca ingressos com status VALIDADO (ingressos que ainda são relevantes para o cliente)
        java.util.List<String> statuses = java.util.Arrays.asList(
            StatusIngresso.VALIDADO.name()
        );
        List<IngressoJpa> ingressosJpa = ingressoJpaRepository.findByStatusIn(statuses);
        return ingressosJpa.stream()
                .map(mapeador::mapearParaIngresso)
                .collect(Collectors.toList());
    }
}


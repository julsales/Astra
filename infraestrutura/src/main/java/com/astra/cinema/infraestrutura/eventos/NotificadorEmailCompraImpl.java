package com.astra.cinema.infraestrutura.eventos;

import com.astra.cinema.dominio.eventos.ObservadorEvento;
import com.astra.cinema.dominio.eventos.CompraConfirmadaEvento;
import org.springframework.stereotype.Component;

/**
 * PADRÃO OBSERVER - Observador Concreto (Infraestrutura)
 * Envia notificações por e-mail quando uma compra é confirmada.
 * 
 * Esta classe pertence à infraestrutura porque:
 * - Usa tecnologias específicas (JavaMail, SMTP, serviços de email)
 * - Pode ser substituída por outras implementações (SendGrid, AWS SES, etc.)
 */
@Component
public class NotificadorEmailCompraImpl implements ObservadorEvento<CompraConfirmadaEvento> {

    // Em produção, injetaria EmailService, JavaMailSender, etc.
    // @Autowired
    // private JavaMailSender mailSender;

    @Override
    public void atualizar(CompraConfirmadaEvento evento) {
        // Simulação de envio de e-mail
        System.out.println("📧 [E-MAIL] Compra confirmada!");
        System.out.println("   Cliente ID: " + evento.getClienteId().getId());
        System.out.println("   Compra ID: " + evento.getCompraId().getId());
        System.out.println("   Ingressos: " + evento.getQuantidadeIngressos());
        System.out.println("   Data: " + evento.getDataHora());

        // Em produção, enviaria e-mail real via SMTP:
        // MimeMessage message = mailSender.createMimeMessage();
        // MimeMessageHelper helper = new MimeMessageHelper(message, true);
        // helper.setTo(cliente.getEmail());
        // helper.setSubject("Compra Confirmada - Cinema Astra");
        // helper.setText(construirCorpoEmail(evento), true);
        // mailSender.send(message);
    }

    @Override
    public Class<CompraConfirmadaEvento> getTipoEvento() {
        return CompraConfirmadaEvento.class;
    }

    private String construirCorpoEmail(CompraConfirmadaEvento evento) {
        return String.format("""
                <html>
                <body>
                    <h2>Compra Confirmada! 🎬</h2>
                    <p>Sua compra foi confirmada com sucesso.</p>
                    <p><strong>ID da Compra:</strong> %d</p>
                    <p><strong>Quantidade de Ingressos:</strong> %d</p>
                    <p>Obrigado por escolher o Cinema Astra!</p>
                </body>
                </html>
                """,
                evento.getCompraId().getId(),
                evento.getQuantidadeIngressos()
        );
    }
}

# language: pt
Funcionalidade: Cancelar Pagamento
  Como sistema de pagamento
  Quero permitir cancelamento de pagamentos
  Para gerenciar transações não concluídas

  Cenário: Cancelar pagamento pendente
    Dado um pagamento pendente de valor 75.0 com método "PIX"
    Quando o cliente cancela o pagamento
    Então o pagamento tem status "CANCELADO"

  Cenário: Cancelar pagamento com falha
    Dado um pagamento com falha na autorização
    Quando o cliente cancela o pagamento
    Então o pagamento tem status "CANCELADO"

  Cenário: Tentativa de cancelar pagamento bem-sucedido
    Dado um pagamento já autorizado com sucesso
    Quando o cliente tenta cancelar o pagamento
    Então o sistema informa que não é possível cancelar pagamento bem-sucedido

  Cenário: Tentativa de cancelar pagamento já cancelado
    Dado um pagamento já cancelado
    Quando o cliente tenta cancelar o pagamento novamente
    Então o sistema informa que o pagamento já está cancelado

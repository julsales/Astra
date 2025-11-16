# language: pt
Funcionalidade: Cancelar Pagamento

  Regra: Um pagamento só pode ser cancelado enquanto estiver com status "PENDENTE"

  Cenário: Cancelamento de pagamento pendente
    Dado que o pagamento está com status "PENDENTE"
    Quando o cliente solicita cancelamento
    Então o pagamento é atualizado para "CANCELADO"

  Cenário: Tentativa de cancelar pagamento já confirmado
    Dado que o pagamento já foi aprovado
    Quando o cliente tenta cancelar
    Então o sistema rejeita a solicitação

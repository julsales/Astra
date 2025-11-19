# language: pt
Funcionalidade: Confirmar Compra

  Regra: A compra só pode ser confirmada se o pagamento associado for autorizado

  Cenário: Compra confirmada com sucesso
    Dado que existe uma compra pendente associada a um pagamento com status "SUCESSO"
    Quando o cliente confirma a compra
    Então o status da compra é atualizado para "CONFIRMADA"
  E os ingressos passam a ter status "VALIDADO"

  Cenário: Tentativa de confirmar compra sem pagamento aprovado
    Dado que existe uma compra pendente associada a um pagamento com status "FALHA"
    Quando o cliente tenta confirmar a compra
    Então o sistema impede a confirmação da compra
    E informa que o pagamento não foi autorizado

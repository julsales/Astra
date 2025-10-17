# language: pt
Funcionalidade: Processamento de Pagamentos
  Como sistema de pagamento
  Quero processar transações
  Para confirmar compras e vendas

  # Regra de Negócio 9
  Cenário: Pagamento aprovado
    Dado que o cliente insere dados válidos do cartão
    Quando o gateway responde com sucesso
    Então o pagamento muda de status para "SUCESSO"

  Cenário: Pagamento recusado pelo gateway
    Dado que o cliente insere dados de cartão inválidos
    Quando o gateway retorna falha
    Então o pagamento muda de status para "FALHA"

  # Regra de Negócio 10
  Cenário: Cancelamento de pagamento pendente
    Dado que o pagamento está com status "PENDENTE"
    Quando o cliente solicita cancelamento
    Então o pagamento é atualizado para "CANCELADO"

  Cenário: Tentativa de cancelar pagamento já confirmado
    Dado que o pagamento já foi aprovado
    Quando o cliente tenta cancelar
    Então o sistema rejeita a solicitação

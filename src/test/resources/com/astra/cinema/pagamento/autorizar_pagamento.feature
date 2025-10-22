# language: pt
Funcionalidade: Autorizar Pagamento

  Regra: O pagamento só pode ser confirmado se a autorização do gateway retornar sucesso

  Cenário: Pagamento aprovado
    Dado que o cliente insere dados válidos do cartão
    Quando o gateway responde com sucesso
    Então o pagamento muda de status para "SUCESSO"

  Cenário: Pagamento recusado pelo gateway
    Dado que o cliente insere dados de cartão inválidos
    Quando o gateway retorna falha
    Então o pagamento muda de status para "FALHA"

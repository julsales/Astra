# language: pt
Funcionalidade: Autorizar Pagamento
  Como sistema de pagamento
  Quero autorizar pagamentos
  Para processar transações financeiras

  Cenário: Autorizar pagamento com sucesso
    Dado um pagamento pendente de valor 50.0 com método "PIX"
    Quando o sistema autoriza o pagamento com sucesso
    Então o pagamento tem status "SUCESSO"
    E a transação foi registrada

  Cenário: Autorizar pagamento com falha
    Dado um pagamento pendente de valor 100.0 com método "Cartão de Crédito"
    Quando o sistema autoriza o pagamento com falha
    Então o pagamento tem status "FALHA"
    E a transação foi registrada

  Cenário: Tentativa de autorizar pagamento já autorizado
    Dado um pagamento já autorizado com sucesso
    Quando o sistema tenta autorizar o pagamento novamente
    Então o sistema informa que o pagamento não está pendente

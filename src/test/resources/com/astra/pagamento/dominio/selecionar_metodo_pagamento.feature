# language: pt
Funcionalidade: Selecionar Método de Pagamento
  Como cliente
  Quero selecionar um método de pagamento
  Para realizar o pagamento da minha compra

  Cenário: Listar métodos de pagamento disponíveis
    Quando o cliente consulta os métodos de pagamento disponíveis
    Então o sistema exibe 4 métodos de pagamento
    E os métodos incluem "PIX", "Cartão de Crédito", "Cartão de Débito" e "Dinheiro"

  Cenário: Iniciar pagamento com PIX
    Quando o cliente inicia um pagamento de valor 50.0 com método "PIX"
    Então o pagamento é criado com status "PENDENTE"
    E o valor do pagamento é 50.0

  Cenário: Iniciar pagamento com Cartão de Crédito
    Quando o cliente inicia um pagamento de valor 100.0 com método "Cartão de Crédito"
    Então o pagamento é criado com status "PENDENTE"
    E o valor do pagamento é 100.0

  Cenário: Tentativa de usar método inativo
    Dado um método de pagamento inativo "PIX"
    Quando o cliente tenta iniciar um pagamento com o método inativo
    Então o sistema informa que o método não está ativo

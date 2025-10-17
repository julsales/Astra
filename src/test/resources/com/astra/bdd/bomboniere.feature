# language: pt
Funcionalidade: Vendas na Bomboniere
  Como atendente da bomboniere
  Quero realizar vendas de produtos
  Para atender os clientes

  # Regra de Negócio 7
  Cenário: Venda confirmada com sucesso
    Dado que há uma venda pendente com pagamento em status "SUCESSO"
    Quando o atendente confirma a venda
    Então o status da venda muda para "CONFIRMADA"

  Cenário: Tentativa de confirmar venda com pagamento pendente
    Dado que a venda possui pagamento com status "FALHA"
    Quando o atendente tenta confirmar a venda
    Então o sistema impede a confirmação
    E exibe mensagem de erro

  # Regra de Negócio 8
  Cenário: Venda com estoque suficiente
    Dado que o produto "Pipoca Média" tem 10 unidades no estoque
    Quando o cliente compra 2 unidades
    Então o estoque é reduzido para 8

  Cenário: Tentativa de venda sem estoque suficiente
    Dado que o produto "Refrigerante" tem 1 unidade no estoque
    Quando o cliente tenta comprar 3 unidades
    Então o sistema recusa a venda
    E informa que o estoque é insuficiente

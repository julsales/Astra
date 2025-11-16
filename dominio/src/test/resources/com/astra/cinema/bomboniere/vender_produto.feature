# language: pt
Funcionalidade: Vender Produto

  Regra: A venda só é autorizada se houver quantidade suficiente em estoque

  Cenário: Venda com estoque suficiente
    Dado que o produto "Pipoca Média" tem 10 unidades no estoque
    Quando o cliente compra 2 unidades
    Então o estoque é reduzido para 8

  Cenário: Tentativa de venda sem estoque suficiente
    Dado que o produto "Refrigerante" tem 1 unidade no estoque
    Quando o cliente tenta comprar 3 unidades
    Então o sistema recusa a venda
    E informa que o estoque é insuficiente

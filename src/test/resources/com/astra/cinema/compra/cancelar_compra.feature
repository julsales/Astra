# language: pt
Funcionalidade: Cancelar Compra

  Regra: O cliente pode cancelar uma compra enquanto os ingressos não tiverem sido utilizados

  Cenário: Cancelamento de compra permitido
    Dado que existe uma compra confirmada com ingressos ainda válidos
    Quando o cliente solicita o cancelamento
    Então o status da compra é alterado para "CANCELADA"
    E o pagamento é estornado

  Cenário: Tentativa de cancelar compra após uso do ingresso
    Dado que o ingresso da compra já foi utilizado na entrada da sessão
    Quando o cliente tenta cancelar a compra
    Então o sistema recusa o cancelamento
    E informa que o ingresso já foi utilizado

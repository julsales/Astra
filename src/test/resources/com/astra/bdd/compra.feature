# language: pt
Funcionalidade: Compra de Ingressos
  Como cliente do cinema
  Quero comprar ingressos
  Para assistir aos filmes

  Contexto:
    Dado que existe um filme "Duna 2" em cartaz
    E que existe uma sala "1" com capacidade para 50 assentos
    E que existe uma sessão para o filme às "19:00" com preço de "R$ 30,00"

  # Regra de Negócio 1
  Cenário: Compra iniciada com sucesso
    Dado que existe uma sessão com assentos A1 e A2 disponíveis
    E um cliente autenticado deseja comprar dois ingressos
    Quando o cliente seleciona os assentos A1 e A2 e inicia a compra
    Então a compra é criada com status "PENDENTE"
    E os assentos A1 e A2 ficam temporariamente reservados

  Cenário: Tentativa de iniciar compra com assento indisponível
    Dado que o assento A1 da sessão já está ocupado
    Quando o cliente tenta iniciar uma compra incluindo o assento A1
    Então o sistema rejeita a criação da compra
    E informa que o assento está indisponível

  # Regra de Negócio 2
  Cenário: Compra confirmada com sucesso
    Dado que existe uma compra pendente associada a um pagamento com status "SUCESSO"
    Quando o cliente confirma a compra
    Então o status da compra é atualizado para "CONFIRMADA"
    E os ingressos passam a ter status "VALIDO"

  Cenário: Tentativa de confirmar compra sem pagamento aprovado
    Dado que existe uma compra pendente associada a um pagamento com status "FALHA"
    Quando o cliente tenta confirmar a compra
    Então o sistema impede a confirmação
    E informa que o pagamento não foi autorizado

  # Regra de Negócio 3
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

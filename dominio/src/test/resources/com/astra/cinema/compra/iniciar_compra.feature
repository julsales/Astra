# language: pt
Funcionalidade: Iniciar Compra

  Regra: O cliente só pode iniciar uma compra se todos os assentos selecionados estiverem disponíveis na sessão

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

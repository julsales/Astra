# language: pt
Funcionalidade: Marcar Sessão como Esgotada

  Regra: A sessão deve ser marcada como "ESGOTADA" quando todos os assentos estiverem ocupados

  Cenário: Sessão esgotada com sucesso
    Dado que todos os assentos da sessão foram reservados
    Quando o último assento é vendido
    Então o status da sessão é atualizado para "ESGOTADA"

  Cenário: Tentativa de marcar sessão esgotada com assentos ainda livres
    Dado que ainda há assentos disponíveis na sessão
    Quando o sistema tenta alterar o status para "ESGOTADA"
    Então a alteração é rejeitada

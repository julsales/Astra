# language: pt
Funcionalidade: Gerenciamento de Sessões
  Como gerente do cinema
  Quero gerenciar sessões de filmes
  Para organizar a programação

  # Regra de Negócio 4
  Cenário: Sessão criada com sucesso
    Dado que o filme "Duna 2" está com status "EM_CARTAZ"
    Quando o gerente cria uma nova sessão para esse filme
    Então a sessão é registrada com status "DISPONIVEL"

  Cenário: Tentativa de criar sessão para filme fora de cartaz
    Dado que o filme "Avatar 3" está com status "RETIRADO"
    Quando o gerente tenta criar uma sessão para esse filme
    Então o sistema recusa a criação
    E informa que o filme não está em cartaz

  # Regra de Negócio 5
  Cenário: Sessão esgotada com sucesso
    Dado que todos os assentos da sessão foram reservados
    Quando o último assento é vendido
    Então o status da sessão é atualizado para "ESGOTADA"

  Cenário: Tentativa de marcar sessão esgotada com assentos ainda livres
    Dado que ainda há assentos disponíveis na sessão
    Quando o sistema tenta alterar o status para "ESGOTADA"
    Então a alteração é rejeitada

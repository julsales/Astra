# language: pt
Funcionalidade: Criar Sessão

  Regra: Uma sessão só pode ser criada para filmes com status "EM_CARTAZ"

  Cenário: Sessão criada com sucesso
    Dado que o filme "Duna 2" está com status "EM_CARTAZ"
    Quando o gerente cria uma nova sessão para esse filme
    Então a sessão é registrada com status "DISPONIVEL"

  Cenário: Tentativa de criar sessão para filme fora de cartaz
    Dado que o filme "Avatar 3" está com status "RETIRADO"
    Quando o gerente tenta criar uma sessão para esse filme
    Então o sistema recusa a criação da sessão
    E informa que o filme não está em cartaz

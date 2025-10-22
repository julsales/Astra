# language: pt
Funcionalidade: Gerenciar Cinema

  Regra: Apenas funcionários com cargo de GERENTE podem gerenciar dados de filmes, sessões e produtos

  Cenário: Gerente cria sessão
    Dado que o usuário autenticado possui cargo "GERENTE"
    Quando ele cria uma nova sessão
    Então a sessão é registrada com sucesso

  Cenário: Tentativa de criação de sessão por cliente
    Dado que o usuário autenticado possui perfil de cliente
    Quando ele tenta criar uma sessão
    Então o sistema recusa a operação
    E exibe mensagem de acesso negado

# language: pt
Funcionalidade: Controle de Acesso de Usuários
  Como sistema de segurança
  Quero controlar permissões
  Para garantir acesso adequado

  # Regra de Negócio 11
  Cenário: Gerente cria sessão
    Dado que o usuário autenticado possui cargo "GERENTE"
    Quando ele cria uma nova sessão
    Então a sessão é registrada com sucesso

  Cenário: Tentativa de criação de sessão por cliente
    Dado que o usuário autenticado possui perfil de cliente
    Quando ele tenta criar uma sessão
    Então o sistema recusa a operação
    E exibe mensagem de acesso negado

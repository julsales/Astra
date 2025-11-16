# language: pt
Funcionalidade: Criar Programação

  Regra: A programação semanal só pode conter sessões com status "DISPONIVEL"

  Cenário: Programação válida
    Dado que há três sessões com status "DISPONIVEL"
    Quando o gerente cria uma nova programação para a semana
    Então a programação é registrada com sucesso

  Cenário: Tentativa de adicionar sessão cancelada à programação
    Dado que uma das sessões selecionadas está com status "CANCELADA"
    Quando o gerente tenta criar a programação
    Então o sistema recusa a criação da programação

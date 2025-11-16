# language: pt
Funcionalidade: Remover Filme

  Regra: Um filme só pode ser removido do catálogo quando não houver sessões futuras vinculadas a ele

  Cenário: Remover filme sem sessões futuras
    Dado que o filme "Matrix" não possui sessões agendadas
    Quando o gerente remove o filme
    Então o status do filme muda para "RETIRADO"

  Cenário: Tentativa de remover filme ainda em exibição
    Dado que o filme "Duna 2" ainda possui sessões futuras
    Quando o gerente tenta remover o filme
    Então o sistema impede a remoção
    E exibe mensagem informando que há sessões ativas

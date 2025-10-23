# language: pt
Funcionalidade: Confirmar Venda na Bomboniere

  Regra: Uma venda na bomboniere só é confirmada após pagamento aprovado

  Cenário: Venda confirmada com sucesso
    Dado que há uma venda pendente com pagamento em status "SUCESSO"
    Quando o atendente confirma a venda
    Então o status da venda muda para "CONFIRMADA"

  Cenário: Tentativa de confirmar venda com pagamento pendente
    Dado que a venda possui pagamento com status "FALHA"
    Quando o atendente tenta confirmar a venda
    Então o sistema impede a confirmação da venda
    E exibe mensagem de erro

# Domínio da Bomboniere

## Descrição
Bounded Context responsável pela **loja de conveniência** do cinema (bomboniere).

## Agregados

### Produto
- **Entidade:** `Produto`
- **Repositório:** `ProdutoRepositorio`
- **Service:** `ProdutoService`

### Venda
- **Entidade:** `Venda`
- **Value Objects:** `StatusVenda` (PENDENTE, CONFIRMADA, CANCELADA)
- **Repositório:** `VendaRepositorio`
- **Service:** `VendaService`

## Responsabilidades

### O que PERTENCE a este domínio:
✅ Cadastro e gerenciamento de produtos (pipoca, refrigerante, doces, etc)
✅ Controle de estoque de produtos
✅ Vendas de produtos da bomboniere
✅ Ajustes de estoque (entrada, saída, correção)

### O que NÃO pertence a este domínio:
❌ Vendas de ingressos (domínio-vendas)
❌ Pagamentos (domínio-vendas - mas pode compartilhar)
❌ Gerenciamento de usuários (domínio-usuarios)

## Regras de Negócio

### Produto
- Cada produto tem nome, preço e quantidade em estoque
- Estoque não pode ficar negativo
- Produtos podem ser adicionados, modificados ou removidos

### Venda
- Vendas podem incluir múltiplos produtos
- Controla quantidade vendida de cada produto
- Status: PENDENTE, CONFIRMADA, CANCELADA
- Reduz estoque automaticamente ao confirmar venda

## Dependências
- `dominio-compartilhado` (Value Objects)
- `dominio-vendas` (para compartilhar conceito de Pagamento - avaliar se necessário)

## Estrutura de Pacotes
```
com.astra.cinema.dominio.bomboniere/
├── Produto.java
├── Venda.java
├── StatusVenda.java
├── ProdutoRepositorio.java
├── VendaRepositorio.java
├── ProdutoService.java (Domain Service)
└── VendaService.java (Domain Service)
```

## Linguagem Ubíqua
- **Produto:** Item à venda na bomboniere (comida/bebida)
- **Venda:** Transação de venda de produtos
- **Estoque:** Quantidade disponível de um produto
- **Ajuste de Estoque:** Correção manual da quantidade disponível
- **Entrada de Estoque:** Adição de produtos ao estoque
- **Bomboniere:** Loja de conveniência do cinema

## Possíveis Melhorias Futuras
- Separar Pagamento em um contexto isolado compartilhado entre vendas e bomboniere
- Implementar combos de produtos
- Sistema de promoções e descontos
- Integração com fornecedores para reposição automática

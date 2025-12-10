# API - Produtos da Bomboniere no Ingresso

## Visão Geral

Os ingressos agora incluem informações detalhadas sobre os produtos da bomboniere comprados junto com a compra.

## Estrutura do `IngressoDTO`

```json
{
  "id": 1,
  "qrCode": "ASTRA176537B9B0705DA79B27",
  "codigo": "ASTRA176537B9B0705DA79B27",
  "sessaoId": 2,
  "assento": "C4",
  "tipo": "INTEIRA",
  "status": "ATIVO",
  "remarcado": false,
  "historicoRemarcacao": null,
  "produtosBomboniere": [
    {
      "produtoId": 1,
      "nome": "Pipoca Grande",
      "quantidade": 2,
      "precoUnitario": 15.00,
      "subtotal": 30.00
    },
    {
      "produtoId": 3,
      "nome": "Refrigerante 500ml",
      "quantidade": 1,
      "precoUnitario": 8.00,
      "subtotal": 8.00
    }
  ]
}
```

## Endpoints Disponíveis

### 1. Criar Compra com Produtos da Bomboniere

**POST** `/api/compras`

```json
{
  "clienteId": 1,
  "sessaoId": 2,
  "assentos": ["C4", "C5"],
  "tipoIngresso": "INTEIRA",
  "produtos": [
    {
      "produtoId": 1,
      "quantidade": 2
    },
    {
      "produtoId": 3,
      "quantidade": 1
    }
  ]
}
```

**Resposta:**
```json
{
  "compraId": 1,
  "clienteId": 1,
  "status": "CONFIRMADA",
  "ingressos": [
    {
      "id": 1,
      "qrCode": "ASTRA176537B9B0705DA79B27",
      "codigo": "ASTRA176537B9B0705DA79B27",
      "sessaoId": 2,
      "assento": "C4",
      "tipo": "INTEIRA",
      "status": "ATIVO",
      "produtosBomboniere": [
        {
          "produtoId": 1,
          "nome": "Pipoca Grande",
          "quantidade": 2,
          "precoUnitario": 15.00,
          "subtotal": 30.00
        }
      ]
    },
    {
      "id": 2,
      "qrCode": "ASTRA276537B9B0705DA79B28",
      "codigo": "ASTRA276537B9B0705DA79B28",
      "sessaoId": 2,
      "assento": "C5",
      "tipo": "INTEIRA",
      "status": "ATIVO",
      "produtosBomboniere": [
        {
          "produtoId": 1,
          "nome": "Pipoca Grande",
          "quantidade": 2,
          "precoUnitario": 15.00,
          "subtotal": 30.00
        }
      ]
    }
  ]
}
```

### 2. Consultar Compra por ID

**GET** `/api/compras/{id}`

Retorna a compra completa com todos os ingressos e produtos da bomboniere.

### 3. Consultar Ingresso por QR Code (NOVO!)

**GET** `/api/compras/ingresso/{qrCode}`

**Exemplo:**
```
GET /api/compras/ingresso/ASTRA176537B9B0705DA79B27
```

**Resposta:**
```json
{
  "ingresso": {
    "id": 1,
    "qrCode": "ASTRA176537B9B0705DA79B27",
    "codigo": "ASTRA176537B9B0705DA79B27",
    "sessaoId": 2,
    "assento": "C4",
    "tipo": "INTEIRA",
    "status": "ATIVO",
    "remarcado": false,
    "produtosBomboniere": [
      {
        "produtoId": 1,
        "nome": "Pipoca Grande",
        "quantidade": 2,
        "precoUnitario": 15.00,
        "subtotal": 30.00
      },
      {
        "produtoId": 3,
        "nome": "Refrigerante 500ml",
        "quantidade": 1,
        "precoUnitario": 8.00,
        "subtotal": 8.00
      }
    ]
  },
  "compraId": 1,
  "clienteId": 1
}
```

## Detalhes Técnicos

### Agrupamento de Produtos

Os produtos são automaticamente agrupados por `produtoId` e suas quantidades são somadas. Por exemplo, se a compra tiver:
- 2x Pipoca associados ao ingresso 1
- 3x Pipoca associados ao ingresso 2

O campo `produtosBomboniere` mostrará:
```json
{
  "produtoId": 1,
  "nome": "Pipoca Grande",
  "quantidade": 5,
  "precoUnitario": 15.00,
  "subtotal": 75.00
}
```

### Cálculo de Subtotal

O subtotal é calculado automaticamente:
```
subtotal = quantidade × precoUnitario
```

### Compatibilidade

- O campo `produtosBomboniere` pode ser `null` ou uma lista vazia se não houver produtos comprados
- Todos os endpoints que retornam `IngressoDTO` agora incluem os produtos da bomboniere
- A estrutura é retrocompatível com versões anteriores

## Banco de Dados

A associação entre compras e produtos é feita através da tabela `venda`:

```sql
CREATE TABLE venda (
    id INT PRIMARY KEY AUTO_INCREMENT,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    pagamento_id INT,
    status VARCHAR(20) NOT NULL,
    compra_id INT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

A coluna `compra_id` vincula a venda à compra específica.

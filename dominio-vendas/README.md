# Domínio de Vendas

## Descrição
Bounded Context responsável por **vendas de ingressos e pagamentos** no cinema.

## Agregados

### Compra (Raiz do Agregado)
- **Entidades:** `Compra`, `Ingresso`
- **Value Objects:** `StatusCompra` (PENDENTE, CONFIRMADA, CANCELADA), `StatusIngresso` (ATIVO, VALIDADO, CANCELADO), `TipoIngresso` (MEIA, INTEIRA)
- **Repositório:** `CompraRepositorio`
- **Service:** `CompraService`

### Pagamento
- **Entidade:** `Pagamento`
- **Value Objects:** `StatusPagamento` (PENDENTE, AUTORIZADO, CANCELADO)
- **Repositório:** `PagamentoRepositorio`
- **Service:** `PagamentoService`

### Operações de Auditoria
- **Entidades:** `ValidacaoIngresso`, `RemarcacaoSessao`
- **Repositórios:** `ValidacaoIngressoRepositorio`, `RemarcacaoSessaoRepositorio`

## Responsabilidades

### O que PERTENCE a este domínio:
✅ Iniciar, confirmar e cancelar compras de ingressos
✅ Gerenciar ingressos (geração de QR Code, status)
✅ Processar pagamentos (autorizar, cancelar)
✅ Validar ingressos na entrada do cinema
✅ Remarcar ingressos (trocar sessão/assento)
✅ Registrar histórico de operações (auditoria)

### O que NÃO pertence a este domínio:
❌ Gerenciar sessões e filmes (domínio-sessoes)
❌ Gerenciar usuários (domínio-usuarios)
❌ Vender produtos da bomboniere (dominio-bomboniere)

## Regras de Negócio

### Compra
- **RN1:** Uma compra só pode ser confirmada após a autorização do pagamento
- **RN2:** A compra só pode ser confirmada se o pagamento associado for AUTORIZADO
- **RN3:** Ao cancelar uma compra, o pagamento pendente também é cancelado
- Assentos são reservados temporariamente ao iniciar a compra
- Ingressos recebem QR Code único para validação

### Ingresso
- **RN7:** Um ingresso só pode ser validado uma vez
- **RN8:** Remarcação só é permitida até 2h antes da sessão original
- Tipos: MEIA ou INTEIRA
- Status: ATIVO, VALIDADO, CANCELADO

### Pagamento
- **RN2:** Pagamento deve estar AUTORIZADO para confirmar compra
- Cancelamento automático ao cancelar compra

### Validação (Auditoria)
- Registra todas as tentativas de validação de ingresso
- Armazena funcionário responsável, data/hora e resultado
- Usado para histórico e auditoria

### Remarcação (Auditoria)
- Registra todas as remarcações de ingressos
- Armazena sessão/assento original e novo
- Armazena funcionário responsável e motivo técnico
- Usado para notificações e auditoria

## Dependências
- `dominio-compartilhado` (Value Objects)
- `dominio-sessoes` (para validar disponibilidade de assentos)

## Estrutura de Pacotes
```
com.astra.cinema.dominio/
├── compra/
│   ├── Compra.java (Agregado Raiz)
│   ├── Ingresso.java (Entidade)
│   ├── StatusCompra.java
│   ├── StatusIngresso.java
│   ├── TipoIngresso.java
│   ├── CompraRepositorio.java
│   └── CompraService.java (Domain Service)
├── pagamento/
│   ├── Pagamento.java
│   ├── StatusPagamento.java
│   ├── PagamentoRepositorio.java
│   └── PagamentoService.java (Domain Service)
└── operacao/ ⭐ NOVO (movido de dominio-compartilhado)
    ├── ValidacaoIngresso.java (Auditoria)
    ├── ValidacaoIngressoRepositorio.java
    ├── RemarcacaoSessao.java (Auditoria)
    └── RemarcacaoSessaoRepositorio.java
```

## Linguagem Ubíqua
- **Compra:** Transação de aquisição de um ou mais ingressos
- **Ingresso:** Bilhete de entrada para uma sessão específica
- **Pagamento:** Transação financeira associada a uma compra
- **QR Code:** Código único para validação do ingresso
- **Validação:** Ato de verificar e marcar um ingresso como usado
- **Remarcação:** Transferência de ingresso para outra sessão/assento
- **Meia-entrada:** Ingresso com desconto
- **Inteira:** Ingresso preço cheio

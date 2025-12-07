# Arquitetura do Sistema - Astra Cinema

## Visão Geral
Sistema de gerenciamento de cinema desenvolvido com **Domain-Driven Design (DDD)** e **Arquitetura em Camadas**.

## Padrões Arquiteturais

### 1. Domain-Driven Design (DDD)
- **Bounded Contexts:** Divisão do domínio em contextos delimitados independentes
- **Ubiquitous Language:** Linguagem comum entre desenvolvedores e especialistas do domínio
- **Aggregates:** Agrupamento de entidades e value objects com raiz do agregado
- **Domain Services:** Lógica de negócio que não pertence a uma entidade específica
- **Repositories:** Abstrações para persistência de agregados

### 2. Arquitetura em Camadas

```
┌─────────────────────────────────────────────────────────┐
│                  APRESENTAÇÃO (UI)                      │
│  apresentacao-backend (REST API) + frontend (React)     │
│  Responsabilidade: Controllers, DTOs, Serialização      │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                  APLICAÇÃO (Use Cases)                  │
│  aplicacao/                                             │
│  Responsabilidade: Orquestração, Coordenação            │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                  DOMÍNIO (Negócio)                      │
│  ┌────────────┬──────────┬────────────┬─────────────┐  │
│  │ sessoes    │ vendas   │ bomboniere │  usuarios   │  │
│  │            │          │            │             │  │
│  │ Filmes     │ Compras  │ Produtos   │  Clientes   │  │
│  │ Sessões    │ Ingressos│ Vendas     │ Funcionários│  │
│  │ Programação│ Pagamentos│           │             │  │
│  └────────────┴──────────┴────────────┴─────────────┘  │
│  Responsabilidade: Regras de Negócio, Entidades         │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                  INFRAESTRUTURA                         │
│  infraestrutura/                                        │
│  Responsabilidade: JPA, BD, APIs Externas, Config       │
└─────────────────────────────────────────────────────────┘
```

## Bounded Contexts (Contextos Delimitados)

### 🔵 dominio-compartilhado (Shared Kernel)
**Responsabilidade:** Value Objects e validações compartilhadas

**Contém:**
- Identificadores (IDs) de todas as entidades
- Classe `ValidacaoDominio` com validações reutilizáveis

**Dependências:** Nenhuma

---

### 🟢 dominio-usuarios (Identity & Access)
**Responsabilidade:** Gerenciar identidade e acesso dos usuários

**Agregados:**
- Usuario → Cliente, Funcionario

**Conceitos:**
- Cargo, TipoUsuario
- Autenticação e autorização

**Dependências:**
- dominio-compartilhado

---

### 🟢 dominio-sessoes (Programming)
**Responsabilidade:** Programação de filmes e sessões

**Agregados:**
- Filme
- Sessao
- Programacao

**Conceitos:**
- Catálogo de filmes
- Horários e salas
- Disponibilidade de assentos
- Validação de conflitos

**Dependências:**
- dominio-compartilhado
- dominio-usuarios (para validar permissões)

---

### 🟢 dominio-vendas (Sales)
**Responsabilidade:** Vendas de ingressos e pagamentos

**Agregados:**
- Compra → Ingresso
- Pagamento
- ValidacaoIngresso (auditoria)
- RemarcacaoSessao (auditoria)

**Conceitos:**
- Fluxo de compra
- Pagamentos
- Validação na entrada
- Remarcação de ingressos
- Histórico e auditoria

**Dependências:**
- dominio-compartilhado
- dominio-sessoes (para validar disponibilidade)

---

### 🟢 dominio-bomboniere (Store)
**Responsabilidade:** Loja de conveniência (bomboniere)

**Agregados:**
- Produto
- Venda

**Conceitos:**
- Catálogo de produtos
- Controle de estoque
- Vendas de produtos

**Dependências:**
- dominio-compartilhado
- dominio-vendas (compartilha conceito de Pagamento)

---

## Camadas da Aplicação

### 📋 Camada de Domínio
**Localização:** `dominio-*`

**Responsabilidade:**
- Regras de negócio
- Entidades e Value Objects
- Domain Services
- Interfaces de Repositórios

**Princípio:**
- Não depende de infraestrutura
- Não conhece detalhes de persistência
- Contém a lógica de negócio pura

### 📋 Camada de Aplicação
**Localização:** `aplicacao/`

**Responsabilidade:**
- Use Cases (casos de uso)
- Orquestração de múltiplos agregados
- Coordenação de transações
- DTOs

**Princípio:**
- Não contém lógica de negócio
- Apenas coordena chamadas aos Domain Services
- Fina camada de coordenação

### 📋 Camada de Infraestrutura
**Localização:** `infraestrutura/`

**Responsabilidade:**
- Implementações de repositórios (JPA)
- Configurações (Spring Boot)
- Mapeadores (Domain ↔ JPA)
- Integrações externas

**Tecnologias:**
- Spring Data JPA
- PostgreSQL
- Flyway (migrations)
- ModelMapper

### 📋 Camada de Apresentação
**Localização:** `apresentacao-backend/`, `apresentacao-frontend/`

**Responsabilidade:**
- REST Controllers
- DTOs de entrada/saída
- Interface React
- Serialização JSON

**Tecnologias:**
- Spring Boot REST
- React + Hooks
- Fetch API

---

## Regras de Negócio Principais

### RN1: Confirmação de Compra
Uma compra só pode ser confirmada após a autorização do pagamento.

### RN2: Status do Pagamento
A compra só pode ser confirmada se o pagamento associado for AUTORIZADO (SUCESSO).

### RN3: Cancelamento de Compra
Ao cancelar uma compra, o pagamento pendente também é cancelado automaticamente.

### RN4: Filme em Cartaz
Uma sessão só pode ser criada para filmes com status "EM_CARTAZ".

### RN5: Sessão Esgotada
Uma sessão é marcada como "ESGOTADA" automaticamente quando não há mais assentos disponíveis.

### RN6: Remoção de Filme
Um filme só pode ser removido quando não houver sessões futuras vinculadas a ele.

### RN7: Validação Única
Um ingresso só pode ser validado uma vez. Após validado, não pode ser revalidado.

### RN8: Prazo de Remarcação
A remarcação de um ingresso só é permitida até 2h antes do início da sessão original.

### RN11: Permissões de Gerente
Apenas funcionários com cargo de GERENTE podem:
- Gerenciar filmes (adicionar, remover, alterar)
- Criar e modificar sessões
- Criar programações

### RN12: Sessões Disponíveis na Programação
A programação só pode conter sessões com status "DISPONIVEL".

---

## Melhorias Implementadas (Refatoração DDD)

### ✅ Eliminação de Duplicação de Services
**Problema:** Existiam Domain Services (domínio) e Application Services (aplicação) com o mesmo nome.

**Solução:** Removidos Application Services que apenas delegavam:
- ❌ `aplicacao.FilmeService` → ✅ `dominio.filme.FilmeService`
- ❌ `aplicacao.SessaoService` → ✅ `dominio.sessao.SessaoService`
- ❌ `aplicacao.CompraService` → ✅ `dominio.compra.CompraService`
- ❌ `aplicacao.ProdutoService` → ✅ `dominio.bomboniere.ProdutoService`
- ❌ `aplicacao.ProgramacaoService` → ✅ `dominio.programacao.ProgramacaoService`

**Resultado:** Código mais limpo, sem redundância, mantendo Domain Services com lógica de negócio.

### ✅ Movimentação de Entidades de Operação
**Problema:** Entidades `RemarcacaoSessao` e `ValidacaoIngresso` estavam em `dominio-compartilhado`.

**Solução:** Movidas para `dominio-vendas/operacao/`

**Razão:** São operações de negócio sobre ingressos, não conceitos compartilhados.

### ✅ Documentação DDD
Adicionados arquivos README.md em cada módulo explicando:
- Responsabilidades do bounded context
- Agregados e entidades
- Regras de negócio
- Linguagem ubíqua
- Dependências

---

## Diagrama de Dependências

```
apresentacao-backend
        ↓
   infraestrutura
        ↓
     aplicacao
        ↓
    ┌───┴────┬────────┬─────────────┐
    ↓        ↓        ↓             ↓
usuarios  sessoes  vendas     bomboniere
    ↓        ↓        ↓             ↓
    └────────┴────────┴─────────────┘
                  ↓
         dominio-compartilhado
```

**Princípio:** Dependências sempre apontam para dentro (domínio não depende de infraestrutura).

---

## Tecnologias

### Backend
- Java 17
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- Flyway
- Maven

### Frontend
- React 18
- React Hooks
- Fetch API
- CSS Modules

### Arquitetura
- DDD (Domain-Driven Design)
- Clean Architecture
- SOLID Principles
- Repository Pattern

---

## Como Navegar no Código

1. **Para entender regras de negócio:** Veja os módulos `dominio-*`
2. **Para entender casos de uso:** Veja `aplicacao/*UseCase.java`
3. **Para entender APIs REST:** Veja `apresentacao-backend/rest/*Controller.java`
4. **Para entender persistência:** Veja `infraestrutura/persistencia/jpa/`

Cada módulo de domínio tem seu próprio `README.md` explicando detalhes.

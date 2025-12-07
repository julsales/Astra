# Domínio de Sessões

## Descrição
Bounded Context responsável pela **programação de filmes e sessões** do cinema.

## Agregados

### Filme
- **Entidade:** `Filme`
- **Value Objects:** `StatusFilme` (EM_CARTAZ, FORA_DE_CARTAZ, REMOVIDO)
- **Repositório:** `FilmeRepositorio`
- **Service:** `FilmeService`

### Sessao
- **Entidade:** `Sessao`
- **Value Objects:** `StatusSessao` (DISPONIVEL, ESGOTADA, CANCELADA)
- **Repositório:** `SessaoRepositorio`
- **Service:** `SessaoService`

### Programacao
- **Entidade:** `Programacao`
- **Repositório:** `ProgramacaoRepositorio`
- **Service:** `ProgramacaoService`

## Responsabilidades

### O que PERTENCE a este domínio:
✅ Catálogo de filmes (adicionar, remover, alterar status)
✅ Criação e gerenciamento de sessões
✅ Controle de assentos disponíveis
✅ Programação semanal de sessões
✅ Validação de conflitos de horário

### O que NÃO pertence a este domínio:
❌ Compra de ingressos (domínio-vendas)
❌ Pagamentos (domínio-vendas)
❌ Validação de ingressos na entrada (domínio-vendas)

## Regras de Negócio

### Filmes
- **RN4:** Uma sessão só pode ser criada para filmes com status "EM_CARTAZ"
- **RN6:** Um filme só pode ser removido quando não houver sessões futuras vinculadas
- **RN11:** Apenas GERENTES podem gerenciar filmes

### Sessões
- **RN5:** Uma sessão é marcada como "ESGOTADA" quando não há mais assentos disponíveis
- **RN11:** Apenas GERENTES podem criar/modificar sessões
- Sessões devem ter horário futuro
- Não pode haver conflito de horário na mesma sala

### Programação
- **RN11:** Apenas GERENTES podem criar programações
- **RN12:** A programação só pode conter sessões com status "DISPONIVEL"
- Não pode haver sessões duplicadas em uma programação
- Todas as sessões devem estar dentro do período da programação
- Validação de conflitos de horário na mesma sala

## Dependências
- `dominio-compartilhado` (Value Objects)
- `dominio-usuarios` (para validar permissões de Funcionario)

## Estrutura de Pacotes
```
com.astra.cinema.dominio/
├── filme/
│   ├── Filme.java
│   ├── StatusFilme.java
│   ├── FilmeRepositorio.java
│   └── FilmeService.java (Domain Service)
├── sessao/
│   ├── Sessao.java
│   ├── StatusSessao.java
│   ├── SessaoRepositorio.java
│   └── SessaoService.java (Domain Service)
└── programacao/
    ├── Programacao.java
    ├── ProgramacaoRepositorio.java
    └── ProgramacaoService.java (Domain Service)
```

## Linguagem Ubíqua
- **Filme:** Obra cinematográfica que pode ser exibida no cinema
- **Sessão:** Exibição de um filme em horário e sala específicos
- **Programação:** Conjunto de sessões organizadas em um período semanal
- **Assento:** Lugar específico na sala de cinema
- **Sala:** Ambiente físico onde a sessão é exibida
- **Esgotada:** Sessão sem assentos disponíveis
- **Em Cartaz:** Filme disponível para criar novas sessões

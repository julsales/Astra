# Padrões de Projeto Implementados

Este documento descreve os padrões de projeto adotados no sistema Astra Cinema, listando as classes criadas e/ou alteradas para cada padrão.

---

## 1. Iterator

**Descrição**: Padrão comportamental que fornece uma forma de acessar sequencialmente os elementos de uma coleção sem expor sua representação interna.

**Aplicação**: Utilizado para iterar sobre a coleção de assentos de uma sessão, permitindo filtrar assentos disponíveis e ocupados.

### Classes Implementadas

#### `ColecaoAssentos`
- **Localização**: `dominio-sessoes/src/main/java/com/astra/cinema/dominio/sessao/ColecaoAssentos.java`
- **Papel**: Aggregate (coleção iterável)
- **Responsabilidades**:
  - Mantém mapa de assentos com seus status (disponível/ocupado)
  - Implementa `Iterable<Map.Entry<AssentoId, Boolean>>`
  - Fornece métodos `disponiveis()` e `ocupados()` que retornam iteráveis filtrados
  - Usa cópia defensiva para proteger estado interno

#### `AssentoIterator`
- **Localização**: `dominio-sessoes/src/main/java/com/astra/cinema/dominio/sessao/AssentoIterator.java`
- **Papel**: Iterator concreto
- **Responsabilidades**:
  - Itera sobre entradas do mapa de assentos
  - Filtra assentos por status (disponível ou ocupado)
  - Implementa `Iterator<Map.Entry<AssentoId, Boolean>>`

### Correções Aplicadas
- ✅ Adicionada cópia defensiva em `ColecaoAssentos` para evitar exposição de estado mutável

---

## 2. Decorator

**Descrição**: Padrão estrutural que permite adicionar comportamentos a objetos dinamicamente, envolvendo-os em objetos decoradores.

**Aplicação**: Cadeia de validadores de ingressos que podem ser combinados para aplicar múltiplas regras de validação.

### Classes Implementadas

#### `ValidadorIngresso` (Interface)
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/validacao/ValidadorIngresso.java`
- **Papel**: Component interface
- **Responsabilidades**:
  - Define contrato `validar(Ingresso ingresso)`
  - Retorna `ResultadoValidacao` com status e mensagens

#### `ValidadorIngressoBase`
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/validacao/ValidadorIngressoBase.java`
- **Papel**: Concrete Component (validador base)
- **Responsabilidades**:
  - Implementação básica sem validações adicionais
  - Ponto de partida da cadeia de decoradores

#### `ValidadorDuplicidade`
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/validacao/ValidadorDuplicidade.java`
- **Papel**: Concrete Decorator
- **Responsabilidades**:
  - Valida se ingresso já foi validado anteriormente
  - Mantém cache thread-safe de ingressos validados
  - Delega para próximo validador na cadeia

#### `ValidadorSessaoAtiva`
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/validacao/ValidadorSessaoAtiva.java`
- **Papel**: Concrete Decorator
- **Responsabilidades**:
  - Verifica se sessão está ativa e não cancelada
  - Valida se sessão ainda não ocorreu

#### `ValidadorAssentoDisponivel`
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/validacao/ValidadorAssentoDisponivel.java`
- **Papel**: Concrete Decorator
- **Responsabilidades**:
  - Verifica se assento está disponível na sessão
  - Valida se assento existe e não está ocupado

### Correções Aplicadas
- ✅ Implementado cache thread-safe com `ConcurrentHashMap.newKeySet()` em `ValidadorDuplicidade`
- ✅ Adicionado construtor que aceita cache externo para facilitar testes e injeção de dependências

---

## 3. Observer

**Descrição**: Padrão comportamental que define uma dependência um-para-muitos entre objetos, onde mudanças em um objeto notificam automaticamente seus dependentes.

**Aplicação**: Sistema de notificação de eventos de compra, permitindo múltiplos observadores reagirem a confirmações de compra.

### Classes Implementadas

#### `ObservadorEvento` (Interface)
- **Localização**: `dominio-compartilhado/src/main/java/com/astra/cinema/dominio/eventos/ObservadorEvento.java`
- **Papel**: Observer interface
- **Responsabilidades**:
  - Define método `atualizar(Evento evento)`
  - Permite que observadores reajam a eventos

#### `PublicadorEventos`
- **Localização**: `dominio-compartilhado/src/main/java/com/astra/cinema/dominio/eventos/PublicadorEventos.java`
- **Papel**: Subject (publicador)
- **Responsabilidades**:
  - Gerencia registro e remoção de observadores
  - Publica eventos para todos os observadores registrados
  - Thread-safe com `ConcurrentHashMap` e `CopyOnWriteArrayList`

#### `CompraConfirmadaEvento`
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/eventos/CompraConfirmadaEvento.java`
- **Papel**: Concrete Event
- **Responsabilidades**:
  - Carrega dados do evento de compra confirmada
  - Contém `compraId`, `clienteId`, `quantidadeIngressos`, `timestamp`

#### `NotificadorEmailCompraImpl`
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/eventos/NotificadorEmailCompraImpl.java`
- **Papel**: Concrete Observer
- **Responsabilidades**:
  - Simula envio de email quando compra é confirmada
  - Registra log de notificação

#### `AtualizadorEstatisticasCompraImpl`
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/eventos/AtualizadorEstatisticasCompraImpl.java`
- **Papel**: Concrete Observer
- **Responsabilidades**:
  - Atualiza estatísticas quando compra é confirmada
  - Registra log de atualização

#### `ConfirmarCompraUseCase`
- **Localização**: `aplicacao/src/main/java/com/astra/cinema/aplicacao/compra/ConfirmarCompraUseCase.java`
- **Papel**: Publisher (usa PublicadorEventos)
- **Modificações**:
  - Injeta `PublicadorEventos` via construtor
  - Publica `CompraConfirmadaEvento` após confirmar compra

#### `CompraService`
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/compra/CompraService.java`
- **Papel**: Publisher (usa PublicadorEventos)
- **Modificações**:
  - Injeta `PublicadorEventos` via construtor
  - Publica eventos após operações de compra

### Configuração

#### `InfraestruturaConfiguration`
- **Localização**: `infraestrutura/src/main/java/com/astra/cinema/infraestrutura/config/InfraestruturaConfiguration.java`
- **Modificações**:
  - Registra `PublicadorEventos` como `@Bean`
  - Registra observadores (`NotificadorEmailCompraImpl`, `AtualizadorEstatisticasCompraImpl`)
  - Configura injeção de dependências via Spring

### Correções Aplicadas
- ✅ Removido padrão Singleton anti-pattern de `PublicadorEventos`
- ✅ Implementada injeção de dependências via Spring
- ✅ Adicionada thread-safety com `ConcurrentHashMap` e `CopyOnWriteArrayList`
- ✅ Construtor público para permitir gerenciamento pelo container DI

---

## 4. Template Method

**Descrição**: Padrão comportamental que define o esqueleto de um algoritmo em uma classe base, permitindo que subclasses sobrescrevam etapas específicas sem alterar a estrutura geral.

**Aplicação**: Processamento de pagamentos com diferentes métodos (PIX, Cartão), onde o fluxo geral é o mesmo mas detalhes de processamento variam.

### Classes Implementadas

#### `ProcessadorPagamento` (Classe Abstrata)
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/pagamento/ProcessadorPagamento.java`
- **Papel**: Template Method (classe abstrata)
- **Responsabilidades**:
  - Define método template `processar(Pagamento pagamento)` (final)
  - Orquestra etapas: validar → processar específico → confirmar
  - Declara métodos abstratos para subclasses implementarem:
    - `validarPagamento(Pagamento)`
    - `processarEspecifico(Pagamento)`
    - `confirmarPagamento(Pagamento)`

#### `ProcessadorPagamentoPix`
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/pagamento/ProcessadorPagamentoPix.java`
- **Papel**: Concrete Implementation
- **Responsabilidades**:
  - Implementa validação específica para PIX
  - Processa pagamento via PIX
  - Confirma pagamento PIX

#### `ProcessadorPagamentoCartao`
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/pagamento/ProcessadorPagamentoCartao.java`
- **Papel**: Concrete Implementation
- **Responsabilidades**:
  - Implementa validação específica para Cartão
  - Processa pagamento via Cartão
  - Confirma pagamento Cartão

---

## Correções de Arquitetura

Além dos padrões de projeto, foram aplicadas correções arquiteturais para seguir os princípios de Clean Architecture e DDD:

### Problema Identificado
Controllers acessavam repositories diretamente, violando a separação de camadas ("um pecado grande" segundo feedback do professor).

### Solução Aplicada
Todos os controllers foram corrigidos para acessar apenas Services ou Use Cases:

#### Classes Modificadas

1. **ProgramacaoController**
   - ❌ Antes: Injetava `ProgramacaoRepositorio`, `SessaoRepositorio`, `FilmeRepositorio`
   - ✅ Depois: Injeta apenas `ProgramacaoService`

2. **SalaController**
   - ❌ Antes: Injetava `SalaRepositorio`
   - ✅ Depois: Injeta `SalaService`

3. **ProdutoController**
   - ❌ Antes: Criava use cases com `new` dentro dos métodos
   - ✅ Depois: Injeta `EntradaEstoqueUseCase` e `AjusteEstoqueUseCase`

4. **AuthController**
   - ❌ Antes: Injetava `ClienteRepositorio` para buscar clienteId
   - ✅ Depois: Usa `resultado.getClienteId()` de `AutenticarUsuarioUseCase`

5. **RelatorioController**
   - ❌ Antes: Injetava `RemarcacaoSessaoRepositorio`
   - ✅ Depois: Injeta `ListarRemarcacoesUseCase`

#### Classes Criadas

- `SalaService` - Service para operações de Sala
- `ListarRemarcacoesUseCase` - Use case para listar remarcações

#### Classes Atualizadas

- `AutenticarUsuarioUseCase` - Agora retorna `clienteId` no resultado
- `UseCaseConfiguration` - Registra novos beans (services e use cases)
- `ProgramacaoService` - Adicionado método `listarProgramacoes()`

---

## Resumo

O projeto implementa **4 padrões de projeto GoF**:

1. ✅ **Iterator** - Iteração sobre coleção de assentos
2. ✅ **Decorator** - Cadeia de validadores de ingressos
3. ✅ **Observer** - Sistema de eventos de compra
4. ✅ **Template Method** - Processamento de pagamentos

Todas as implementações seguem boas práticas:
- Thread-safety onde necessário
- Injeção de dependências via Spring
- Separação clara de responsabilidades
- Arquitetura em camadas respeitada (Controllers → Services/Use Cases → Repositories)

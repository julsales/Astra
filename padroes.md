# Padrões de Projeto Implementados

Este documento descreve os padrões de projeto adotados no sistema Astra Cinema, organizados por padrão e relacionados às funcionalidades do sistema.

---

## 1. Iterator

**Descrição**: Padrão comportamental que fornece uma forma de acessar sequencialmente os elementos de uma coleção sem expor sua representação interna.

**Funcionalidades Relacionadas**:
- Iniciar compra (reserva de assentos) - iteração sobre assentos disponíveis
- Criar sessão - iteração sobre assentos da sala

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

### Benefícios
- ✅ Encapsulamento da estrutura interna de assentos
- ✅ Facilita filtragem de assentos por status
- ✅ Proteção contra modificações externas com cópia defensiva

---

## 2. Decorator

**Descrição**: Padrão estrutural que permite adicionar comportamentos a objetos dinamicamente, envolvendo-os em objetos decoradores.

**Funcionalidades Relacionadas**:
- Iniciar compra (reserva de assentos) - validação de ingressos
- Confirmar compra (após pagamento) - validação de ingressos
- Cancelar compra - validação de ingressos

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

#### `ValidadorHorario`
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/validacao/ValidadorHorario.java`
- **Papel**: Concrete Decorator
- **Responsabilidades**:
  - Valida horário da sessão
  - Verifica se sessão está no horário adequado

#### `ValidadorQRCode`
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/validacao/ValidadorQRCode.java`
- **Papel**: Concrete Decorator
- **Responsabilidades**:
  - Valida código QR do ingresso
  - Verifica autenticidade do código

### Benefícios
- ✅ Composição flexível de validações
- ✅ Fácil adição de novas regras de validação
- ✅ Thread-safety com `ConcurrentHashMap.newKeySet()`
- ✅ Testabilidade com injeção de cache externo

---

## 3. Observer

**Descrição**: Padrão comportamental que define uma dependência um-para-muitos entre objetos, onde mudanças em um objeto notificam automaticamente seus dependentes.

**Funcionalidades Relacionadas**:
- Confirmar compra (após pagamento) - notificação de eventos
- Confirmar venda na bomboniere - notificação de eventos

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
  - Contém `compraId`, `clienteId`, `quantidadeIngressos`

#### `NotificadorEmailCompraImpl`
- **Localização**: `infraestrutura/src/main/java/com/astra/cinema/infraestrutura/eventos/NotificadorEmailCompraImpl.java`
- **Papel**: Concrete Observer
- **Responsabilidades**:
  - Simula envio de email quando compra é confirmada
  - Registra log de notificação

#### `AtualizadorEstatisticasCompraImpl`
- **Localização**: `infraestrutura/src/main/java/com/astra/cinema/infraestrutura/eventos/AtualizadorEstatisticasCompraImpl.java`
- **Papel**: Concrete Observer
- **Responsabilidades**:
  - Atualiza estatísticas quando compra é confirmada
  - Registra log de atualização

#### `CompraService`
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/compra/CompraService.java`
- **Papel**: Publisher (usa PublicadorEventos)
- **Modificações**:
  - Injeta `PublicadorEventos` via construtor
  - Publica `CompraConfirmadaEvento` após confirmar compra
  - Método `confirmar()` dispara o evento

### Configuração

#### `InfraestruturaConfiguration`
- **Localização**: `infraestrutura/src/main/java/com/astra/cinema/infraestrutura/config/InfraestruturaConfiguration.java`
- **Modificações**:
  - Registra `PublicadorEventos` como `@Bean`
  - Registra observadores (`NotificadorEmailCompraImpl`, `AtualizadorEstatisticasCompraImpl`)
  - Configura injeção de dependências via Spring

### Benefícios
- ✅ Desacoplamento entre eventos e ações
- ✅ Fácil adição de novos observadores
- ✅ Thread-safety com coleções concorrentes
- ✅ Gerenciamento via Spring DI (sem singleton anti-pattern)

---

## 4. Template Method

**Descrição**: Padrão comportamental que define o esqueleto de um algoritmo em uma classe base, permitindo que subclasses sobrescrevam etapas específicas sem alterar a estrutura geral.

**Funcionalidades Relacionadas**:
- Confirmar compra (após pagamento) - processamento de pagamentos
- Confirmar venda na bomboniere - processamento de pagamentos

### Classes Implementadas

#### `ProcessadorPagamento` (Classe Abstrata)
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/pagamento/ProcessadorPagamento.java`
- **Papel**: Template Method (classe abstrata no domínio)
- **Responsabilidades**:
  - Define método template `processar(Pagamento pagamento, BigDecimal valor)` (final)
  - Orquestra etapas do algoritmo:
    1. `validarDados()` - valida dados do pagamento
    2. `verificarLimites()` - verifica limites de valor
    3. `processarComGateway()` - processa com gateway (abstrato)
    4. `tratarFalha()` - trata falhas
    5. `confirmarTransacao()` - confirma transação
    6. `gerarComprovante()` - gera comprovante
  - Métodos abstratos para subclasses implementarem:
    - `processarComGateway(Pagamento, BigDecimal)` - OBRIGATÓRIO
    - `getNome()` - OBRIGATÓRIO

#### `ProcessadorPixImpl`
- **Localização**: `infraestrutura/src/main/java/com/astra/cinema/infraestrutura/pagamento/ProcessadorPixImpl.java`
- **Papel**: Concrete Implementation (na infraestrutura)
- **Responsabilidades**:
  - Implementa processamento específico para PIX
  - Integração com APIs do Banco Central/PSPs
  - Alta taxa de aprovação (95%)
  - Sem limite por transação
  - Comprovante específico do PIX

#### `ProcessadorCartaoCreditoImpl`
- **Localização**: `infraestrutura/src/main/java/com/astra/cinema/infraestrutura/pagamento/ProcessadorCartaoCreditoImpl.java`
- **Papel**: Concrete Implementation (na infraestrutura)
- **Responsabilidades**:
  - Implementa processamento específico para Cartão de Crédito
  - Integração com gateways (Cielo, Rede, PagSeguro)
  - Taxa de aprovação de 90%
  - Limite de R$ 5.000 por transação
  - Validação de bandeira e parcelas

#### `ProcessadorDinheiroImpl`
- **Localização**: `infraestrutura/src/main/java/com/astra/cinema/infraestrutura/pagamento/ProcessadorDinheiroImpl.java`
- **Papel**: Concrete Implementation (na infraestrutura)
- **Responsabilidades**:
  - Implementa processamento específico para Dinheiro
  - Validação de troco
  - Aprovação instantânea

### Benefícios
- ✅ Reutilização do fluxo geral de processamento
- ✅ Flexibilidade para diferentes métodos de pagamento
- ✅ Facilita adição de novos métodos de pagamento
- ✅ Algoritmo bem definido com pontos de extensão claros
- ✅ Separação entre domínio (template) e infraestrutura (implementações)

---

## 5. State

**Descrição**: Padrão comportamental que permite que um objeto altere seu comportamento quando seu estado interno muda, parecendo que a classe do objeto mudou.

**Funcionalidades Relacionadas**:
- Marcar sessão como esgotada - transição de estados da sessão
- Criar sessão - inicialização com estado DISPONIVEL
- Cancelar compra - transição de estados da compra

### Classes Implementadas

#### `StatusSessao` (Enum)
- **Localização**: `dominio-sessoes/src/main/java/com/astra/cinema/dominio/sessao/StatusSessao.java`
- **Papel**: Estados possíveis da sessão
- **Estados**:
  - `DISPONIVEL` - sessão com assentos disponíveis
  - `ESGOTADA` - todos os assentos reservados
  - `CANCELADA` - sessão cancelada

#### `Sessao`
- **Localização**: `dominio-sessoes/src/main/java/com/astra/cinema/dominio/sessao/Sessao.java`
- **Papel**: Context (contexto com estados)
- **Responsabilidades**:
  - Mantém estado atual (`StatusSessao status`)
  - Implementa transições de estado:
    - `reservarAssento()` → pode transicionar para ESGOTADA
    - `liberarAssento()` → pode transicionar de ESGOTADA para DISPONIVEL
    - `marcarComoEsgotada()` → transição explícita para ESGOTADA
  - Valida transições de estado (ex: só marca esgotada se não há assentos disponíveis)

#### `StatusCompra` (Enum)
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/compra/StatusCompra.java`
- **Papel**: Estados possíveis da compra
- **Estados**:
  - `PENDENTE` - compra iniciada, aguardando pagamento
  - `CONFIRMADA` - pagamento aprovado
  - `CANCELADA` - compra cancelada

#### `Compra`
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/compra/Compra.java`
- **Papel**: Context (contexto com estados)
- **Responsabilidades**:
  - Mantém estado atual (`StatusCompra status`)
  - Implementa transições de estado:
    - `confirmar()` → PENDENTE para CONFIRMADA
    - `cancelar()` → para CANCELADA
  - Valida transições (ex: só pode cancelar se não estiver confirmada)

### Benefícios
- ✅ Transições de estado explícitas e validadas
- ✅ Comportamento diferente por estado
- ✅ Facilita adição de novos estados
- ✅ Código mais legível e mantível

---

## 6. Strategy

**Descrição**: Padrão comportamental que define uma família de algoritmos, encapsula cada um e os torna intercambiáveis, permitindo que o algoritmo varia independentemente dos clientes que o utilizam.

**Funcionalidades Relacionadas**:
- Criar programação semanal - validação de conflitos de horário
- Remover filme do catálogo - validação de sessões futuras
- Gerenciar Cinema (controle de acesso) - controle de permissões por cargo

### Classes Implementadas

#### Estratégia 1: Validação de Programação Semanal

##### `ProgramacaoService`
- **Localização**: `dominio-sessoes/src/main/java/com/astra/cinema/dominio/programacao/ProgramacaoService.java`
- **Papel**: Context que usa estratégia de validação
- **Responsabilidades**:
  - Método `criarProgramacao()` valida regras de negócio:
    - RN11: Apenas gerentes podem criar programações
    - RN12: Programação só pode conter sessões DISPONIVEL
    - Validação de conflitos de horário na mesma sala
    - Validação de período (não pode ser no passado)
  - Método `validarConflitosDeHorario()` implementa estratégia de detecção de conflitos:
    - Agrupa sessões por sala
    - Ordena por horário
    - Verifica sobreposição entre sessões consecutivas

#### Estratégia 2: Remover Filme do Catálogo

##### `RemoverFilmeUseCase`
- **Localização**: `aplicacao/src/main/java/com/astra/cinema/aplicacao/filme/RemoverFilmeUseCase.java`
- **Papel**: Use Case que implementa estratégia de remoção segura
- **Responsabilidades**:
  - Valida se filme existe no catálogo
  - Verifica se há sessões futuras agendadas para o filme
  - Impede remoção se houver sessões futuras (regra de negócio)
  - Altera status do filme para RETIRADO (não deleta fisicamente)
  - Método `podeRemover()` verifica pré-condições

##### `Filme`
- **Localização**: `dominio-sessoes/src/main/java/com/astra/cinema/dominio/filme/Filme.java`
- **Papel**: Entidade do domínio
- **Responsabilidades**:
  - Método `retirarDeCartaz()` altera status para RETIRADO
  - Mantém histórico do filme no sistema

#### Estratégia 3: Gerenciar Cinema (Controle de Acesso)

##### `GerenciarCinemaUseCase`
- **Localização**: `aplicacao/src/main/java/com/astra/cinema/aplicacao/usuario/GerenciarCinemaUseCase.java`
- **Papel**: Use Case que implementa estratégia de controle de acesso (também usa padrão Proxy)
- **Responsabilidades**:
  - Valida permissões baseadas em cargo do funcionário
  - Apenas gerentes podem:
    - Criar/remover filmes
    - Criar/cancelar sessões
    - Gerenciar produtos da bomboniere
    - Criar programações semanais
  - Métodos de validação:
    - `validarPermissaoGerencial()` - validação genérica
    - `validarPermissaoCriarSessao()` - específica para sessões
    - `validarPermissaoRemoverFilme()` - específica para filmes
  - Interface funcional `OperacaoGerencial` para executar operações protegidas
  - Método `executarOperacaoGerencial()` aplica proxy de segurança

##### `Funcionario`
- **Localização**: `dominio-usuarios/src/main/java/com/astra/cinema/dominio/usuario/Funcionario.java`
- **Papel**: Entidade do domínio
- **Responsabilidades**:
  - Método `isGerente()` verifica se cargo é GERENTE
  - Atributo `cargo` determina nível de acesso

### Benefícios
- ✅ Validações encapsuladas e reutilizáveis
- ✅ Fácil substituição de algoritmos de validação
- ✅ Testabilidade isolada de cada estratégia
- ✅ Separação de responsabilidades
- ✅ Segurança centralizada no controle de acesso
- ✅ Flexibilidade para diferentes níveis de permissão

---

## 7. Repository

**Descrição**: Padrão arquitetural que encapsula a lógica de acesso a dados, fornecendo uma interface de coleção para acessar objetos de domínio.

**Funcionalidades Relacionadas**:
- Todas as funcionalidades do sistema utilizam repositórios para persistência

### Repositórios Implementados

#### `CompraRepositorio`
- **Localização**: `dominio-vendas/src/main/java/com/astra/cinema/dominio/compra/CompraRepositorio.java`
- **Funcionalidades**: Iniciar compra, Confirmar compra, Cancelar compra

#### `SessaoRepositorio`
- **Localização**: `dominio-sessoes/src/main/java/com/astra/cinema/dominio/sessao/SessaoRepositorio.java`
- **Funcionalidades**: Criar sessão, Marcar sessão como esgotada

#### `FilmeRepositorio`
- **Localização**: `dominio-sessoes/src/main/java/com/astra/cinema/dominio/filme/FilmeRepositorio.java`
- **Funcionalidades**: Remover filme do catálogo

#### `ProgramacaoRepositorio`
- **Localização**: `dominio-sessoes/src/main/java/com/astra/cinema/dominio/programacao/ProgramacaoRepositorio.java`
- **Funcionalidades**: Criar programação semanal

#### `ProdutoRepositorio`
- **Localização**: `dominio-bomboniere/src/main/java/com/astra/cinema/dominio/bomboniere/ProdutoRepositorio.java`
- **Funcionalidades**: Vender produto na bomboniere

#### `VendaRepositorio`
- **Localização**: `dominio-bomboniere/src/main/java/com/astra/cinema/dominio/bomboniere/VendaRepositorio.java`
- **Funcionalidades**: Confirmar venda na bomboniere

### Benefícios
- ✅ Abstração da camada de persistência
- ✅ Facilita testes com mocks/stubs
- ✅ Mudança de tecnologia de banco sem impactar domínio
- ✅ Separação clara entre domínio e infraestrutura

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

O projeto implementa **7 padrões de projeto**:

### Padrões GoF (Gang of Four):

1. ✅ **Iterator** - Iteração sobre coleção de assentos
   - Usado em: Iniciar compra, Criar sessão

2. ✅ **Decorator** - Cadeia de validadores de ingressos
   - Usado em: Iniciar compra, Confirmar compra, Cancelar compra

3. ✅ **Observer** - Sistema de eventos de compra
   - Usado em: Confirmar compra, Confirmar venda na bomboniere

4. ✅ **Template Method** - Processamento de pagamentos
   - Usado em: Confirmar compra, Confirmar venda na bomboniere

5. ✅ **State** - Gerenciamento de estados de sessão e compra
   - Usado em: Marcar sessão como esgotada, Criar sessão, Cancelar compra

6. ✅ **Strategy** - Validações e controle de acesso
   - Usado em: Criar programação semanal (validação de conflitos), Remover filme do catálogo (validação de sessões), Gerenciar Cinema (controle de permissões)

### Padrões Arquiteturais:

7. ✅ **Repository** - Abstração de acesso a dados
   - Usado em: Todas as funcionalidades do sistema

---

## Boas Práticas Implementadas

Todas as implementações seguem boas práticas de engenharia de software:

- ✅ **Thread-safety** onde necessário (Iterator, Decorator, Observer)
- ✅ **Injeção de dependências** via Spring Framework
- ✅ **Separação clara de responsabilidades** (SRP - Single Responsibility Principle)
- ✅ **Arquitetura em camadas** respeitada (Controllers → Services/Use Cases → Repositories)
- ✅ **Clean Architecture** e **Domain-Driven Design (DDD)**
- ✅ **Open/Closed Principle** - aberto para extensão, fechado para modificação
- ✅ **Dependency Inversion Principle** - dependência de abstrações, não de implementações

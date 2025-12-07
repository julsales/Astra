# Padrões de Projeto Implementados - Astra Cinema

Este documento descreve os **5 padrões de projeto** (Design Patterns) implementados no sistema, conforme requisitos da 2ª entrega.

---

## 📋 Índice

1. [Strategy](#1-strategy---estratégias-de-cálculo-de-preço)
2. [Observer](#2-observer---sistema-de-notificações)
3. [Template Method](#3-template-method---processamento-de-pagamento)
4. [Decorator](#4-decorator---validadores-de-ingresso)
5. [Iterator](#5-iterator---percorrer-assentos)

---

## 1. STRATEGY - Estratégias de Cálculo de Preço

### 📝 Descrição
Permite definir diferentes **algoritmos de precificação** de ingressos e trocar entre eles em tempo de execução, sem modificar a entidade Ingresso.

### 🎯 Problema Resolvido
Diferentes tipos de ingresso têm regras de desconto diferentes:
- **Inteira**: 100% do preço
- **Meia-entrada**: 50% do preço
- **Promocional**: desconto customizado (matinê, VIP, estudante, etc)

Sem o padrão, teríamos vários `if/else` espalhados pelo código para calcular preços.

### 🏗️ Estrutura

```
CalculadoraPreco (interface Strategy)
    ├── PrecoInteira (ConcreteStrategy)
    ├── PrecoMeiaEntrada (ConcreteStrategy)
    └── PrecoPromocional (ConcreteStrategy)

CalculadoraPrecoFactory (Factory para criar Strategies)
```

### 📂 Localização
- [`dominio-vendas/compra/CalculadoraPreco.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/compra/CalculadoraPreco.java)
- [`dominio-vendas/compra/PrecoInteira.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/compra/PrecoInteira.java)
- [`dominio-vendas/compra/PrecoMeiaEntrada.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/compra/PrecoMeiaEntrada.java)
- [`dominio-vendas/compra/PrecoPromocional.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/compra/PrecoPromocional.java)
- [`dominio-vendas/compra/CalculadoraPrecoFactory.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/compra/CalculadoraPrecoFactory.java)

### 💻 Exemplo de Uso

```java
// Usando Factory para criar estratégia
CalculadoraPreco calculadora = CalculadoraPrecoFactory.criar(TipoIngresso.MEIA);
BigDecimal precoFinal = calculadora.calcular(new BigDecimal("40.00"));
// Retorna 20.00 (50% de desconto)

// Estratégia promocional customizada
CalculadoraPreco matine = CalculadoraPrecoFactory.criarPromocional(
    new BigDecimal("0.30"), "Matinê"
);
BigDecimal precoMatine = matine.calcular(new BigDecimal("40.00"));
// Retorna 28.00 (30% de desconto)
```

### ✅ Benefícios
- ✅ Fácil adicionar novos tipos de desconto
- ✅ Algoritmos de precificação encapsulados
- ✅ Código limpo, sem condicionais complexas
- ✅ Facilita testes unitários

---

## 2. OBSERVER - Sistema de Notificações

### 📝 Descrição
Implementa um **sistema de eventos** onde objetos (observadores) são notificados automaticamente quando eventos importantes ocorrem no sistema.

### 🎯 Problema Resolvido
Quando uma compra é confirmada, várias ações precisam ocorrer:
- Enviar e-mail para o cliente
- Atualizar estatísticas
- Registrar em log de auditoria
- Notificar sistemas externos

Sem Observer, essas ações ficariam todas acopladas no código de confirmação de compra.

### 🏗️ Estrutura

```
PublicadorEventos (Subject - Singleton)
    ↓ notifica
ObservadorEvento<T> (Observer interface)
    ├── NotificadorEmailCompra (ConcreteObserver)
    └── AtualizadorEstatisticasCompra (ConcreteObserver)

CompraConfirmadaEvento (Evento concreto)
```

### 📂 Localização
- [`dominio-compartilhado/eventos/ObservadorEvento.java`](dominio-compartilhado/src/main/java/com/astra/cinema/dominio/eventos/ObservadorEvento.java)
- [`dominio-compartilhado/eventos/PublicadorEventos.java`](dominio-compartilhado/src/main/java/com/astra/cinema/dominio/eventos/PublicadorEventos.java)
- [`dominio-vendas/eventos/CompraConfirmadaEvento.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/eventos/CompraConfirmadaEvento.java)
- [`dominio-vendas/eventos/NotificadorEmailCompra.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/eventos/NotificadorEmailCompra.java)
- [`dominio-vendas/eventos/AtualizadorEstatisticasCompra.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/eventos/AtualizadorEstatisticasCompra.java)

### 💻 Exemplo de Uso

```java
// Registrar observadores (feito uma vez na inicialização)
PublicadorEventos publicador = PublicadorEventos.getInstancia();
publicador.registrar(new NotificadorEmailCompra());
publicador.registrar(new AtualizadorEstatisticasCompra());

// Publicar evento (quando compra é confirmada)
CompraConfirmadaEvento evento = new CompraConfirmadaEvento(
    compraId, clienteId, quantidadeIngressos
);
publicador.publicar(evento);

// Todos os observadores são notificados automaticamente
// 📧 E-mail enviado
// 📊 Estatísticas atualizadas
```

### ✅ Benefícios
- ✅ Baixo acoplamento entre componentes
- ✅ Fácil adicionar novos comportamentos (novos observers)
- ✅ Princípio Open/Closed (aberto para extensão, fechado para modificação)
- ✅ Reativo e orientado a eventos

---

## 3. TEMPLATE METHOD - Processamento de Pagamento

### 📝 Descrição
Define o **esqueleto de um algoritmo** em uma classe base, permitindo que subclasses sobrescrevam etapas específicas sem alterar a estrutura geral.

### 🎯 Problema Resolvido
Diferentes formas de pagamento (Cartão, PIX, Dinheiro) seguem o mesmo fluxo:
1. Validar dados
2. Verificar limites
3. **Processar com gateway** (específico)
4. Confirmar transação
5. Gerar comprovante

O processamento com gateway varia por tipo, mas o resto é igual.

### 🏗️ Estrutura

```
ProcessadorPagamento (Template abstrato)
    ├── processar() [FINAL - template method]
    ├── validarDados() [pode ser sobrescrito]
    ├── verificarLimites() [pode ser sobrescrito]
    ├── processarComGateway() [ABSTRATO - deve implementar]
    └── gerarComprovante() [pode ser sobrescrito]

Implementações:
    ├── ProcessadorCartaoCredito
    ├── ProcessadorPix
    └── ProcessadorDinheiro
```

### 📂 Localização
- [`dominio-vendas/pagamento/ProcessadorPagamento.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/pagamento/ProcessadorPagamento.java)
- [`dominio-vendas/pagamento/ProcessadorCartaoCredito.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/pagamento/ProcessadorCartaoCredito.java)
- [`dominio-vendas/pagamento/ProcessadorPix.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/pagamento/ProcessadorPix.java)
- [`dominio-vendas/pagamento/ProcessadorDinheiro.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/pagamento/ProcessadorDinheiro.java)

### 💻 Exemplo de Uso

```java
// Criar processador específico
ProcessadorPagamento processador = new ProcessadorPix();

// O método processar() executa TODO o fluxo
ResultadoProcessamento resultado = processador.processar(
    pagamento,
    new BigDecimal("50.00")
);

if (resultado.isSucesso()) {
    System.out.println("Autorizado: " + resultado.getCodigoAutorizacao());
}

// Fluxo executado automaticamente:
// 1. ✅ Dados validados
// 2. ✅ Limites verificados
// 3. ⚡ Processado com API PIX (específico)
// 4. ✅ Transação confirmada
// 5. 📄 Comprovante gerado
```

### ✅ Benefícios
- ✅ Reutilização de código (estrutura comum)
- ✅ Controle do fluxo na classe base
- ✅ Fácil adicionar novos tipos de pagamento
- ✅ Garante que passos obrigatórios sempre são executados

---

## 4. DECORATOR - Validadores de Ingresso

### 📝 Descrição
Permite **adicionar responsabilidades** a objetos dinamicamente, criando camadas de validação que podem ser compostas.

### 🎯 Problema Resolvido
Validar um ingresso envolve múltiplas verificações:
- Status do ingresso (ATIVO?)
- QR Code válido?
- Horário adequado?
- Não foi validado antes? (anti-fraude)

Cada validação é opcional e pode ser combinada de diferentes formas.

### 🏗️ Estrutura

```
ValidadorIngresso (Component interface)
    ├── ValidadorIngressoBase (ConcreteComponent)
    └── ValidadorIngressoDecorator (Decorator abstrato)
         ├── ValidadorQRCode (ConcreteDecorator)
         ├── ValidadorHorario (ConcreteDecorator)
         └── ValidadorDuplicidade (ConcreteDecorator)
```

### 📂 Localização
- [`dominio-vendas/validacao/ValidadorIngresso.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/validacao/ValidadorIngresso.java)
- [`dominio-vendas/validacao/ValidadorIngressoBase.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/validacao/ValidadorIngressoBase.java)
- [`dominio-vendas/validacao/ValidadorIngressoDecorator.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/validacao/ValidadorIngressoDecorator.java)
- [`dominio-vendas/validacao/ValidadorQRCode.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/validacao/ValidadorQRCode.java)
- [`dominio-vendas/validacao/ValidadorHorario.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/validacao/ValidadorHorario.java)
- [`dominio-vendas/validacao/ValidadorDuplicidade.java`](dominio-vendas/src/main/java/com/astra/cinema/dominio/validacao/ValidadorDuplicidade.java)

### 💻 Exemplo de Uso

```java
// Validação simples
ValidadorIngresso validador = new ValidadorIngressoBase();

// Adicionar camada de validação de QR Code
validador = new ValidadorQRCode(validador);

// Adicionar camada de validação de horário
validador = new ValidadorHorario(validador, sessaoRepo, 30, 15);

// Adicionar camada anti-fraude
validador = new ValidadorDuplicidade(validador);

// Executa TODAS as validações em cadeia
ResultadoValidacao resultado = validador.validar(ingresso);

// Ordem de execução:
// 1. Validação base (status ATIVO)
// 2. Validação QR Code
// 3. Validação horário
// 4. Validação duplicidade
```

### ✅ Benefícios
- ✅ Composição flexível de validações
- ✅ Adicionar/remover validações dinamicamente
- ✅ Cada validador tem uma responsabilidade única (SRP)
- ✅ Fácil criar novas validações

---

## 5. ITERATOR - Percorrer Assentos

### 📝 Descrição
Fornece uma maneira de **acessar elementos de uma coleção** sequencialmente sem expor sua representação interna.

### 🎯 Problema Resolvido
Precisamos percorrer assentos de uma sessão de diferentes formas:
- Todos os assentos
- Apenas disponíveis
- Apenas ocupados
- Calcular percentual de ocupação

Sem Iterator, teríamos código duplicado com lógica de filtragem espalhada.

### 🏗️ Estrutura

```
Iterable<Entry<AssentoId, Boolean>>
    ↑
ColecaoAssentos (Aggregate)
    └── cria → AssentoIterator (ConcreteIterator)
         ├── FiltroAssento.TODOS
         ├── FiltroAssento.DISPONIVEIS
         └── FiltroAssento.OCUPADOS
```

### 📂 Localização
- [`dominio-sessoes/sessao/AssentoIterator.java`](dominio-sessoes/src/main/java/com/astra/cinema/dominio/sessao/AssentoIterator.java)
- [`dominio-sessoes/sessao/ColecaoAssentos.java`](dominio-sessoes/src/main/java/com/astra/cinema/dominio/sessao/ColecaoAssentos.java)

### 💻 Exemplo de Uso

```java
Map<AssentoId, Boolean> mapaAssentos = sessao.getAssentos();
ColecaoAssentos colecao = new ColecaoAssentos(mapaAssentos);

// Percorrer apenas assentos disponíveis
for (Map.Entry<AssentoId, Boolean> assento : colecao.disponiveis()) {
    System.out.println("Disponível: " + assento.getKey());
}

// Percorrer apenas assentos ocupados
for (Map.Entry<AssentoId, Boolean> assento : colecao.ocupados()) {
    System.out.println("Ocupado: " + assento.getKey());
}

// Usar métodos de conveniência
int disponiveis = colecao.contarDisponiveis();
int ocupados = colecao.contarOcupados();
double percentual = colecao.percentualOcupacao();

System.out.println("Ocupação: " + percentual + "%");
```

### ✅ Benefícios
- ✅ Encapsula lógica de iteração
- ✅ Múltiplas formas de percorrer a mesma coleção
- ✅ Interface familiar (Iterable do Java)
- ✅ Código limpo com enhanced for

---

## 📊 Resumo dos Padrões

| Padrão | Categoria | Uso no Sistema | Localização |
|--------|-----------|----------------|-------------|
| **Strategy** | Comportamental | Cálculo de preço de ingressos | `dominio-vendas/compra` |
| **Observer** | Comportamental | Sistema de notificações de eventos | `dominio-compartilhado/eventos` + `dominio-vendas/eventos` |
| **Template Method** | Comportamental | Processamento de diferentes tipos de pagamento | `dominio-vendas/pagamento` |
| **Decorator** | Estrutural | Validação de ingressos com camadas | `dominio-vendas/validacao` |
| **Iterator** | Comportamental | Percorrer assentos com filtros | `dominio-sessoes/sessao` |

---

## 🎓 Conceitos Aplicados

### Princípios SOLID Seguidos

1. **Single Responsibility Principle (SRP)**
   - Cada validador (Decorator) tem UMA responsabilidade
   - Cada estratégia de preço encapsula UM algoritmo

2. **Open/Closed Principle (OCP)**
   - Fácil adicionar novos Observers sem modificar PublicadorEventos
   - Fácil adicionar novas Strategies sem modificar CalculadoraPreco

3. **Liskov Substitution Principle (LSP)**
   - Qualquer ProcessadorPagamento pode substituir outro
   - Decorators podem ser compostos livremente

4. **Interface Segregation Principle (ISP)**
   - Interfaces focadas (ValidadorIngresso, CalculadoraPreco)

5. **Dependency Inversion Principle (DIP)**
   - Código depende de abstrações (interfaces), não implementações

### Padrões GoF (Gang of Four)

Todos os 5 padrões implementados são padrões clássicos do livro "Design Patterns: Elements of Reusable Object-Oriented Software" (GoF, 1994).

---

## 🚀 Como Usar os Padrões

### 1. Adicionar Nova Estratégia de Preço

```java
public class PrecoIdoso implements CalculadoraPreco {
    @Override
    public BigDecimal calcular(BigDecimal precoBase) {
        return precoBase.multiply(new BigDecimal("0.60")); // 40% desconto
    }

    @Override
    public String getNomeEstrategia() {
        return "Idoso";
    }
}
```

### 2. Adicionar Novo Observador

```java
public class NotificadorSMS implements ObservadorEvento<CompraConfirmadaEvento> {
    @Override
    public void atualizar(CompraConfirmadaEvento evento) {
        // Enviar SMS
    }

    @Override
    public Class<CompraConfirmadaEvento> getTipoEvento() {
        return CompraConfirmadaEvento.class;
    }
}

// Registrar
PublicadorEventos.getInstancia().registrar(new NotificadorSMS());
```

### 3. Adicionar Novo Processador de Pagamento

```java
public class ProcessadorBoleto extends ProcessadorPagamento {
    @Override
    protected String processarComGateway(Pagamento pag, BigDecimal valor) {
        // Gerar boleto
        return "BOLETO-" + gerarCodigoBarras();
    }

    @Override
    public String getNome() {
        return "Boleto Bancário";
    }
}
```

---

## ✅ Requisitos da 2ª Entrega - ATENDIDOS

- ✅ **Implementados 5 padrões** (exigido: mínimo 4)
  1. Strategy
  2. Observer
  3. Template Method
  4. Decorator
  5. Iterator

- ✅ **Camada de persistência** com JPA/Hibernate
- ✅ **Camada de apresentação web** (REST API + React)
- ✅ **DDD completo** com bounded contexts
- ✅ **Documentação** de arquitetura e padrões

---

**Desenvolvido para o Projeto Astra Cinema - 2ª Entrega**

# Documentação do Sistema Astra Cinema

## Sumário

1. [Visão Geral](#visão-geral)
2. [Tipos de Usuários](#tipos-de-usuários)
3. [Funcionalidades por Perfil](#funcionalidades-por-perfil)
   - [Cliente](#cliente)
   - [Funcionário](#funcionário)
   - [Gerente/Administrador](#gerenteadministrador)
4. [Fluxos Principais](#fluxos-principais)
5. [Arquitetura do Sistema](#arquitetura-do-sistema)

---

## Visão Geral

O **Astra Cinema** é um sistema completo de gerenciamento de cinema que permite a venda de ingressos online, controle de sessões, gerenciamento de produtos da bomboniere e geração de relatórios gerenciais.

### Tecnologias Utilizadas

- **Backend**: Java 17, Spring Boot 3.5.7
- **Frontend**: React.js
- **Banco de Dados**: PostgreSQL 16
- **Containerização**: Docker & Docker Compose
- **Migração de Schema**: Flyway

### Arquitetura

O sistema segue os princípios de **Domain-Driven Design (DDD)** e **Clean Architecture**, organizados em camadas:

- **Domínio**: Lógica de negócio pura
- **Aplicação**: Casos de uso e serviços
- **Infraestrutura**: Persistência e integrações
- **Apresentação**: Controllers REST e interface React

---

## Tipos de Usuários

O sistema possui três perfis de usuários, cada um com permissões e funcionalidades específicas:

### 1. Cliente

Usuário final que utiliza o sistema para comprar ingressos e produtos.

**Características:**
- Cadastro próprio via tela de registro
- Acesso à programação de filmes
- Compra de ingressos online
- Visualização de ingressos adquiridos

### 2. Funcionário

Colaborador do cinema responsável pela operação diária.

**Características:**
- Validação de ingressos na entrada
- Remarcação de ingressos
- Consulta de histórico de validações
- Acesso limitado a funcionalidades operacionais

### 3. Gerente/Administrador

Responsável pela gestão completa do cinema.

**Características:**
- Todas as permissões de funcionário
- Gerenciamento de filmes e sessões
- Gerenciamento de salas
- Gerenciamento de produtos (bomboniere)
- Criação de programações semanais
- Acesso a relatórios gerenciais
- Gerenciamento de funcionários

---

## Funcionalidades por Perfil

### Cliente

#### 1. Cadastro e Autenticação

**Registro de Nova Conta**
- Nome completo
- Email (único no sistema)
- CPF
- Senha segura

![Tela de Registro](screenshots/cliente-registro.png)

**Login**
- Autenticação via email e senha
- Token JWT para sessão
- Redirecionamento para dashboard do cliente

![Tela de Login](screenshots/cliente-login.png)

---

#### 2. Navegação e Programação

**Visualização de Filmes em Cartaz**
- Lista de todos os filmes disponíveis
- Informações detalhadas:
  - Título
  - Classificação indicativa
  - Duração
  - Gênero
  - Sinopse
  - Imagem do pôster

![Programação de Filmes](screenshots/cliente-programacao.png)

**Visualização de Sessões**
- Horários disponíveis por filme
- Informação de sala
- Preço do ingresso
- Assentos disponíveis

![Sessões Disponíveis](screenshots/cliente-sessoes.png)

---

#### 3. Compra de Ingressos

**Fluxo de Compra:**

1. **Seleção de Sessão**
   - Escolha do filme
   - Escolha do horário
   - Visualização de disponibilidade

![Seleção de Sessão](screenshots/cliente-selecao-sessao.png)

2. **Escolha de Assentos**
   - Mapa visual da sala
   - Assentos disponíveis e ocupados
   - Seleção de múltiplos assentos
   - Cálculo automático do valor total

![Seleção de Assentos](screenshots/cliente-selecao-assentos.png)

3. **Resumo da Compra**
   - Filme e horário selecionado
   - Assentos escolhidos
   - Valor total
   - Confirmação de dados

![Resumo da Compra](screenshots/cliente-resumo-compra.png)

4. **Pagamento**
   - Escolha do método:
     - **PIX**: Geração de QR Code
     - **Cartão de Crédito ou Débito**: Dados do cartão
   - Confirmação de pagamento

![Tela de Pagamento](screenshots/cliente-pagamento.png)

5. **Confirmação**
   - Ingresso gerado com QR Code único
   - Email de confirmação (simulado)
   - Opção de download/impressão

![Confirmação de Compra](screenshots/cliente-confirmacao.png)

---

#### 4. Meus Ingressos

**Visualização de Ingressos Adquiridos**
- Lista de todos os ingressos do cliente
- Status do ingresso:
  - ✅ Validado
  - ⏳ Ativo
  - ❌ Cancelado
  - 🕐 Expirado
- QR Code para validação
- Informações da sessão

![Meus Ingressos](screenshots/cliente-meus-ingressos.png)

**Detalhes do Ingresso**
- QR Code em tamanho grande
- Filme, sala, horário
- Assento
- Status de validação
- Opção de remarcação (se disponível)

![Detalhes do Ingresso](screenshots/cliente-detalhe-ingresso.png)

---

### Funcionário

#### 1. Dashboard Operacional

**Visão Geral**
- Estatísticas do dia
- Atalhos rápidos para funções principais
- Lista de sessões ativas

![Dashboard Funcionário](screenshots/funcionario-dashboard.png)

---

#### 2. Validação de Ingressos

**Fluxo de Validação:**

1. **Leitura de QR Code**
   - Scanner via câmera ou upload de imagem
   - Leitura do código do ingresso
   - Validação automática

![Validação de Ingresso](screenshots/funcionario-validacao.png)

2. **Resultado da Validação**
   - ✅ **Sucesso**: Ingresso válido e autorizado
   - ❌ **Falha**: Mensagens de erro específicas:
     - Ingresso já validado
     - Sessão inválida
     - Assento indisponível
     - Ingresso expirado

![Resultado Validação Sucesso](screenshots/funcionario-validacao-sucesso.png)
![Resultado Validação Erro](screenshots/funcionario-validacao-erro.png)

3. **Histórico de Validações**
   - Lista de todas as validações realizadas
   - Filtros por data, sessão, filme
   - Informações do cliente
   - Horário da validação

![Histórico de Validações](screenshots/funcionario-historico-validacoes.png)

---

#### 3. Remarcação de Ingressos

**Processo de Remarcação:**

1. **Busca de Ingresso**
   - Por código do ingresso
   - Por CPF do cliente
   - Por sessão

![Busca Ingresso Remarcação](screenshots/funcionario-busca-remarcacao.png)

2. **Seleção de Nova Sessão**
   - Lista de sessões disponíveis do mesmo filme
   - Verificação de assentos disponíveis
   - Confirmação de alteração

![Seleção Nova Sessão](screenshots/funcionario-nova-sessao.png)

3. **Confirmação**
   - Ingresso atualizado
   - Novo QR Code gerado
   - Registro no histórico

![Confirmação Remarcação](screenshots/funcionario-confirmacao-remarcacao.png)

---

### Gerente/Administrador

#### 1. Dashboard Gerencial

**Visão Geral Completa**
- KPIs principais
- Gráficos de desempenho
- Acesso rápido a todas as funcionalidades
- Alertas e notificações

![Dashboard Admin](screenshots/admin-dashboard.png)

---

#### 2. Gerenciamento de Filmes

**Listagem de Filmes**
- Todos os filmes cadastrados
- Busca e filtros
- Status (Em cartaz, Em breve, Removido)

![Lista de Filmes](screenshots/admin-lista-filmes.png)

**Adicionar Novo Filme**
- Título
- Classificação indicativa
- Duração (minutos)
- Gênero
- Sinopse
- URL da imagem
- Data de lançamento

![Adicionar Filme](screenshots/admin-adicionar-filme.png)

**Editar Filme**
- Atualização de informações
- Verificação de sessões vinculadas
- Controle de status

![Editar Filme](screenshots/admin-editar-filme.png)

**Remover Filme**
- Validação de sessões ativas
- Confirmação de exclusão
- Impacto em programações

![Remover Filme](screenshots/admin-remover-filme.png)

---

#### 3. Gerenciamento de Sessões

**Listagem de Sessões**
- Todas as sessões (passadas, presentes, futuras)
- Filtros por filme, sala, data
- Indicadores de ocupação
- Status da sessão

![Lista de Sessões](screenshots/admin-lista-sessoes.png)

**Criar Nova Sessão**
- Seleção de filme
- Seleção de sala
- Data e horário
- Preço do ingresso
- Validação de conflitos de sala

![Criar Sessão](screenshots/admin-criar-sessao.png)

**Editar Sessão**
- Alteração de horário (se não houver ingressos vendidos)
- Alteração de preço
- Alteração de status
- Remarcação automática de ingressos (se necessário)

![Editar Sessão](screenshots/admin-editar-sessao.png)

**Cancelar Sessão**
- Notificação de clientes afetados
- Reembolso automático
- Registro no histórico

![Cancelar Sessão](screenshots/admin-cancelar-sessao.png)

---

#### 4. Gerenciamento de Salas

**Listagem de Salas**
- Nome da sala
- Capacidade total
- Status (Ativa, Manutenção)
- Sessões agendadas

![Lista de Salas](screenshots/admin-lista-salas.png)

**Adicionar/Editar Sala**
- Nome/Número da sala
- Capacidade de assentos
- Configuração de layout
- Recursos especiais (3D, IMAX, etc.)

![Gerenciar Sala](screenshots/admin-gerenciar-sala.png)

---

#### 5. Gerenciamento de Programações

**Criar Programação Semanal**
- Seleção de período (início e fim)
- Seleção de sessões a incluir
- Validação de permissões (apenas gerentes)
- Validação de sessões disponíveis

![Criar Programação](screenshots/admin-criar-programacao.png)

**Visualizar Programações**
- Programações ativas e passadas
- Sessões incluídas
- Período de vigência

![Visualizar Programações](screenshots/admin-visualizar-programacoes.png)

---

#### 6. Gerenciamento de Produtos (Bomboniere)

**Listagem de Produtos**
- Nome e descrição
- Preço
- Estoque atual
- Status (Disponível, Esgotado)

![Lista de Produtos](screenshots/admin-lista-produtos.png)

**Adicionar Produto**
- Nome
- Descrição
- Preço
- Estoque inicial
- Categoria

![Adicionar Produto](screenshots/admin-adicionar-produto.png)

**Controle de Estoque**
- Entrada de estoque
- Ajuste de estoque
- Histórico de movimentações

![Controle de Estoque](screenshots/admin-controle-estoque.png)

**Editar/Remover Produto**
- Atualização de informações
- Alteração de preço
- Desativação/Exclusão

![Editar Produto](screenshots/admin-editar-produto.png)

---

#### 7. Gerenciamento de Funcionários

**Listagem de Funcionários**
- Nome e email
- Cargo (Funcionário, Gerente)
- Status (Ativo, Inativo)
- Data de cadastro

![Lista de Funcionários](screenshots/admin-lista-funcionarios.png)

**Adicionar Funcionário**
- Nome completo
- Email
- Senha inicial
- Cargo (definir permissões)

![Adicionar Funcionário](screenshots/admin-adicionar-funcionario.png)

**Editar Funcionário**
- Alteração de cargo
- Atualização de dados
- Alteração de status

![Editar Funcionário](screenshots/admin-editar-funcionario.png)

---

#### 8. Relatórios Gerenciais

**Relatório de Vendas**
- Período customizável
- Total de ingressos vendidos
- Receita total
- Receita por filme
- Receita por sessão
- Gráficos de evolução

![Relatório de Vendas](screenshots/admin-relatorio-vendas.png)

**Relatório de Filmes Populares**
- Ranking de filmes
- Quantidade de ingressos vendidos
- Receita gerada
- Taxa de ocupação média

![Filmes Populares](screenshots/admin-filmes-populares.png)

**Relatório de Ocupação de Salas**
- Taxa de ocupação por sala
- Sessões com maior/menor público
- Horários de pico
- Análise de capacidade

![Ocupação de Salas](screenshots/admin-ocupacao-salas.png)

**Relatório de Remarcações**
- Total de remarcações no período
- Sessões mais remarcadas
- Motivos de remarcação
- Impacto na ocupação

![Relatório Remarcações](screenshots/admin-relatorio-remarcacoes.png)

**Analytics da Bomboniere**
- Produtos mais vendidos
- Receita de produtos
- Estoque crítico
- Análise de margem

![Analytics Bomboniere](screenshots/admin-analytics-bomboniere.png)

---

## Fluxos Principais

### Fluxo 1: Compra de Ingresso (Cliente)

```
1. Cliente acessa o sistema
2. Visualiza filmes em cartaz
3. Seleciona filme desejado
4. Escolhe sessão (data/horário)
5. Seleciona assento(s) no mapa da sala
6. Revisa resumo da compra
7. Escolhe método de pagamento (PIX ou Cartão)
8. Confirma pagamento
9. Recebe ingresso com QR Code
10. Ingresso aparece em "Meus Ingressos"
```

**Diagrama do Fluxo:**

![Fluxo Compra Ingresso](screenshots/fluxo-compra-ingresso.png)

---

### Fluxo 2: Validação de Ingresso (Funcionário)

```
1. Cliente apresenta ingresso (QR Code)
2. Funcionário acessa tela de validação
3. Escaneia QR Code do ingresso
4. Sistema valida:
   - Ingresso existe
   - Não foi validado anteriormente
   - Sessão está ativa
   - Assento está vinculado corretamente
5. Sistema registra validação
6. Sistema atualiza status do ingresso
7. Funcionário autoriza entrada
8. Validação registrada no histórico
```

**Diagrama do Fluxo:**

![Fluxo Validação Ingresso](screenshots/fluxo-validacao-ingresso.png)

---

### Fluxo 3: Criação de Sessão (Gerente)

```
1. Gerente acessa gerenciamento de sessões
2. Clica em "Criar Nova Sessão"
3. Seleciona filme
4. Seleciona sala
5. Define data e horário
6. Define preço do ingresso
7. Sistema valida:
   - Sala disponível no horário
   - Filme existe e está ativo
   - Horário não conflita com outras sessões
8. Sistema cria sessão
9. Assentos são inicializados como disponíveis
10. Sessão aparece na programação
```

**Diagrama do Fluxo:**

![Fluxo Criação Sessão](screenshots/fluxo-criacao-sessao.png)

---

### Fluxo 4: Remarcação de Ingresso (Funcionário)

```
1. Cliente solicita remarcação
2. Funcionário busca ingresso (código ou CPF)
3. Sistema valida:
   - Ingresso existe
   - Não foi validado
   - Sessão original ainda não ocorreu
4. Funcionário seleciona nova sessão
5. Sistema verifica disponibilidade de assento
6. Sistema cria registro de remarcação
7. Sistema atualiza ingresso com nova sessão
8. Sistema gera novo QR Code
9. Cliente recebe ingresso atualizado
10. Remarcação registrada no histórico
```

**Diagrama do Fluxo:**

![Fluxo Remarcação](screenshots/fluxo-remarcacao.png)

---

## Arquitetura do Sistema

### Arquitetura de Camadas (DDD + Clean Architecture)

```
┌─────────────────────────────────────────┐
│         Apresentação (REST API)         │
│      Controllers + React Frontend       │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│      Aplicação (Use Cases/Services)     │
│   Orquestração da Lógica de Negócio    │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│        Domínio (Entidades/VOs)          │
│      Lógica de Negócio Pura             │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│    Infraestrutura (Repositórios/DB)     │
│   Persistência + Integrações Externas   │
└─────────────────────────────────────────┘
```

**Diagrama da Arquitetura:**

![Arquitetura em Camadas](screenshots/arquitetura-camadas.png)

---

### Módulos do Sistema

O sistema está organizado em módulos Maven seguindo DDD:

#### Domínio
- **dominio-compartilhado**: Classes comuns (eventos, value objects, exceções)
- **dominio-usuarios**: Entidades e regras de usuários, clientes e funcionários
- **dominio-sessoes**: Filmes, sessões, salas e programações
- **dominio-vendas**: Compras, ingressos, pagamentos e validações
- **dominio-bomboniere**: Produtos e vendas de produtos

#### Aplicação
- **aplicacao**: Use cases e services para orquestração

#### Infraestrutura
- **infraestrutura**: Implementações de repositórios (JPA), configurações, migrations (Flyway)

#### Apresentação
- **apresentacao-backend**: Controllers REST Spring Boot
- **apresentacao-frontend**: Interface React.js

**Diagrama de Módulos:**

![Módulos DDD](screenshots/modulos-ddd.png)

---

### Padrões de Projeto Implementados

O sistema implementa 4 padrões GoF (Gang of Four):

1. **Iterator**: Iteração sobre assentos de uma sessão
2. **Decorator**: Cadeia de validadores de ingressos
3. **Observer**: Sistema de notificação de eventos de compra
4. **Template Method**: Processamento polimórfico de pagamentos

Documentação detalhada em: [`padroes.md`](../../padroes.md)

---

### Banco de Dados

**Modelo Relacional:**

Principais tabelas:
- `usuarios`: Usuários do sistema
- `clientes`: Dados específicos de clientes
- `funcionarios`: Dados de funcionários
- `filmes`: Catálogo de filmes
- `salas`: Salas de cinema
- `sessoes`: Sessões de exibição
- `compras`: Compras de ingressos
- `ingressos`: Ingressos individuais
- `pagamentos`: Registros de pagamento
- `produtos`: Produtos da bomboniere
- `validacoes_ingresso`: Histórico de validações
- `remarcacoes_sessao`: Histórico de remarcações

**Diagrama ER:**

![Diagrama ER](screenshots/diagrama-er.png)

---

### API REST

**Principais Endpoints:**

#### Autenticação
```
POST /api/auth/login          - Login de usuário
POST /api/auth/register       - Registro de cliente
```

#### Filmes
```
GET    /api/filmes            - Lista todos os filmes
GET    /api/filmes/{id}       - Detalhes de um filme
POST   /api/filmes            - Adicionar filme (admin)
PUT    /api/filmes/{id}       - Atualizar filme (admin)
DELETE /api/filmes/{id}       - Remover filme (admin)
```

#### Sessões
```
GET    /api/sessoes           - Lista todas as sessões
GET    /api/sessoes/{id}      - Detalhes de uma sessão
POST   /api/sessoes           - Criar sessão (admin)
PUT    /api/sessoes/{id}      - Atualizar sessão (admin)
DELETE /api/sessoes/{id}      - Cancelar sessão (admin)
```

#### Compras
```
POST   /api/compras/iniciar   - Iniciar compra
POST   /api/compras/confirmar - Confirmar compra
DELETE /api/compras/{id}      - Cancelar compra
GET    /api/compras/cliente   - Ingressos do cliente
```

#### Validação (Funcionário)
```
POST   /api/validacao/validar - Validar ingresso
GET    /api/validacao/historico - Histórico de validações
```

#### Remarcação (Funcionário)
```
POST   /api/remarcacao/{id}   - Remarcar ingresso
GET    /api/remarcacao/historico - Histórico de remarcações
```

#### Relatórios (Admin)
```
GET    /api/relatorios/vendas         - Relatório de vendas
GET    /api/relatorios/filmes         - Filmes populares
GET    /api/relatorios/ocupacao       - Ocupação de salas
GET    /api/relatorios/remarcacoes    - Remarcações
GET    /api/relatorios/analytics      - Analytics bomboniere
```

---

## Tecnologias e Ferramentas

### Backend
- **Java 17**: Linguagem principal
- **Spring Boot 3.5.7**: Framework web
- **Spring Data JPA**: Persistência
- **Hibernate 6.6**: ORM
- **Flyway**: Migração de banco
- **PostgreSQL 16**: Banco de dados
- **Maven**: Gerenciamento de dependências

### Frontend
- **React.js 18**: Framework UI
- **React Router**: Navegação SPA
- **Axios**: Cliente HTTP
- **CSS3**: Estilização

### DevOps
- **Docker**: Containerização
- **Docker Compose**: Orquestração de containers
- **Git**: Controle de versão

---

## Executando o Sistema

### Pré-requisitos
- Docker e Docker Compose instalados
- Portas 8080 (backend) e 5432 (postgres) disponíveis

### Iniciar Aplicação

```bash
# Clonar repositório
git clone [URL_DO_REPOSITORIO]
cd Astra

# Construir e iniciar containers
docker-compose up -d

# Verificar logs
docker-compose logs -f astra-app
```

### Acessar Sistema

- **Frontend**: http://localhost:8080
- **Backend API**: http://localhost:8080/api

### Credenciais Padrão

Sistema já vem com dados iniciais (via `data.sql`):

**Gerente:**
- Email: `gerente@astra.com`
- Senha: `123456`

**Funcionário:**
- Email: `funcionario@astra.com`
- Senha: `123456`

**Cliente de Teste:**
- Email: `cliente@test.com`
- Senha: `123456`

---

## Conclusão

O sistema **Astra Cinema** é uma solução completa e robusta para gerenciamento de cinemas, implementando boas práticas de engenharia de software como DDD, Clean Architecture, e padrões de projeto GoF. O sistema oferece uma experiência completa tanto para clientes quanto para operadores e gestores do cinema.

---

**Última atualização**: 13 de dezembro de 2025  
**Versão**: 1.0

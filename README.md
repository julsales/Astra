# Astra Cinema - Sistema de Gerenciamento de Cinemas

Sistema completo de gerenciamento de cinemas desenvolvido com arquitetura em camadas (DDD) utilizando Java Spring Boot no backend e React no frontend.

## Funcionalidades

- **Gerenciamento de Filmes**: Cadastro, edição e remoção de filmes
- **Gerenciamento de Sessões**: Criação e modificação de sessões com validação de conflitos
- **Venda de Ingressos**: Sistema de compra com seleção de assentos
- **Bomboniere (PDV)**: Ponto de venda para produtos de bomboniere
- **Validação de Ingressos**: Sistema de validação para funcionários
- **Remarcação**: Sistema de remarcação de ingressos
- **Relatórios**: Dashboards e relatórios analíticos

## Tecnologias Utilizadas

### Backend
- Java 17
- Spring Boot 3.x
- JPA/Hibernate
- H2 Database (desenvolvimento)
- Maven

### Frontend
- React 18
- Lucide React (ícones)
- CSS3

## Pré-requisitos

- Java 17+
- Node.js 16+
- Maven 3.8+

## Instalação e Execução

### Backend

```bash
cd apresentacao-backend
./mvnw spring-boot:run
```

O backend estará disponível em `http://localhost:8080`

### Frontend

```bash
cd apresentacao-frontend/src/main/react
npm install
npm start
```

O frontend estará disponível em `http://localhost:3000`

## Contas Demo

### Administrador
- **Usuário**: Acesse via interface e selecione perfil "Administrador"
- **Funcionalidades**: Gerenciamento completo de filmes, sessões, produtos, usuários e relatórios

### Funcionário
- **Usuário**: Acesse via interface e selecione perfil "Funcionário"
- **Funcionalidades**: Validação de ingressos, PDV de bomboniere, remarcação de ingressos e relatórios operacionais

### Cliente
- **Usuário**: Acesse via interface e selecione perfil "Cliente"
- **Funcionalidades**: Compra de ingressos, visualização de histórico e remarcação

## Estrutura do Projeto

```
Astra/
├── apresentacao-backend/     # API REST Spring Boot
├── apresentacao-frontend/    # Interface React
├── aplicacao/               # Casos de uso
├── dominio-*/              # Modelos de domínio (DDD)
├── infraestrutura/         # Persistência e infraestrutura
└── docs/                   # Documentação
```

## Arquitetura

O projeto segue os princípios de Domain-Driven Design (DDD) com separação clara entre:

- **Camada de Apresentação**: Controllers REST e Interface React
- **Camada de Aplicação**: Casos de uso e serviços
- **Camada de Domínio**: Entidades, value objects e regras de negócio
- **Camada de Infraestrutura**: Repositórios JPA e configurações

## Padrões de Projeto Utilizados

- Repository Pattern
- Service Layer
- DTO (Data Transfer Object)
- Factory Pattern
- Strategy Pattern
- Command Pattern
- Template Method

## Licença

Este projeto está sob a licença especificada no arquivo LICENSE.

## Contato

Para mais informações sobre o projeto, consulte a documentação na pasta `docs/`.

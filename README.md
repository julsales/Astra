<<<<<<< HEAD
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
=======

<p align="center">
  <img src="https://img.shields.io/badge/Status-Em%20desenvolvimento-green?style=for-the-badge&logo=github" alt="Status" />
  <img src="https://img.shields.io/github/repo-size/julsales/Astra?style=for-the-badge&logo=github" alt="Repository Size" />
  <img src="https://img.shields.io/github/languages/count/julsales/Astra?style=for-the-badge&logo=python" alt="Language Count" />
  <img src="https://img.shields.io/github/commit-activity/t/julsales/Astra?style=for-the-badge&logo=github" alt="Commit Activity" />
  <a href="LICENSE.md"><img src="https://img.shields.io/github/license/julsales/Astra?style=for-the-badge" alt="License" /></a>
</p>


<p align="center">
<img width="1248" height="632" alt="Gemini_Generated_Image_4xspzd4xspzd4xsp" src="https://github.com/user-attachments/assets/c6a60c39-a045-45c1-bb8b-72143a9b2227" />
</p>

## 📝 Descrição

O Astra é um sistema de gestão completo para cinemas, integrando a compra de ingressos, controle de sessões, vendas de bomboniere e administração de usuários em uma única plataforma.

## 🗺️ Mapa de histórias

![Screenshot do Astra](entreg%C3%A1veis/mapa%20de%20hist%C3%B3rias/Astra%20-%20Mapa%20de%20hist%C3%B3rias.jpeg)

## ✨ Features

* Gerenciar filmes, sessões, funcionários e bomboniere
* Ver sessão, comprar ingresso, comprar na bomboniere, verificar ingresso
* Operar a bomboniere, validar ingresso
* Ver relatórios

## �️ Como rodar localmente

1. Compile todos os módulos Maven (frontend incluso):
  ```bash
  mvn -q -DskipTests package
  ```
2. Suba Postgres + backend com Docker Compose (usa as variáveis `DATABASE_*` já configuradas):
  ```bash
  docker-compose up --build
  ```
3. A aplicação backend sobe em `http://localhost:8080` servindo também os assets do módulo `apresentacao-frontend`.

## ✅ Testes BDD

Os cenários Cucumber foram movidos para o módulo `astra-dominio` para espelhar o SGB. Execute-os com:

```bash
mvn -q -pl dominio test
```

Isso roda `RunCucumberTest`, cobrindo fluxos de compra, sessão, bomboniere, pagamento, programação e gerenciamento de usuários.

## �🔗 Links dos artefatos/entregáveis

* [Descrição do Domínio](https://docs.google.com/document/d/1_o6GAWY7OvhhR_YJnx4K9i8jbMJ_n436_AnnscwNL5o/edit?tab=t.0)
* [Mapa de histórias](https://docs.google.com/spreadsheets/d/1WRr6s1s3xA9KvXNEO1kmZ--eTIqEMnCFXpGGjTszHWc/edit?gid=1767904539#gid=1767904539)
* [Apresentação no canvas](https://www.canva.com/design/DAG2SMUE2Xo/xg1hjPB_XHOw6zBcu73L3Q/edit)
* [Cenários de testes BDD](https://docs.google.com/document/d/18jGI9RaSSxZy_uKOETACXw2tJWp0BffRo4_Bta-eQLE/edit?tab=t.0#heading=h.35kthn3hhsl8)
* [Protótipo de alta fidelidade](https://www.figma.com/make/IaFR0A4iC6hLt7yz3viT0T/Astra---Prot%C3%B3tipo?node-id=0-1&p=f&t=PQeHTOn1hViBS5n6-0)
* [Arquivo CML](entreg%C3%A1veis/cml/Astra.cml)
  
<br>

## 👥 Nossa Equipe

<div align="center">
  <a href="https://github.com/julsales/Astra/graphs/contributors">
    <img src="https://contrib.rocks/image?repo=julsales/Astra" style="width: 350px; height: auto;" />
  </a>
</div>
>>>>>>> c8d8fa7962ed4c3b8103919879ee7c1130517026

# Astra Cinemas 🎬⭐

Sistema de gerenciamento de cinema desenvolvido com Spring Boot e React.

## 🚀 Tecnologias

### Backend
- Java 17
- Spring Boot 3.1.4
- **Spring Data JPA** (ORM)
- **Hibernate** (Implementação JPA)
- H2 Database (desenvolvimento)
- Maven

### Frontend
- React 19.2.0
- CSS3 com animações
- Design responsivo

### Arquitetura
- **ORM:** JPA/Hibernate para mapeamento objeto-relacional
- **Padrão:** Repository Pattern + Service Layer
- **Herança:** Single Table Inheritance para hierarquia de usuários

## 📋 Pré-requisitos

- Java 17 ou superior
- Node.js 18 ou superior
- Maven 3.6 ou superior

## 🔧 Instalação e Execução

### Desenvolvimento

#### Backend (Spring Boot)
```bash
# Na raiz do projeto
./mvnw spring-boot:run
```
O backend estará disponível em: `http://localhost:8080`

#### Frontend (React)
```bash
# Entre na pasta frontend
cd frontend

# Instale as dependências
npm install

# Execute o servidor de desenvolvimento
npm start
```
O frontend estará disponível em: `http://localhost:3000`

### Produção

Para build completo (backend + frontend):
```bash
# Na raiz do projeto
./mvnw clean package

# Execute o JAR gerado
java -jar target/astra-0.0.1-SNAPSHOT.jar
```

O aplicativo completo estará disponível em: `http://localhost:8080`

## 🎨 Funcionalidades

### Tela de Login
- Login com email e senha
- Sistema focado em **clientes**
- Checkbox "Lembrar-me"
- Link para cadastro
- Design com tema espacial roxo/violeta

### Tela de Registro
- Cadastro completo de clientes
- Validação de formulário em tempo real
- Campos: Nome, Email, Senha, CPF, Telefone, Data de Nascimento
- Validação backend (email e CPF únicos)
- Design responsivo e consistente

## 📁 Estrutura do Projeto

```
Astra/
├── src/
│   ├── main/
│   │   ├── java/com/astra/
│   │   │   ├── AstraApplication.java
│   │   │   ├── controller/
│   │   │   │   └── AuthController.java
│   │   │   ├── service/
│   │   │   │   ├── UsuarioService.java
│   │   │   │   ├── ClienteService.java
│   │   │   │   └── FuncionarioService.java
│   │   │   ├── repository/
│   │   │   │   ├── UsuarioRepository.java
│   │   │   │   ├── ClienteRepository.java
│   │   │   │   ├── FuncionarioRepository.java
│   │   │   │   └── AdministradorRepository.java
│   │   │   ├── model/
│   │   │   │   ├── Usuario.java (Abstract)
│   │   │   │   ├── Cliente.java
│   │   │   │   ├── Funcionario.java
│   │   │   │   └── Administrador.java
│   │   │   └── dto/
│   │   │       ├── LoginRequest.java
│   │   │       └── LoginResponse.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql
│   └── test/
│       └── java/
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   │   ├── Login.js
│   │   │   ├── Login.css
│   │   │   └── Stars.js
│   │   ├── App.js
│   │   └── App.css
│   └── package.json
├── pom.xml
├── README.md
├── ARCHITECTURE_JPA.md
└── GITHUB_SETUP.md
```

## 🔐 Acesso ao Sistema

### Credenciais de Teste (Banco de Dados com JPA/Hibernate)

O sistema já vem com dados iniciais carregados via `data.sql`:

| Email | Senha |
|-------|-------|
| cliente@teste.com | 123456 |
| maria@teste.com | 123456 |
| pedro@teste.com | 123456 |

💡 **Ou crie sua própria conta** usando a tela de registro!

### H2 Console (Acesso ao Banco de Dados)
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:astradb`
- Username: `sa`
- Password: `password`

### Arquitetura de Dados
- **ORM:** JPA com Hibernate
- **Padrão:** Single Table Inheritance
- **Tabela:** usuarios (com discriminador tipo_usuario)

## 👥 Colaboradores

Este projeto está configurado para ser acessível ao usuário @profsauloaraujo no GitHub.

## 📝 Licença

Este projeto está sob a licença especificada no arquivo LICENSE.

## 🎯 Próximos Passos

- [ ] Implementar autenticação JWT
- [ ] Criar endpoints REST para usuários
- [ ] Implementar dashboard para cada tipo de usuário
- [ ] Adicionar gestão de filmes
- [ ] Implementar sistema de reservas
- [ ] Adicionar gestão de salas e sessões

---

Desenvolvido para o projeto Astra Cinemas 🌟

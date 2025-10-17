# Diagrama de Entidades - Astra Cinemas

## 📊 Modelo Conceitual

```
┌─────────────────────────────────────────────────┐
│                   Usuario                       │
│                  (Abstract)                     │
├─────────────────────────────────────────────────┤
│ - id: Long (PK)                                 │
│ - email: String (UNIQUE, NOT NULL)              │
│ - senha: String (NOT NULL)                      │
│ - nome: String (NOT NULL)                       │
│ - telefone: String                              │
│ - dataCadastro: LocalDateTime                   │
│ - ativo: Boolean                                │
└───────────────────┬─────────────────────────────┘
                    │
        ┌───────────┴───────────┬────────────────┐
        │                       │                │
        ▼                       ▼                ▼
┌───────────────┐     ┌──────────────┐  ┌──────────────────┐
│   Cliente     │     │ Funcionario  │  │ Administrador    │
├───────────────┤     ├──────────────┤  ├──────────────────┤
│ - cpf: String │     │ - matricula  │  │ - nivelAcesso    │
│ - dataNasc    │     │ - cargo      │  │ - departamento   │
│ - pontos      │     │ - setor      │  │                  │
│               │     │ - salario    │  │                  │
└───────────────┘     └──────────────┘  └──────────────────┘
```

## 🗂️ Tabela Física (Single Table)

```sql
CREATE TABLE usuarios (
    -- Campos Comuns
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    tipo_usuario        VARCHAR(255),     -- Discriminator
    email               VARCHAR(255) UNIQUE NOT NULL,
    senha               VARCHAR(255) NOT NULL,
    nome                VARCHAR(255) NOT NULL,
    telefone            VARCHAR(20),
    data_cadastro       TIMESTAMP,
    ativo               BOOLEAN DEFAULT TRUE,
    
    -- Campos de Cliente
    cpf                 VARCHAR(11) UNIQUE,
    data_nascimento     VARCHAR(10),
    pontos_fidelidade   INT DEFAULT 0,
    
    -- Campos de Funcionario
    matricula           VARCHAR(50) UNIQUE,
    cargo               VARCHAR(100),
    setor               VARCHAR(100),
    salario             DECIMAL(10,2),
    
    -- Campos de Administrador
    nivel_acesso        INT DEFAULT 3,
    departamento        VARCHAR(100)
);
```

## 🔄 Fluxo de Dados

### 1. Login Flow
```
┌──────────┐      ┌──────────────┐      ┌─────────────┐
│  React   │─────▶│ Controller   │─────▶│  Service    │
│ (Login)  │      │ AuthController│      │ UsuarioServ │
└──────────┘      └──────────────┘      └─────────────┘
     ▲                                          │
     │                                          ▼
     │              ┌──────────────┐      ┌─────────────┐
     └──────────────│  Response    │◀─────│ Repository  │
                    │   (JSON)     │      │UsuarioRepo  │
                    └──────────────┘      └─────────────┘
                                                │
                                                ▼
                                          ┌─────────────┐
                                          │ Hibernate   │
                                          │    ORM      │
                                          └─────────────┘
                                                │
                                                ▼
                                          ┌─────────────┐
                                          │  Database   │
                                          │     H2      │
                                          └─────────────┘
```

### 2. CRUD Operations
```
Service Layer          Repository Layer        Hibernate           Database
─────────────          ────────────────        ─────────          ────────

save(entity)    ─────▶ save()           ─────▶ INSERT      ─────▶ [Table]
findById()      ─────▶ findById()       ─────▶ SELECT      ─────▶ [Table]
findAll()       ─────▶ findAll()        ─────▶ SELECT      ─────▶ [Table]
delete()        ─────▶ deleteById()     ─────▶ DELETE      ─────▶ [Table]
```

## 🏗️ Camadas da Aplicação

```
┌─────────────────────────────────────────────────────────┐
│              PRESENTATION LAYER                         │
│  React Components (Login, Dashboard, etc.)              │
└───────────────────────┬─────────────────────────────────┘
                        │ HTTP/REST
┌───────────────────────▼─────────────────────────────────┐
│              CONTROLLER LAYER                           │
│  @RestController - AuthController, etc.                 │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│              SERVICE LAYER                              │
│  @Service - Business Logic                              │
│  UsuarioService, ClienteService, etc.                   │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│              REPOSITORY LAYER                           │
│  @Repository - Data Access                              │
│  JpaRepository interfaces                               │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│              PERSISTENCE LAYER                          │
│  JPA/Hibernate - ORM                                    │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│              DATABASE LAYER                             │
│  H2 In-Memory Database                                  │
└─────────────────────────────────────────────────────────┘
```

## 📦 Dependências Maven

```xml
spring-boot-starter-web        → REST API
spring-boot-starter-data-jpa   → JPA/Hibernate
spring-boot-starter-validation → Bean Validation
h2                             → Database H2
spring-boot-devtools           → Hot Reload
```

## 🔑 Anotações Principais

### Entidades
- `@Entity` - Define uma entidade JPA
- `@Table` - Mapeia para tabela específica
- `@Id` - Define chave primária
- `@GeneratedValue` - Auto-incremento
- `@Column` - Configura coluna
- `@Inheritance` - Define estratégia de herança
- `@DiscriminatorColumn` - Coluna discriminadora
- `@DiscriminatorValue` - Valor do discriminador

### Repositories
- `@Repository` - Marca como repositório
- `JpaRepository<T, ID>` - Interface base

### Services
- `@Service` - Marca como serviço
- `@Transactional` - Gerencia transações
- `@Autowired` - Injeção de dependência

### Controllers
- `@RestController` - Controller REST
- `@RequestMapping` - Mapeia URL base
- `@PostMapping` - Mapeia POST
- `@GetMapping` - Mapeia GET
- `@CrossOrigin` - Habilita CORS

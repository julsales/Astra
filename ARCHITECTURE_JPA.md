# Arquitetura JPA/Hibernate - Astra Cinemas

## 📚 Visão Geral

Este projeto utiliza **JPA (Java Persistence API)** com **Hibernate** como implementação ORM (Object-Relational Mapping) para gerenciar a persistência de dados.

## 🏗️ Arquitetura em Camadas

```
┌─────────────────────────────────────────┐
│         CONTROLLER LAYER                │
│   (AuthController, UserController...)   │
└───────────────┬─────────────────────────┘
                │
┌───────────────▼─────────────────────────┐
│          SERVICE LAYER                  │
│   (UsuarioService, ClienteService...)   │
└───────────────┬─────────────────────────┘
                │
┌───────────────▼─────────────────────────┐
│        REPOSITORY LAYER                 │
│   (UsuarioRepository, ClienteRepo...)   │
└───────────────┬─────────────────────────┘
                │
┌───────────────▼─────────────────────────┐
│          HIBERNATE (ORM)                │
│         JPA Implementation              │
└───────────────┬─────────────────────────┘
                │
┌───────────────▼─────────────────────────┐
│          DATABASE (H2)                  │
│       Relational Database               │
└─────────────────────────────────────────┘
```

## 📦 Estrutura de Entidades

### Hierarquia de Herança

Utilizamos **Single Table Inheritance** para a hierarquia de usuários:

```
Usuario (Abstract)
├── Cliente
├── Funcionario
└── Administrador
```

### Anotações JPA Utilizadas

#### @Entity
Define uma classe como entidade JPA que será mapeada para uma tabela.

```java
@Entity
@Table(name = "usuarios")
public class Usuario { }
```

#### @Id e @GeneratedValue
Define a chave primária e sua estratégia de geração.

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

#### @Column
Mapeia um atributo para uma coluna da tabela.

```java
@Column(nullable = false, unique = true)
private String email;
```

#### @Inheritance
Define a estratégia de herança entre entidades.

```java
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario")
```

#### @DiscriminatorValue
Define o valor discriminador para cada subclasse.

```java
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario { }
```

## 🔍 Repositories (Spring Data JPA)

Os repositories estendem `JpaRepository` que fornece:

- Operações CRUD básicas
- Queries personalizadas
- Paginação e ordenação

### Métodos Automáticos

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Spring Data JPA cria implementação automaticamente
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

### Query Methods Suportados

- `findBy...` - Buscar por campo
- `existsBy...` - Verificar existência
- `countBy...` - Contar registros
- `deleteBy...` - Deletar por critério

## ⚙️ Configuração do Hibernate

### application.properties

```properties
# DDL Auto - Gerenciamento do Schema
spring.jpa.hibernate.ddl-auto=create-drop
# Opções: create, create-drop, update, validate, none

# SQL Logging
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Dialect - Dialeto SQL
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

### Estratégias de DDL

- **create**: Cria o schema, destroi dados anteriores
- **create-drop**: Cria ao iniciar, remove ao finalizar
- **update**: Atualiza o schema se necessário
- **validate**: Valida o schema, não faz mudanças
- **none**: Sem ação automática

## 💾 Ciclo de Vida das Entidades

```
NEW (Transient)
     │
     ▼ persist()
MANAGED (Persistent) ←→ Database
     │
     ▼ remove()
REMOVED
     │
     ▼
DETACHED
```

### Callbacks de Ciclo de Vida

```java
@PrePersist
protected void onCreate() {
    dataCadastro = LocalDateTime.now();
}

@PreUpdate
protected void onUpdate() {
    dataAtualizacao = LocalDateTime.now();
}
```

## 📊 Estratégia de Herança: Single Table

Todas as classes da hierarquia são armazenadas em uma única tabela:

```sql
CREATE TABLE usuarios (
    id BIGINT PRIMARY KEY,
    tipo_usuario VARCHAR(255), -- Discriminator
    email VARCHAR(255),
    senha VARCHAR(255),
    nome VARCHAR(255),
    -- Campos específicos de Cliente
    cpf VARCHAR(11),
    pontos_fidelidade INT,
    -- Campos específicos de Funcionario
    matricula VARCHAR(50),
    cargo VARCHAR(100),
    setor VARCHAR(100),
    -- Campos específicos de Administrador
    nivel_acesso INT,
    departamento VARCHAR(100)
);
```

### Vantagens
- Performance em consultas polimórficas
- Simplicidade de schema
- Facilidade para adicionar subclasses

### Desvantagens
- Colunas podem ter muitos valores NULL
- Tabela pode ficar grande

## 🔐 Transações

O Spring gerencia transações automaticamente:

```java
@Service
@Transactional // Todas operações são transacionais
public class UsuarioService {
    
    @Transactional(readOnly = true) // Otimiza leitura
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
}
```

## 📝 Dados de Teste

Arquivo `data.sql` carrega dados iniciais:

**Credenciais de Teste:**

| Tipo | Email | Senha |
|------|-------|-------|
| Cliente | cliente@teste.com | 123456 |
| Funcionário | funcionario@teste.com | 123456 |
| Administrador | admin@teste.com | 123456 |

## 🔧 Console H2

Acesse o banco de dados em: `http://localhost:8080/h2-console`

**Configurações:**
- JDBC URL: `jdbc:h2:mem:astradb`
- Username: `sa`
- Password: `password`

## 📚 Referências

- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)
- [JPA Specifications](https://www.oracle.com/java/technologies/persistence-jsp.html)

## 🚀 Próximos Passos

- [ ] Implementar relacionamentos entre entidades (Filme, Sessão, Reserva)
- [ ] Adicionar validações com Bean Validation
- [ ] Implementar criptografia de senha (BCrypt)
- [ ] Adicionar auditoria com @CreatedDate e @LastModifiedDate
- [ ] Implementar paginação nas consultas
- [ ] Adicionar índices para otimização
- [ ] Migrar para PostgreSQL em produção

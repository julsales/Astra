# Registro de Cliente - Astra Cinemas

## ✅ Mudanças Implementadas

### 🎯 Objetivo
Criar tela de registro para clientes e remover opções de funcionário e administrador do sistema.

## 📱 Frontend

### 1. Nova Tela de Registro (`Register.js` e `Register.css`)

#### Campos do Formulário
- ✅ **Nome Completo** (obrigatório)
- ✅ **E-mail** (obrigatório, validação de formato)
- ✅ **Senha** (obrigatório, mínimo 6 caracteres)
- ✅ **Confirmar Senha** (obrigatório, deve coincidir)
- ✅ **CPF** (obrigatório, validação de formato)
- ✅ **Telefone** (opcional)
- ✅ **Data de Nascimento** (opcional)

#### Validações Implementadas
```javascript
- Email: formato válido (/\S+@\S+\.\S+/)
- Senha: mínimo 6 caracteres
- Confirmar Senha: deve ser igual à senha
- CPF: formato 11 dígitos
- Nome: obrigatório
```

#### Features
- ✅ Validação em tempo real
- ✅ Mensagens de erro específicas
- ✅ Layout responsivo (2 colunas em desktop, 1 em mobile)
- ✅ Design idêntico ao protótipo (roxo/violeta)
- ✅ Botão "Voltar ao Login"
- ✅ Integração com API REST

### 2. Tela de Login Atualizada

#### Removido
- ❌ Campo "Tipo de Usuário" (select com 3 opções)
- ❌ Opção "Funcionário"
- ❌ Opção "Administrador"

#### Adicionado
- ✅ Link "Não tem uma conta? Cadastre-se"
- ✅ Navegação entre Login e Registro

#### Mantido
- ✅ Campo E-mail
- ✅ Campo Senha
- ✅ Checkbox "Lembrar-me"
- ✅ Botão "Entrar"
- ✅ Design roxo/violeta

### 3. Gerenciamento de Rotas (`App.js`)

```javascript
const [showRegister, setShowRegister] = useState(false);

// Alterna entre Login e Registro
{showRegister ? (
  <Register onBackToLogin={() => setShowRegister(false)} />
) : (
  <Login onRegisterClick={() => setShowRegister(true)} />
)}
```

## 🔧 Backend

### 1. Novo Controller - `ClienteController.java`

#### Endpoint POST `/api/clientes`
Cadastra novo cliente com validações:

```java
@PostMapping
public ResponseEntity<?> cadastrarCliente(@RequestBody ClienteRequest request)
```

**Validações:**
- ✅ Email único (não pode duplicar)
- ✅ CPF único (não pode duplicar)
- ✅ Retorna erro 400 se já existir
- ✅ Retorna 201 Created se sucesso

**Response de Sucesso:**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com",
  "cpf": "12345678901",
  "pontosFidelidade": 0
}
```

**Response de Erro:**
```json
{
  "message": "Email já cadastrado"
}
```

#### Endpoint GET `/api/clientes/{id}`
Busca cliente por ID

### 2. Novos DTOs

#### `ClienteRequest.java`
```java
- nome: String
- email: String
- senha: String
- cpf: String
- telefone: String
- dataNascimento: String
```

#### `ClienteResponse.java`
```java
- id: Long
- nome: String
- email: String
- cpf: String
- pontosFidelidade: Integer
```

### 3. Service Atualizado - `ClienteService.java`

Novo método adicionado:
```java
public boolean existeEmail(String email)
```

## 🎨 Design da Tela de Registro

### Layout
```
┌─────────────────────────────────┐
│         ⭐ ASTRA                │
│           CINEMAS               │
├─────────────────────────────────┤
│        Criar Conta              │
│  Cadastre-se para reservar...   │
├─────────────────────────────────┤
│  Nome Completo: [_____________] │
│  E-mail:       [_____________]  │
│  Senha:        [____] [____]    │
│  Confirmar:    [____] [____]    │
│  CPF:          [____] [____]    │
│  Telefone:     [____] [____]    │
│  Data Nasc:    [_____________]  │
│                                 │
│  [ Cadastrar ]                  │
│  [ Voltar ao Login ]            │
└─────────────────────────────────┘
```

### Cores Utilizadas
- Background: `rgba(30, 20, 60, 0.6)`
- Inputs: `rgba(139, 92, 246, 0.1)`
- Bordas: `rgba(139, 92, 246, 0.3)`
- Botão: `linear-gradient(135deg, #8B5CF6 0%, #7C3AED 100%)`
- Erro: `#ef4444`

## 🔄 Fluxo de Cadastro

```
1. Usuário clica em "Cadastre-se" no login
   ↓
2. Preenche formulário de registro
   ↓
3. Sistema valida campos (frontend)
   ↓
4. Envia POST /api/clientes
   ↓
5. Backend valida email e CPF únicos
   ↓
6. Se OK: Salva no banco (JPA/Hibernate)
   ↓
7. Retorna sucesso
   ↓
8. Frontend exibe mensagem de sucesso
   ↓
9. Redireciona para tela de login
```

## 📊 Estrutura de Arquivos

```
frontend/src/
├── components/
│   ├── Login.js        ← Atualizado (removido tipos)
│   ├── Login.css       ← Atualizado (link cadastro)
│   ├── Register.js     ← NOVO
│   ├── Register.css    ← NOVO
│   └── Stars.js
├── App.js              ← Atualizado (gerencia rotas)
└── App.css

backend/src/main/java/com/astra/
├── controller/
│   ├── AuthController.java
│   └── ClienteController.java  ← NOVO
├── dto/
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── ClienteRequest.java     ← NOVO
│   └── ClienteResponse.java    ← NOVO
├── service/
│   └── ClienteService.java     ← Atualizado
├── repository/
│   └── ClienteRepository.java
└── model/
    ├── Usuario.java
    └── Cliente.java
```

## ✅ Checklist de Implementação

- [x] Criar componente Register.js
- [x] Criar estilos Register.css
- [x] Remover select "Tipo de Usuário" do Login
- [x] Adicionar link "Cadastre-se" no Login
- [x] Implementar navegação entre telas (App.js)
- [x] Criar ClienteController no backend
- [x] Criar DTOs (ClienteRequest/Response)
- [x] Adicionar validação de email único
- [x] Adicionar validação de CPF único
- [x] Implementar validações no frontend
- [x] Testar fluxo completo de cadastro
- [x] Garantir design idêntico ao protótipo

## 🧪 Teste o Fluxo

1. Acesse: http://localhost:3000
2. Clique em "Cadastre-se"
3. Preencha os campos
4. Clique em "Cadastrar"
5. Volte ao login
6. Entre com as credenciais cadastradas

## 🎯 Resultado

O sistema agora:
- ✅ Aceita APENAS clientes
- ✅ Possui tela de registro completa
- ✅ Valida dados no frontend e backend
- ✅ Armazena clientes no banco via JPA
- ✅ Design consistente com o protótipo
- ✅ Navegação fluida entre telas

🎉 Sistema de cadastro de clientes implementado com sucesso!

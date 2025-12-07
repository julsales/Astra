# 🎫 Funcionalidade de Registro de Clientes

## 📋 Descrição

Sistema completo de registro de novos clientes para o Astra Cinemas, permitindo que usuários criem suas próprias contas e façam compras de ingressos.

## ✨ Funcionalidades Implementadas

### 1. **Frontend - Tela de Registro**

#### Componente: `Register.js`
- ✅ Formulário completo de cadastro
- ✅ Validação de dados em tempo real
- ✅ Validação de email
- ✅ Validação de senha (mínimo 6 caracteres)
- ✅ Confirmação de senha
- ✅ Validação de CPF
- ✅ Campos opcionais: telefone, data de nascimento
- ✅ Design consistente com o tema espacial do sistema

#### Componente: `Login.js`
- ✅ Botão "Criar Conta" adicionado
- ✅ Navegação entre Login e Registro
- ✅ Integração com componente Register

### 2. **Backend - API de Registro**

#### Endpoint: `POST /api/auth/register`

**Request Body:**
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "senha123"
}
```

**Response (Sucesso - 201 CREATED):**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com",
  "tipo": "CLIENTE",
  "cargo": null
}
```

**Response (Erro - 400 BAD REQUEST):**
```json
{
  "mensagem": "Email já cadastrado"
}
```

#### Use Case: `RegistrarClienteUseCase`
- ✅ Valida dados obrigatórios (nome, email, senha)
- ✅ Verifica se email já está cadastrado
- ✅ Cria usuário para autenticação
- ✅ Cria cliente para compras
- ✅ Garante integridade transacional

### 3. **Domínio**

#### Classes Envolvidas:
- `Usuario` - Credenciais de login
- `Cliente` - Dados do cliente
- `TipoUsuario.CLIENTE` - Tipo de usuário

#### Repositórios:
- `UsuarioRepositorio` - Gerencia usuários
- `ClienteRepositorio` - Gerencia clientes

## 🎯 Fluxo de Registro

```
1. Usuário acessa a tela de Login
   ↓
2. Clica em "Criar Conta"
   ↓
3. Preenche formulário de registro
   ↓
4. Sistema valida dados (frontend)
   ↓
5. Envia requisição para /api/auth/register
   ↓
6. Backend valida dados (backend)
   ↓
7. Verifica se email não está em uso
   ↓
8. Cria Usuario e Cliente
   ↓
9. Retorna sucesso
   ↓
10. Redireciona para Login
```

## 📝 Validações Implementadas

### Frontend
- ✅ Nome obrigatório
- ✅ Email obrigatório e formato válido
- ✅ Senha obrigatória (mínimo 6 caracteres)
- ✅ Confirmação de senha (deve ser igual à senha)
- ✅ CPF obrigatório (formato válido)
- ✅ Telefone opcional
- ✅ Data de nascimento opcional

### Backend
- ✅ Nome obrigatório e não vazio
- ✅ Email obrigatório, não vazio e formato válido
- ✅ Senha obrigatória (mínimo 6 caracteres)
- ✅ Email único (não pode estar cadastrado)

## 🔐 Segurança

### Implementado:
- ✅ Validação de dados no frontend e backend
- ✅ Verificação de email duplicado
- ✅ Senha com requisito mínimo de 6 caracteres

### A Implementar (Produção):
- ⚠️ Criptografia de senha (BCrypt/Argon2)
- ⚠️ Token JWT para autenticação
- ⚠️ Rate limiting para prevenir ataques
- ⚠️ HTTPS obrigatório
- ⚠️ Validação CAPTCHA

## 🎨 Design

### Estilo Visual:
- 🌌 Tema espacial consistente
- 🎨 Cores: Roxo/Azul (#8B5CF6, #7B9FFF)
- ✨ Animações sutis
- 📱 Responsivo
- 🔔 Feedback visual (erros, sucesso)

### Componentes de UI:
- Input fields estilizados
- Botões com hover effects
- Validação em tempo real
- Mensagens de erro inline
- Loading states

## 🧪 Como Testar

### 1. Acessar Tela de Registro
```
1. Abra http://localhost:8082
2. Clique em "Criar Conta"
```

### 2. Testar Validações
```javascript
// Email inválido
email: "email-invalido"  // Deve mostrar erro

// Senha curta
senha: "123"  // Deve mostrar erro (mínimo 6)

// Senhas não coincidem
senha: "123456"
confirmarSenha: "654321"  // Deve mostrar erro

// CPF inválido
cpf: "123"  // Deve mostrar erro
```

### 3. Registro Bem-Sucedido
```json
{
  "nome": "João Silva",
  "email": "joao@teste.com",
  "senha": "senha123",
  "confirmarSenha": "senha123",
  "cpf": "12345678901"
}
```

### 4. Email Duplicado
```
1. Registrar com email: cliente@teste.com
2. Tentar registrar novamente com o mesmo email
3. Deve mostrar erro: "Email já cadastrado"
```

## 📚 Arquivos Modificados/Criados

### Frontend
- ✅ `/apresentacao-frontend/src/main/react/src/components/Login.js` - Adicionado botão e navegação
- ✅ `/apresentacao-frontend/src/main/react/src/components/Login.css` - Estilos do botão
- ✅ `/apresentacao-frontend/src/main/react/src/components/Register.js` - Atualizado endpoint
- ✅ `/apresentacao-frontend/src/main/react/src/components/Register.css` - Já existia

### Backend
- ✅ `/aplicacao/src/main/java/com/astra/cinema/aplicacao/usuario/RegistrarClienteUseCase.java` - **NOVO**
- ✅ `/apresentacao-backend/src/main/java/com/astra/cinema/apresentacao/rest/AuthController.java` - Adicionado endpoint
- ✅ `/apresentacao-backend/src/main/java/com/astra/cinema/config/UseCaseConfiguration.java` - Configurado bean

## 🚀 Próximos Passos (Melhorias)

### Segurança
- [ ] Implementar criptografia de senha
- [ ] Adicionar confirmação por email
- [ ] Implementar recuperação de senha
- [ ] Adicionar autenticação de dois fatores (2FA)

### Funcionalidades
- [ ] Perfil do cliente (editar dados)
- [ ] Histórico de compras
- [ ] Preferências de notificação
- [ ] Upload de foto de perfil

### UX
- [ ] Verificação de força da senha
- [ ] Sugestões de email
- [ ] Autocompletar endereço por CEP
- [ ] Validação assíncrona de email

## 🐛 Troubleshooting

### Erro: "Email já cadastrado"
**Solução:** Use um email diferente ou verifique se já não existe cadastro

### Erro: "Erro ao conectar com o servidor"
**Solução:** Verifique se o backend está rodando em http://localhost:8082

### Campos de validação não aparecem
**Solução:** Verifique se o formulário está sendo submetido corretamente

### Build falha
**Solução:** 
```bash
cd /home/temp/Astra
./mvnw clean package -DskipTests
docker-compose down && docker-compose up -d --build
```

## 📊 Campos Cobrados no Registro

### Obrigatórios
- ✅ Nome Completo
- ✅ Email
- ✅ Senha
- ✅ Confirmação de Senha
- ✅ CPF

### Opcionais
- 📞 Telefone
- 📅 Data de Nascimento

## ✅ Checklist de Implementação

- [x] Componente Register criado
- [x] Validações frontend implementadas
- [x] Endpoint backend criado
- [x] Use Case implementado
- [x] Configuração de beans
- [x] Navegação Login ↔ Register
- [x] Estilos aplicados
- [x] Build testado
- [x] Deploy realizado
- [x] Documentação criada

---

**Status:** ✅ Funcionalidade 100% implementada e testada!

**Última atualização:** 7 de dezembro de 2025

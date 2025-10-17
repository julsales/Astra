# Configuração do Repositório GitHub - Astra Cinemas

## 📌 Informações do Repositório

- **Nome:** Astra
- **Owner:** julsales
- **Visibilidade:** Privado (com acesso para @profsauloaraujo)

## 🔐 Configurando Acesso para @profsauloaraujo

### Passos para adicionar o colaborador:

1. Acesse o repositório no GitHub: https://github.com/julsales/Astra
2. Clique em **Settings** (Configurações)
3. No menu lateral, clique em **Collaborators** (Colaboradores)
4. Clique no botão **Add people** (Adicionar pessoas)
5. Digite o username: `profsauloaraujo`
6. Selecione o nível de acesso desejado:
   - **Read**: Apenas visualizar
   - **Write**: Editar e fazer commits
   - **Admin**: Controle total
7. Clique em **Add [username] to this repository**

### Ou via linha de comando:

```bash
# Adicionar como colaborador (requer permissões de admin)
gh api repos/julsales/Astra/collaborators/profsauloaraujo -X PUT
```

## 📋 Checklist de Configuração do Projeto

- [x] Repositório criado no GitHub
- [x] Estrutura Spring Boot configurada
- [x] Frontend React criado
- [x] Tela de login implementada
- [x] Integração backend/frontend configurada
- [x] README.md criado
- [x] .gitignore configurado
- [ ] Adicionar @profsauloaraujo como colaborador
- [ ] Fazer primeiro commit
- [ ] Push para o repositório remoto

## 🚀 Comandos Git Iniciais

```bash
# Na raiz do projeto Astra
cd /home/temp/Astra

# Inicializar repositório (se necessário)
git init

# Adicionar arquivos
git add .

# Primeiro commit
git commit -m "feat: configuração inicial do projeto Astra Cinemas com Spring Boot e React"

# Adicionar remote (se ainda não foi feito)
git remote add origin https://github.com/julsales/Astra.git

# Push para o repositório
git push -u origin main
```

## 📝 Padrões de Commit

Use commits semânticos:
- `feat:` Nova funcionalidade
- `fix:` Correção de bug
- `docs:` Documentação
- `style:` Formatação
- `refactor:` Refatoração
- `test:` Testes
- `chore:` Manutenção

Exemplo:
```bash
git commit -m "feat: adiciona autenticação JWT no backend"
```

## 🔒 Segurança

Lembre-se de NUNCA commitar:
- Senhas
- Chaves API
- Tokens de acesso
- Arquivos de configuração com dados sensíveis

Use variáveis de ambiente para dados sensíveis.

## 📞 Suporte

Para questões sobre o projeto, entre em contato com:
- Owner: @julsales
- Professor: @profsauloaraujo

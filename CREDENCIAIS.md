# 🎬 Astra Cinema - Credenciais de Acesso

## ✅ Aplicação Rodando

- **Frontend + Backend**: http://localhost:8081
- **PostgreSQL**: localhost:5432

## 👤 Usuários de Teste

### Administrador
- **Email**: `admin@astra.com`
- **Senha**: `demo123`
- **Tipo**: ADMIN

### Gerente
- **Email**: `gerente@astra.com`
- **Senha**: `gerente123`
- **Tipo**: FUNCIONARIO

### Atendente
- **Email**: `atendente@astra.com`
- **Senha**: `atendente123`
- **Tipo**: FUNCIONARIO

### Cliente
- **Email**: `tempzinxd@gmail.com`
- **Senha**: `thiago123`
- **Tipo**: CLIENTE

## 🎯 Dados de Demonstração

### Filmes Cadastrados
- Duna 2 (EM_CARTAZ)
- Matrix (EM_CARTAZ)
- Avatar 3 (RETIRADO)
- Oppenheimer (EM_CARTAZ)
- Barbie (EM_BREVE)

### Sessões Disponíveis
- Duna 2 - Hoje (2h a partir de agora)
- Matrix - Amanhã às 19h
- Oppenheimer - Amanhã às 15h

### Produtos da Bomboniere
- Pipoca Grande - R$ 18,00
- Pipoca Média - R$ 14,00
- Refrigerante 500ml - R$ 8,00
- Combo Pipoca + Refri - R$ 25,00
- ... e mais 6 produtos

## 🔧 Gerenciar Containers

### Parar serviços
```bash
docker stop astra-app astra-postgres
docker rm astra-app astra-postgres
```

### Iniciar serviços
```bash
# Postgres
docker run -d --name astra-postgres --network astra-net \
  -e POSTGRES_DB=astra -e POSTGRES_USER=astra -e POSTGRES_PASSWORD=astra \
  -p 5432:5432 postgres:16

# Aguardar 5 segundos
sleep 5

# Aplicação
docker run -d --name astra-app --network astra-net \
  -e DATABASE_URL=jdbc:postgresql://astra-postgres:5432/astra \
  -e DATABASE_USER=astra -e DATABASE_PASSWORD=astra \
  -p 8081:8080 astra-apresentacao-backend:0.0.1-SNAPSHOT
```

### Verificar logs
```bash
docker logs astra-app
```

## 📝 Notas Importantes

- ⚠️ Senhas em **texto plano** apenas para demonstração
- ✅ CORS configurado para permitir todas as origens
- ✅ Frontend usa URLs relativas (sem problema de porta)
- ✅ Flyway gerencia migrações automaticamente
- ✅ Dados iniciais carregados via V2__dados_iniciais.sql

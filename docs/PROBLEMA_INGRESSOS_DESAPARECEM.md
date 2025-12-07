# 🐛 PROBLEMA: Ingressos Ativos Desaparecem Após Rebuild

## 📋 Descrição do Problema

Ingressos com status ATIVO desaparecem do banco de dados após reiniciar a aplicação (rebuild), tornando-os "não validáveis" como se não existissem no banco.

## 🔍 Causa Raiz Identificada

A causa é o **`ON DELETE CASCADE`** na foreign key entre `ingresso` e `compra`:

```sql
CREATE TABLE IF NOT EXISTS ingresso (
    id              SERIAL PRIMARY KEY,
    compra_id       INTEGER NOT NULL REFERENCES compra(id) ON DELETE CASCADE,
    ...
);
```

### O que isso significa?

- ✅ Se uma **compra** for deletada do banco
- ⚠️ TODOS os **ingressos** associados são **automaticamente deletados**
- 💥 Isso é **irreversível** e acontece em nível de banco de dados

## 🎯 Quando o Problema Ocorre?

O problema pode ocorrer em várias situações:

### 1. **Rebuild com `docker-compose down -v`**
```bash
docker-compose down -v  # ❌ A flag -v remove os VOLUMES (apaga todos os dados!)
docker-compose up --build
```

### 2. **Código que limpa dados antigos**
Se houver algum processo que:
- Limpa compras antigas/canceladas
- Faz "garbage collection" de dados
- Remove compras sem pagamento confirmado

### 3. **Testes que limpam o banco**
Se você roda testes que fazem:
```java
@BeforeEach
void setUp() {
    compraRepository.deleteAll(); // Vai deletar TODOS os ingressos também!
}
```

## ✅ Soluções

### Solução 1: Remover o CASCADE (RECOMENDADO)

**Migration já criada**: `V11__remove_cascade_from_ingresso.sql`

Para aplicar:
```bash
# Parar a aplicação
docker-compose down

# Subir novamente (Flyway vai aplicar a migration automaticamente)
docker-compose up
```

Isso vai:
- ✅ Remover o `ON DELETE CASCADE`
- ✅ Ingressos NÃO serão mais deletados automaticamente
- ✅ Será necessário deletar ingressos manualmente antes de deletar uma compra

### Solução 2: Usar o Script de Diagnóstico

```bash
./diagnostico-ingressos.sh
```

Esse script vai mostrar:
- Status atual do banco
- Quantos ingressos e compras existem
- Se o CASCADE ainda está ativo
- Dados detalhados para debug

### Solução 3: Rebuild Correto (SEM perder dados)

**❌ NÃO FAÇA:**
```bash
docker-compose down -v  # Remove volumes = perde dados!
```

**✅ FAÇA:**
```bash
# Opção 1: Rebuild apenas da aplicação (mantém o banco)
docker-compose stop astra-app
docker-compose rm -f astra-app
docker-compose up --build astra-app

# Opção 2: Rebuild completo MAS mantendo volumes
docker-compose down  # SEM a flag -v
docker-compose up --build

# Opção 3: Restart simples
docker-compose restart
```

## 🔍 Como Verificar se o Problema Foi Resolvido

1. **Antes de aplicar a fix:**
```bash
./diagnostico-ingressos.sh
# Deve mostrar "CASCADE" no delete_rule
```

2. **Aplicar a migration V11**
```bash
docker-compose down
docker-compose up
```

3. **Depois de aplicar a fix:**
```bash
./diagnostico-ingressos.sh
# Deve mostrar "NO ACTION" ou "RESTRICT" no delete_rule
```

4. **Testar o comportamento:**
```sql
-- Tentar deletar uma compra que tem ingressos
DELETE FROM compra WHERE id = 1;
-- Antes da fix: Sucesso (ingressos deletados automaticamente)
-- Depois da fix: ERRO (não pode deletar porque há ingressos)
```

## 📊 Impacto da Mudança

### Antes (com CASCADE):
- ✅ Fácil limpar dados (deletar compra = deletar tudo)
- ❌ Perda acidental de dados (ingressos válidos podem ser deletados)
- ❌ Sem controle sobre o que é deletado

### Depois (sem CASCADE):
- ✅ Dados protegidos contra deleção acidental
- ✅ Controle explícito sobre o que deletar
- ⚠️ Necessário deletar ingressos manualmente antes de deletar compra

## 🔧 Se Precisar Deletar Compras no Futuro

```java
// Código correto para deletar uma compra
public void deletarCompra(CompraId compraId) {
    // 1. Primeiro, buscar e deletar os ingressos
    List<Ingresso> ingressos = ingressoRepository.buscarPorCompraId(compraId);
    for (Ingresso ingresso : ingressos) {
        ingressoRepository.deletar(ingresso.getIngressoId());
    }
    
    // 2. Depois, deletar a compra
    compraRepository.deletar(compraId);
}
```

## 📚 Referências

- Migration V1: `/infraestrutura/src/main/resources/db/migration/V1__create_core_tables.sql`
- Migration V11 (FIX): `/infraestrutura/src/main/resources/db/migration/V11__remove_cascade_from_ingresso.sql`
- Script diagnóstico: `./diagnostico-ingressos.sh`

## 🎯 Próximos Passos

1. ✅ Aplicar a migration V11
2. ✅ Rodar o script de diagnóstico
3. ✅ Testar criação de ingressos
4. ✅ Fazer rebuild sem perder dados
5. ✅ Verificar que ingressos permanecem após rebuild

# 🔴 BREAKING CHANGE - Refatoração de Sala (Capacidade)

## Data: 7 de dezembro de 2025

## Problema Identificado
A **capacidade da sala** estava incorretamente modelada como um atributo de **Sessão**, quando na verdade é uma característica **intrínseca e imutável da Sala física**.

### ❌ Modelo Incorreto (Antes)
```java
class Sessao {
    private String sala;        // ❌ Sala como String
    private int capacidade;     // ❌ Capacidade na Sessão
}
```

**Problemas:**
1. ✗ Violação de DDD - capacidade não é propriedade de sessão
2. ✗ Mesma sala pode ter capacidades diferentes entre sessões
3. ✗ Duplicação de dados - capacidade repetida em cada sessão
4. ✗ Inconsistência - dados da sala espalhados no sistema

---

## ✅ Modelo Correto (Após Refatoração)

### Nova Estrutura de Domínio

```java
// 1. SalaId - Value Object no Shared Kernel
class SalaId {
    private final int id;
}

// 2. Sala - Entidade com capacidade fixa
class Sala {
    private final SalaId salaId;
    private final String nome;
    private final int capacidade;    // ✅ Capacidade FIXA da sala
    private final TipoSala tipo;
}

// 3. Sessão - Referencia a Sala por SalaId
class Sessao {
    private final SalaId salaId;    // ✅ Referência à sala
    // capacidade removida ❌
}
```

---

## 📋 Arquivos Criados

### Domínio
- ✅ `dominio-compartilhado/comum/SalaId.java` - Value Object
- ✅ `dominio-sessoes/sessao/Sala.java` - Entidade
- ✅ `dominio-sessoes/sessao/TipoSala.java` - Enum (PADRAO, VIP, IMAX, 3D, etc.)
- ✅ `dominio-sessoes/sessao/SalaRepositorio.java` - Interface

### Modificados
- ✅ `dominio-sessoes/sessao/Sessao.java`:
  - Removido: `private final int capacidade`
  - Removido: `private final String sala`
  - Adicionado: `private final SalaId salaId`
  - `getCapacidade()` agora retorna `mapaAssentosDisponiveis.size()` (transitório)

---

## 🔧 Próximos Passos

### Backend (Pendente)

#### 1. Infraestrutura JPA
- [ ] Criar `SalaJpa.java` - Entidade JPA
- [ ] Criar `SalaJpaRepository.java` - Spring Data
- [ ] Criar `SalaRepositorioJpa.java` - Implementação
- [ ] Atualizar `SessaoJpa.java`:
  - Remover `private Integer capacidade`
  - Adicionar `@ManyToOne private SalaJpa sala`
- [ ] Criar migration SQL para:
  - Criar tabela `salas` (id, nome, capacidade, tipo)
  - Alterar tabela `sessoes`:
    - Adicionar FK `sala_id`
    - Remover coluna `capacidade`
  - Popular tabela `salas` com dados padrão

#### 2. Application Layer
- [ ] Atualizar `CriarSessaoUseCase`:
  - Remover parâmetro `capacidadeSala`
  - Adicionar parâmetro `SalaId`
  - Buscar sala do repositório
  - Gerar assentos baseado na capacidade da sala
- [ ] Atualizar `ModificarSessaoUseCase`:
  - Remover parâmetro `novaCapacidade`
  - Permitir trocar de sala (opcional)

#### 3. Presentation Layer
- [ ] Criar `SalaController.java`:
  - GET `/api/salas` - Listar todas
  - GET `/api/salas/{id}` - Obter por ID
  - POST `/api/salas` - Criar (admin)
- [ ] Criar `SalaDTO.java`
- [ ] Atualizar `SessaoController`:
  - Endpoint deve receber `salaId` ao invés de `capacidade`
- [ ] Atualizar `SessaoDTO`:
  - Adicionar campo `salaId`
  - Adicionar campo `salaNome` (para display)
  - **Manter** `capacidade` temporariamente para compatibilidade

---

## 📁 ARQUIVOS FRONTEND AFETADOS

### 🎯 Arquivo Principal a Modificar

**`/home/temp/Astra/apresentacao-frontend/src/main/react/src/components/admin/pages/Sessoes.js`**

Este arquivo possui **19 ocorrências** da palavra "capacidade" e precisa de alterações significativas:

| Linha | Código | Ação Necessária |
|-------|--------|-----------------|
| 38 | `capacidadeSala: 100` | ❌ REMOVER do state inicial |
| 115 | `capacidadeSala: sessao.capacidade` | ❌ SUBSTITUIR por `salaId: sessao.salaId` |
| 123 | `capacidadeSala: 100` | ❌ SUBSTITUIR por `salaId: null` |
| 142 | `capacidadeSala: 100` | ❌ SUBSTITUIR por `salaId: null` |
| 160 | `const capacidadeValor = parseInt(...)` | ❌ REMOVER esta linha |
| 166 | `capacidade: capacidadeValor` | ❌ SUBSTITUIR por `salaId: formData.salaId` |
| 173 | `capacidade: capacidadeValor` | ❌ SUBSTITUIR por `salaId: formData.salaId` |
| 399 | `<th>Capacidade</th>` | ✅ MANTER (só leitura) |
| 416 | `<td>{sessao.capacidade} lugares</td>` | ✅ MANTER (backend retorna via getCapacidade()) |
| **495-501** | **FormControl completo** | ❌ **REMOVER COMPLETAMENTE** |

### 📝 Arquivos com Leitura Apenas (NÃO PRECISAM ALTERAR)

**`FuncionarioPanel.js`** - Linhas 464, 470
- Apenas lê `proximaSessao.capacidade` do backend
- Backend continuará retornando via método `getCapacidade()` ✅

**`CompraIngresso.js`** - Linha 39  
- Apenas comentário explicativo
- Pode atualizar o comentário opcionalmente

---

### Frontend (React) - **BREAKING CHANGES** 🔴

#### 1. Criar novo componente de Salas
```javascript
// src/services/salaService.js
export const salaService = {
  listarTodas: async () => {
    const response = await fetch('/api/salas');
    return response.json();
  },
  
  obterPorId: async (id) => {
    const response = await fetch(`/api/salas/${id}`);
    return response.json();
  }
};
```

#### 2. Atualizar telas de Admin - Criar Sessão
**Arquivo:** `src/components/admin/pages/Sessoes.js`

❌ **Remover campo:**
```javascript
// REMOVER ESTE CAMPO
<FormControl>
  <FormLabel>Capacidade da Sala</FormLabel>
  <Input 
    type="number" 
    value={novaCapacidade} 
    onChange={(e) => setNovaCapacidade(e.target.value)} 
  />
</FormControl>
```

✅ **Adicionar dropdown de Salas:**
```javascript
const [salas, setSalas] = useState([]);
const [salaSelecionada, setSalaSelecionada] = useState(null);

useEffect(() => {
  salaService.listarTodas().then(setSalas);
}, []);

// No formulário:
<FormControl isRequired>
  <FormLabel>Sala</FormLabel>
  <Select 
    placeholder="Selecione a sala"
    value={salaSelecionada} 
    onChange={(e) => setSalaSelecionada(e.target.value)}
  >
    {salas.map(sala => (
      <option key={sala.id} value={sala.id}>
        {sala.nome} ({sala.capacidade} lugares - {sala.tipo})
      </option>
    ))}
  </Select>
</FormControl>
```

✅ **Atualizar requisição de criação:**
```javascript
// ANTES (Errado)
const novaSessao = {
  filmeId,
  horario,
  capacidade: novaCapacidade  // ❌ Remover
};

// DEPOIS (Correto)
const novaSessao = {
  filmeId,
  horario,
  salaId: salaSelecionada  // ✅ Usar SalaId
};
```

#### 3. Atualizar visualização de Sessões
**Arquivo:** `src/components/cliente/Sessoes.js`

O campo `capacidade` continua existindo no DTO (por enquanto), então **não é necessário mudar** a visualização, mas é bom exibir também o nome da sala:

```javascript
<Text>Sala: {sessao.salaNome || 'Sala 1'}</Text>
<Text>Capacidade: {sessao.capacidade} lugares</Text>
<Text>Disponíveis: {sessao.assentosDisponiveis}</Text>
```

#### 4. Nova tela: Gerenciar Salas (Admin)
**Arquivo:** `src/components/admin/pages/Salas.js` (NOVO)

```javascript
// Listagem de salas com:
// - ID
// - Nome
// - Capacidade
// - Tipo
// - Ações: Editar (somente nome/tipo), Ver Sessões
```

---

## 🗄️ Migrations SQL Necessárias

### Migration 1: Criar tabela Salas
```sql
-- V002__criar_tabela_salas.sql
CREATE TABLE salas (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    capacidade INTEGER NOT NULL CHECK (capacidade > 0),
    tipo VARCHAR(50) NOT NULL DEFAULT 'PADRAO',
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Popular com salas padrão
INSERT INTO salas (nome, capacidade, tipo) VALUES
    ('Sala 1', 100, 'PADRAO'),
    ('Sala 2', 150, 'PADRAO'),
    ('Sala 3', 80, 'VIP'),
    ('Sala IMAX', 200, 'IMAX'),
    ('Sala 4D', 120, '4DX');
```

### Migration 2: Alterar tabela Sessões
```sql
-- V003__migrar_sessoes_para_salas.sql
-- 1. Adicionar coluna sala_id
ALTER TABLE sessoes ADD COLUMN sala_id INTEGER;

-- 2. Migrar dados existentes (mapear sala string -> sala_id)
UPDATE sessoes SET sala_id = 1 WHERE sala IS NULL OR sala = 'Sala 1';
UPDATE sessoes SET sala_id = 2 WHERE sala = 'Sala 2';
UPDATE sessoes SET sala_id = 3 WHERE sala = 'Sala 3' OR sala LIKE '%VIP%';

-- 3. Tornar sala_id obrigatório e adicionar FK
ALTER TABLE sessoes ALTER COLUMN sala_id SET NOT NULL;
ALTER TABLE sessoes ADD CONSTRAINT fk_sessao_sala 
    FOREIGN KEY (sala_id) REFERENCES salas(id);

-- 4. Remover colunas antigas
ALTER TABLE sessoes DROP COLUMN sala;
ALTER TABLE sessoes DROP COLUMN capacidade;
```

---

## 📊 Impacto e Compatibilidade

### API Breaking Changes
- ❌ `POST /api/sessoes` - Não aceita mais `capacidade`, requer `salaId`
- ❌ `PUT /api/sessoes/{id}` - Não aceita mais `novaCapacidade`

### API Retrocompatível
- ✅ `GET /api/sessoes` - Continua retornando `capacidade` (derivada)
- ✅ `GET /api/sessoes/{id}` - Adiciona campos `salaId` e `salaNome`

### Frontend Impactado
- 🔴 **Alta prioridade:** Formulário de criação de sessão (admin)
- 🟡 **Média prioridade:** Formulário de edição de sessão (admin)
- 🟢 **Baixa prioridade:** Visualização de sessões (cliente) - sem mudanças

---

## ✅ Benefícios da Refatoração

1. **Modelagem correta do domínio**
   - Sala é uma entidade com características próprias
   - Capacidade é imutável e pertence à sala

2. **Consistência de dados**
   - Uma única fonte de verdade para capacidade
   - Impossível ter inconsistências

3. **Flexibilidade**
   - Fácil adicionar novos atributos à sala (tipo, equipamentos)
   - Fácil implementar regras por tipo de sala (preços diferentes)

4. **Manutenibilidade**
   - Mudanças em salas não afetam sessões existentes
   - Histórico de sessões preservado mesmo se sala mudar

---

## 🚀 Ordem de Implementação Recomendada

1. ✅ **Domínio** (Concluído)
2. ⏳ **Infraestrutura JPA** (Próximo)
3. ⏳ **Migrations SQL**
4. ⏳ **Application Layer**
5. ⏳ **Presentation Layer (Backend)**
6. ⏳ **Frontend React**
7. ⏳ **Testes**

---

## 📝 Checklist de Implementação

### Backend
- [x] Criar `SalaId` no shared kernel
- [x] Criar entidade `Sala`
- [x] Criar `TipoSala` enum
- [x] Criar `SalaRepositorio` interface
- [x] Refatorar `Sessao` (remover capacidade)
- [ ] Criar `SalaJpa`, `SalaRepositorioJpa`
- [ ] Criar migrations SQL
- [ ] Atualizar `CinemaMapeador`
- [ ] Atualizar `CriarSessaoUseCase`
- [ ] Atualizar `ModificarSessaoUseCase`
- [ ] Criar `SalaController`
- [ ] Atualizar `SessaoDTO`

### Frontend
- [ ] Remover campo `capacidade` de formulários
- [ ] Criar `salaService.js`
- [ ] Adicionar dropdown de salas em criação de sessão
- [ ] Criar tela de gerenciamento de salas (admin)
- [ ] Atualizar exibição para mostrar nome da sala
- [ ] Testar todos os fluxos

---

## 🎯 GUIA PRÁTICO - REFATORAÇÃO FRONTEND

### 📍 Arquivo Principal: `Sessoes.js`

#### Mudança 1: State do Formulário (Linha ~38)

```diff
const [formData, setFormData] = useState({
  filmeId: '',
  horario: '',
-  sala: 'Sala 1',
-  capacidadeSala: 100
+  salaId: null
});

+ const [salas, setSalas] = useState([]);
```

#### Mudança 2: Carregar Salas (Adicionar após useEffect linha ~56)

```javascript
const carregarSalas = async () => {
  try {
    const response = await fetch('/api/salas');
    if (response.ok) {
      const dados = await response.json();
      setSalas(dados);
    }
  } catch (err) {
    console.error('Erro ao carregar salas:', err);
  }
};

useEffect(() => {
  carregarDados({});
  carregarSalas();
}, []);
```

#### Mudança 3: Abrir Modal (Linha ~115)

```diff
setFormData({
  filmeId: sessao.filmeId,
  horario: dataFormatada,
-  sala: sessao.sala,
-  capacidadeSala: sessao.capacidade
+  salaId: sessao.salaId
});
```

#### Mudança 4: Payload da API (Linhas ~160-174)

```diff
- const capacidadeValor = parseInt(formData.capacidadeSala, 10) || 100;

const payload = editando ? {
    horario: horarioISO,
-    sala: formData.sala,
-    capacidade: capacidadeValor,
+    salaId: formData.salaId,
    funcionario: getFuncionarioPayload()
  } : {
    filmeId: parseInt(formData.filmeId),
    horario: horarioISO,
-    sala: formData.sala,
-    capacidade: capacidadeValor,
+    salaId: formData.salaId,
    funcionario: getFuncionarioPayload()
  };
```

#### Mudança 5: FormControl (Linha ~495) - SUBSTITUIR COMPLETAMENTE

**❌ REMOVER:**
```javascript
<div className="form-group">
  <label>Capacidade da Sala *</label>
  <input
    type="number"
    min="10"
    value={formData.capacidadeSala}
    onChange={(e) => setFormData({...formData, capacidadeSala: e.target.value})}
    required
  />
</div>
```

**✅ ADICIONAR:**
```javascript
<div className="form-group">
  <label>Sala *</label>
  <select
    value={formData.salaId || ''}
    onChange={(e) => setFormData({...formData, salaId: parseInt(e.target.value)})}
    required
  >
    <option value="">Selecione a sala</option>
    {salas.map(sala => (
      <option key={sala.id} value={sala.id}>
        {sala.nome} ({sala.capacidade} lugares - {sala.tipo})
      </option>
    ))}
  </select>
  <small style={{color: 'rgba(255,255,255,0.6)', display: 'block', marginTop: '5px'}}>
    💡 A capacidade é definida pela sala selecionada
  </small>
</div>
```

#### Mudança 6: Remover Dropdown Antigo de Sala (Linha ~514-524)

**❌ DELETAR COMPLETAMENTE:**
```javascript
<div className="form-group">
  <label>Sala *</label>
  <select value={formData.sala} onChange={...}>
    <option value="Sala 1">Sala 1</option>
    <option value="Sala 2">Sala 2</option>
    <!-- ... -->
  </select>
</div>
```

---

### 🎨 Melhorias Visuais Opcionais

#### Exibir Nome da Sala na Tabela (Linha ~416)

```diff
<td>
  {sessao.capacidade} lugares
+  <br />
+  <small style={{color: 'rgba(255,255,255,0.6)'}}>
+    {sessao.salaNome}
+  </small>
</td>
```

---

**Status:** 🟡 Em Progresso  
**Prioridade:** 🔴 Alta - Breaking Change  
**Estimativa:** ~4-6 horas (backend + frontend)

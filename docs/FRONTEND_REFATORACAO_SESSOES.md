# 🎨 GUIA DE REFATORAÇÃO - FRONTEND

## 📌 Contexto

**Mudança no Backend**: Capacidade agora pertence à **Sala**, não à **Sessão**.

**Arquivo Principal**: `/apresentacao-frontend/src/main/react/src/components/admin/pages/Sessoes.js`

---

## 🎯 ALTERAÇÕES NECESSÁRIAS - 7 MUDANÇAS

### 1️⃣ **State Inicial** (Linha ~38)

```diff
const [formData, setFormData] = useState({
  filmeId: '',
  horario: '',
-  sala: 'Sala 1',
-  capacidadeSala: 100
+  salaId: null
});

+ // Adicionar novo state
+ const [salas, setSalas] = useState([]);
```

---

### 2️⃣ **Carregar Salas da API** (Adicionar após useEffect ~linha 56)

```javascript
// ADICIONAR esta função
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

// MODIFICAR o useEffect existente
useEffect(() => {
  carregarDados({});
  carregarSalas();  // ← ADICIONAR esta linha
}, []);
```

---

### 3️⃣ **Abrir Modal para Edição** (Linha ~115)

```diff
if (sessao) {
  setEditando(sessao);
  const dataFormatada = new Date(sessao.horario).toISOString().slice(0, 16);
  setFormData({
    filmeId: sessao.filmeId,
    horario: dataFormatada,
-    sala: sessao.sala,
-    capacidadeSala: sessao.capacidade
+    salaId: sessao.salaId
  });
}
```

---

### 4️⃣ **Limpar Formulário** (Linha ~123 e ~142)

```diff
- setFormData({ filmeId: '', horario: '', sala: 'Sala 1', capacidadeSala: 100 });
+ setFormData({ filmeId: '', horario: '', salaId: null });
```

---

### 5️⃣ **Payload da API** (Linhas ~160-174) - CRÍTICO

```diff
- // REMOVER estas linhas:
- const capacidadeValor = parseInt(formData.capacidadeSala, 10) || 100;

const payload = editando
  ? {
      horario: horarioISO,
-      sala: formData.sala,
-      capacidade: capacidadeValor,
+      salaId: formData.salaId,
      funcionario: getFuncionarioPayload()
    }
  : {
      filmeId: parseInt(formData.filmeId),
      horario: horarioISO,
-      sala: formData.sala,
-      capacidade: capacidadeValor,
+      salaId: formData.salaId,
      funcionario: getFuncionarioPayload()
    };
```

---

### 6️⃣ **REMOVER Campo de Capacidade** (Linhas ~495-501)

**❌ DELETAR COMPLETAMENTE ESTE BLOCO:**

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

---

### 7️⃣ **SUBSTITUIR Dropdown de Sala** (Linhas ~514-524)

**❌ REMOVER o dropdown hardcoded:**

```javascript
<div className="form-group">
  <label>Sala *</label>
  <select
    value={formData.sala}
    onChange={(e) => setFormData({...formData, sala: e.target.value})}
    required
  >
    <option value="Sala 1">Sala 1</option>
    <option value="Sala 2">Sala 2</option>
    <option value="Sala 3">Sala 3</option>
    <option value="Sala 4">Sala 4</option>
  </select>
</div>
```

**✅ ADICIONAR dropdown dinâmico:**

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
        {sala.nome} - {sala.capacidade} lugares ({sala.tipo})
      </option>
    ))}
  </select>
  <small style={{
    color: 'rgba(255,255,255,0.6)', 
    display: 'block', 
    marginTop: '5px'
  }}>
    💡 A capacidade é definida pela sala selecionada
  </small>
</div>
```

---

## 📊 TABELA - NÃO PRECISA ALTERAR

**Linha ~416** - Backend retorna `capacidade` via método `getCapacidade()`:

```javascript
<td>{sessao.capacidade} lugares</td>  // ✅ Funciona normalmente
```

**Opcional** - Adicionar nome da sala:
```javascript
<td>
  {sessao.capacidade} lugares
  <br />
  <small style={{color: 'rgba(255,255,255,0.6)'}}>
    {sessao.salaNome}
  </small>
</td>
```

---

## 🧪 CHECKLIST DE TESTE

Após implementar as mudanças:

- [ ] Carregar página de Sessões - salas aparecem no dropdown
- [ ] Criar nova sessão - selecionar sala funciona
- [ ] Editar sessão existente - sala correta pré-selecionada
- [ ] Tabela exibe capacidade corretamente
- [ ] API recebe `salaId` ao invés de `capacidade`
- [ ] Formulário não solicita mais entrada manual de capacidade

---

## ⚠️ OUTROS ARQUIVOS - NÃO PRECISAM ALTERAR

### `FuncionarioPanel.js` (Linhas 464, 470)
```javascript
proximaSessao.capacidade  // ✅ Backend retorna via getCapacidade()
```
**Ação**: Nenhuma mudança necessária

### `CompraIngresso.js` (Linha 39)
```javascript
// O backend retorna { sessaoId, capacidade, assentos: {...} }
```
**Ação**: Opcional - atualizar comentário

---

## 🔗 PRÉ-REQUISITOS

### Backend DEVE estar pronto:
1. ✅ Entidade `Sala` criada
2. ✅ `SessaoDTO` retorna `salaId` e `salaNome`
3. ✅ Endpoint `/api/salas` disponível
4. ✅ Migration criou tabela `salas` com dados iniciais

**Se o backend não estiver pronto, o frontend vai quebrar! ⚠️**

---

## 🚀 ORDEM DE IMPLEMENTAÇÃO

1. ✅ Backend (domínio, infraestrutura, API)
2. ✅ Migrations SQL
3. 👉 **VOCÊ ESTÁ AQUI** - Frontend
4. ⏳ Testes integração

---

**Arquivo Relacionado**: `/home/temp/Astra/docs/refatoracao-sala-capacidade.md` (documentação completa)

**Estimativa**: ~2 horas para implementação + teste

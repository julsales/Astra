/* eslint-disable no-restricted-globals */
import React, { useEffect, useState } from 'react';
import './Bomboniere.css';

const Bomboniere = () => {
  const [produtos, setProdutos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState({ nome: '', preco: '', estoque: '' });
  const [modalOpen, setModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState(null); // 'add'|'edit'|'entrada'|'ajuste'
  const [modalData, setModalData] = useState(null);

  const load = () => {
    setLoading(true);
    fetch('/api/produtos')
      .then((r) => r.json())
      .then((data) => setProdutos(data || []))
      .catch(() => setProdutos([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleAdd = async (e) => {
    e.preventDefault();
    const payload = { nome: form.nome, preco: parseFloat(form.preco), estoque: parseInt(form.estoque, 10) };
    try {
      const res = await fetch('/api/produtos', {
        method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload)
      });
      if (!res.ok) throw new Error('Falha ao criar produto');
      setForm({ nome: '', preco: '', estoque: '' });
      load();
    } catch (err) {
      alert(err.message);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm) {
      // fallback
      if (!confirm('Remover produto ' + id + '?')) return;
    }
    await fetch('/api/produtos/' + id, { method: 'DELETE' });
    load();
  };

  const openModal = (mode, data = null) => {
    setModalMode(mode);
    setModalData(data);
    setModalOpen(true);
  };

  const closeModal = () => {
    setModalOpen(false);
    setModalMode(null);
    setModalData(null);
  };

  const handleModalSubmit = async (payload) => {
    try {
      if (modalMode === 'edit') {
        await fetch('/api/produtos/' + modalData.id, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
      } else if (modalMode === 'entrada') {
        await fetch('/api/produtos/' + modalData.id + '/entrada', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ quantidade: payload.quantidade }) });
      } else if (modalMode === 'ajuste') {
        await fetch('/api/produtos/' + modalData.id + '/ajuste', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ novoEstoque: payload.novoEstoque }) });
      }
      closeModal();
      load();
    } catch (err) {
      alert('Erro: ' + err.message);
    }
  };

  const handleEdit = (p) => openModal('edit', p);
  const handleEntrada = (p) => openModal('entrada', p);
  const handleAjuste = (p) => openModal('ajuste', p);

  if (loading) return <div className="bomboniere-loading">Carregando bomboniere...</div>;

  return (
    <div className="admin-page bomboniere-page">
      <div className="bomboniere-header">
        <h1>Bomboniere</h1>
        <p className="subtitle">Controle de estoque — entradas, ajustes e conferência.</p>
      </div>

      <div className="bomboniere-actions">
        <form className="add-form" onSubmit={handleAdd}>
          <input className="input" placeholder="Nome" value={form.nome} onChange={e => setForm({...form, nome: e.target.value})} required />
          <input className="input small" placeholder="Preço" value={form.preco} onChange={e => setForm({...form, preco: e.target.value})} required />
          <input className="input small" placeholder="Estoque" value={form.estoque} onChange={e => setForm({...form, estoque: e.target.value})} required />
          <button className="btn primary" type="submit">Adicionar Produto</button>
        </form>
      </div>

      <div className="table-wrapper">
        <table className="bomboniere-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nome</th>
              <th>Preço</th>
              <th>Estoque</th>
              <th className="actions-col">Ações</th>
            </tr>
          </thead>
          <tbody>
            {produtos.map((p) => (
              <tr key={p.id}>
                <td className="id-col">{p.id}</td>
                <td className="name-col">{p.nome}</td>
                <td className="price-col">{p.preco}</td>
                <td className="stock-col">{p.estoque}</td>
                <td className="actions-col">
                  <button className="btn" onClick={() => handleEdit(p)}>Editar</button>
                  <button className="btn danger" onClick={() => handleDelete(p.id)}>Remover</button>
                  <button className="btn" onClick={() => handleEntrada(p)}>Entrada</button>
                  <button className="btn" onClick={() => handleAjuste(p)}>Ajuste</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {modalOpen && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h3>{modalMode === 'edit' ? 'Editar Produto' : modalMode === 'entrada' ? 'Registrar Entrada' : 'Ajustar Estoque'}</h3>
            {modalMode === 'edit' && (
              <EditForm data={modalData} onCancel={closeModal} onSubmit={handleModalSubmit} />
            )}
            {modalMode === 'entrada' && (
              <EntradaForm data={modalData} onCancel={closeModal} onSubmit={handleModalSubmit} />
            )}
            {modalMode === 'ajuste' && (
              <AjusteForm data={modalData} onCancel={closeModal} onSubmit={handleModalSubmit} />
            )}
          </div>
        </div>
      )}
    </div>
  );
};

const EditForm = ({ data, onCancel, onSubmit }) => {
  const [nome, setNome] = useState(data?.nome || '');
  const [preco, setPreco] = useState(data?.preco || '');
  const [estoque, setEstoque] = useState(data?.estoque || '');

  return (
    <form onSubmit={(e) => { e.preventDefault(); onSubmit({ nome, preco: parseFloat(preco), estoque: parseInt(estoque, 10) }); }}>
      <label>Nome</label>
      <input value={nome} onChange={e => setNome(e.target.value)} required />
      <label>Preço</label>
      <input value={preco} onChange={e => setPreco(e.target.value)} required />
      <label>Estoque</label>
      <input value={estoque} onChange={e => setEstoque(e.target.value)} required />
      <div className="modal-actions">
        <button className="btn" type="button" onClick={onCancel}>Cancelar</button>
        <button className="btn primary" type="submit">Salvar</button>
      </div>
    </form>
  );
};

const EntradaForm = ({ data, onCancel, onSubmit }) => {
  const [quantidade, setQuantidade] = useState('');
  return (
    <form onSubmit={(e) => { e.preventDefault(); onSubmit({ quantidade: parseInt(quantidade, 10) }); }}>
      <p>Produto: <strong>{data?.nome}</strong></p>
      <label>Quantidade a adicionar</label>
      <input value={quantidade} onChange={e => setQuantidade(e.target.value)} required />
      <div className="modal-actions">
        <button className="btn" type="button" onClick={onCancel}>Cancelar</button>
        <button className="btn primary" type="submit">Registrar Entrada</button>
      </div>
    </form>
  );
};

const AjusteForm = ({ data, onCancel, onSubmit }) => {
  const [novoEstoque, setNovoEstoque] = useState('');
  return (
    <form onSubmit={(e) => { e.preventDefault(); onSubmit({ novoEstoque: parseInt(novoEstoque, 10) }); }}>
      <p>Produto: <strong>{data?.nome}</strong></p>
      <label>Novo estoque</label>
      <input value={novoEstoque} onChange={e => setNovoEstoque(e.target.value)} required />
      <div className="modal-actions">
        <button className="btn" type="button" onClick={onCancel}>Cancelar</button>
        <button className="btn primary" type="submit">Ajustar Estoque</button>
      </div>
    </form>
  );
};

export default Bomboniere;

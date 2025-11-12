import React, { useState, useEffect } from 'react';
import './PageStyles.css';

const Promocoes = ({ usuario }) => {
  const [produtos, setProdutos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editando, setEditando] = useState(null);
  const [formData, setFormData] = useState({
    nome: '',
    preco: '',
    estoque: ''
  });

  useEffect(() => {
    carregarProdutos();
  }, []);

  const carregarProdutos = async () => {
    try {
      setLoading(true);
      const response = await fetch('/api/produtos');
      const data = await response.json();
      setProdutos(data);
    } catch (error) {
      console.error('Erro ao carregar produtos:', error);
      alert('Erro ao carregar produtos');
    } finally {
      setLoading(false);
    }
  };

  const abrirModal = (produto = null) => {
    if (produto) {
      setEditando(produto);
      setFormData({
        nome: produto.nome,
        preco: produto.preco,
        estoque: produto.estoque
      });
    } else {
      setEditando(null);
      setFormData({
        nome: '',
        preco: '',
        estoque: ''
      });
    }
    setShowModal(true);
  };

  const fecharModal = () => {
    setShowModal(false);
    setEditando(null);
    setFormData({ nome: '', preco: '', estoque: '' });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      const payload = {
        nome: formData.nome,
        preco: parseFloat(formData.preco),
        funcionario: {
          nome: usuario.nome,
          cargo: usuario.cargo
        }
      };

      let response;
      if (editando) {
        // Modificar produto existente
        payload.estoque = parseInt(formData.estoque);
        response = await fetch(`/api/produtos/${editando.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
      } else {
        // Adicionar novo produto
        payload.estoqueInicial = parseInt(formData.estoque);
        response = await fetch('/api/produtos', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
      }

      if (response.ok) {
        alert(editando ? 'Produto modificado com sucesso!' : 'Produto adicionado com sucesso!');
        fecharModal();
        carregarProdutos();
      } else {
        const error = await response.json();
        alert(error.mensagem || 'Erro ao salvar produto');
      }
    } catch (error) {
      console.error('Erro:', error);
      alert('Erro ao salvar produto');
    }
  };

  const removerProduto = async (id) => {
    if (!window.confirm('Tem certeza que deseja remover este produto?')) {
      return;
    }

    try {
      const response = await fetch(`/api/produtos/${id}`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          nome: usuario.nome,
          cargo: usuario.cargo
        })
      });

      if (response.ok) {
        alert('Produto removido com sucesso!');
        carregarProdutos();
      } else {
        const error = await response.json();
        alert(error.mensagem || 'Erro ao remover produto');
      }
    } catch (error) {
      console.error('Erro:', error);
      alert('Erro ao remover produto');
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        <div style={{ textAlign: 'center', padding: '60px', color: 'rgba(255,255,255,0.7)' }}>
          <div className="spinner"></div>
          Carregando produtos...
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <div className="page-title-section">
          <h1 className="page-title">
            <span className="page-icon">�</span>
            Bomboniere
          </h1>
          <p className="page-subtitle">
            Gerencie produtos da bomboniere • {produtos.length} produtos
          </p>
        </div>
        <button className="btn-primary" onClick={() => abrirModal()}>
           Adicionar Produto
        </button>
      </div>

      {/* Estatísticas */}
      <div className="stats-grid-main">
        <div className="stat-card">
          <div className="stat-header">
            <span className="stat-label">Total de Produtos</span>
            <div className="stat-icon-circle purple"></div>
          </div>
          <div className="stat-value">{produtos.length}</div>
          <div className="stat-footer neutro">
            <span>cadastrados</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-header">
            <span className="stat-label">Valor Médio</span>
            <div className="stat-icon-circle green"></div>
          </div>
          <div className="stat-value">
            R$ {produtos.length > 0 ? 
              (produtos.reduce((acc, p) => acc + p.preco, 0) / produtos.length).toFixed(2) : 
              '0.00'}
          </div>
          <div className="stat-footer neutro">
            <span>por produto</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-header">
            <span className="stat-label">Estoque Total</span>
            <div className="stat-icon-circle blue"></div>
          </div>
          <div className="stat-value">
            {produtos.reduce((acc, p) => acc + p.estoque, 0)}
          </div>
          <div className="stat-footer neutro">
            <span>unidades</span>
          </div>
        </div>
      </div>

      <div className="section-container">
        <h2 className="section-title">Produtos da Bomboniere</h2>
        <p style={{ color: 'rgba(255,255,255,0.6)', fontSize: '14px', marginBottom: '20px' }}>
          Gerencie todos os produtos disponíveis para venda
        </p>

        {produtos.length === 0 ? (
          <div style={{ 
            textAlign: 'center', 
            padding: '60px', 
            background: 'rgba(30,20,60,0.4)',
            borderRadius: '16px',
            border: '1px dashed rgba(139,92,246,0.3)'
          }}>
            <div style={{ fontSize: '64px', marginBottom: '20px', opacity: 0.5 }}></div>
            <h3 style={{ color: 'white', marginBottom: '10px' }}>Nenhum produto cadastrado</h3>
            <p style={{ color: 'rgba(255,255,255,0.6)' }}>Adicione o primeiro produto à bomboniere</p>
          </div>
        ) : (
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Produto</th>
                  <th>Preço</th>
                  <th>Estoque</th>
                  <th>Status</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {produtos.map((produto) => (
                  <tr key={produto.id}>
                    <td>#{produto.id}</td>
                    <td>
                      <strong style={{ color: 'white' }}>{produto.nome}</strong>
                    </td>
                    <td>
                      <strong style={{ color: '#34D399' }}>
                        R$ {produto.preco.toFixed(2)}
                      </strong>
                    </td>
                    <td>
                      <span style={{ 
                        color: produto.estoque > 20 ? '#34D399' : produto.estoque > 0 ? '#FFA500' : '#FF6B6B'
                      }}>
                        {produto.estoque} unidades
                      </span>
                    </td>
                    <td>
                      <span className={`badge ${produto.estoque > 0 ? 'ativa' : 'inativa'}`}>
                        {produto.estoque > 0 ? 'Disponível' : 'Esgotado'}
                      </span>
                    </td>
                    <td>
                      <button 
                        className="btn-secondary" 
                        onClick={() => abrirModal(produto)}
                        style={{ padding: '6px 12px', fontSize: '12px', marginRight: '8px' }}
                      >
                         Editar
                      </button>
                      <button 
                        className="btn-danger" 
                        onClick={() => removerProduto(produto.id)}
                        style={{ padding: '6px 12px', fontSize: '12px' }}
                      >
                         Remover
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal de Adicionar/Editar Produto */}
      {showModal && (
        <div className="modal-overlay" onClick={fecharModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editando ? ' Editar Produto' : ' Adicionar Novo Produto'}</h2>
              <button className="modal-close" onClick={fecharModal}></button>
            </div>
            
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Nome do Produto *</label>
                <input
                  type="text"
                  value={formData.nome}
                  onChange={(e) => setFormData({...formData, nome: e.target.value})}
                  required
                  placeholder="Ex: Pipoca Grande"
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Preço (R$) *</label>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.preco}
                    onChange={(e) => setFormData({...formData, preco: e.target.value})}
                    required
                    min="0"
                    placeholder="18.00"
                  />
                </div>

                <div className="form-group">
                  <label>{editando ? 'Estoque Atual *' : 'Estoque Inicial *'}</label>
                  <input
                    type="number"
                    value={formData.estoque}
                    onChange={(e) => setFormData({...formData, estoque: e.target.value})}
                    required
                    min="0"
                    placeholder="50"
                  />
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn-secondary" onClick={fecharModal}>
                  Cancelar
                </button>
                <button type="submit" className="btn-primary">
                  {editando ? 'Salvar Alterações' : 'Adicionar Produto'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Promocoes;

import React, { useState, useEffect } from 'react';
import './PageStyles.css';
import { AddIcon, EditIcon, DeleteIcon, SearchIcon } from '../Icons';

const statusOpcoes = [
  { label: 'Todos os status', value: 'TODOS' },
  { label: 'Em cartaz', value: 'EM_CARTAZ' },
  { label: 'Em breve', value: 'EM_BREVE' },
  { label: 'Retirados', value: 'RETIRADO' }
];

const Filmes = ({ usuario }) => {
  const [filmes, setFilmes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editando, setEditando] = useState(null);
  const [formData, setFormData] = useState({
    titulo: '',
    sinopse: '',
    classificacaoEtaria: '',
    duracao: ''
  });
  const [filtros, setFiltros] = useState({ status: 'TODOS', busca: '' });

  const cargoUsuario = (
    usuario?.cargo ||
    (usuario?.tipo === 'ADMIN' ? 'GERENTE' : 'ATENDENTE') ||
    'GERENTE'
  ).toUpperCase();

  useEffect(() => {
    carregarFilmes({});
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const getFuncionarioPayload = () => ({
    nome: usuario?.nome || 'Administrador',
    cargo: cargoUsuario
  });

  const carregarFilmes = async (override = {}) => {
    try {
      setLoading(true);
      const nextFiltros = {
        status: override.status ?? filtros.status,
        busca: override.busca ?? filtros.busca
      };

      const params = new URLSearchParams();
      if (nextFiltros.status && nextFiltros.status !== 'TODOS') {
        params.append('status', nextFiltros.status);
      }
      if (nextFiltros.busca) {
        params.append('busca', nextFiltros.busca.trim());
      }

      const url = params.toString() ? `/api/filmes?${params.toString()}` : '/api/filmes';
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error('Erro ao carregar filmes');
      }
      const data = await response.json();
      setFilmes(data);
      setFiltros(nextFiltros);
    } catch (error) {
      console.error('Erro ao carregar filmes:', error);
      alert('Erro ao carregar filmes');
    } finally {
      setLoading(false);
    }
  };

  const abrirModal = (filme = null) => {
    if (filme) {
      setEditando(filme);
      setFormData({
        titulo: filme.titulo,
        sinopse: filme.sinopse,
        classificacaoEtaria: filme.classificacaoEtaria,
        duracao: filme.duracao
      });
    } else {
      setEditando(null);
      setFormData({
        titulo: '',
        sinopse: '',
        classificacaoEtaria: '',
        duracao: ''
      });
    }
    setShowModal(true);
  };

  const fecharModal = () => {
    setShowModal(false);
    setEditando(null);
    setFormData({
      titulo: '',
      sinopse: '',
      classificacaoEtaria: '',
      duracao: ''
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      const payload = {
        ...formData,
        duracao: parseInt(formData.duracao),
        funcionario: getFuncionarioPayload()
      };

      let response;
      if (editando) {
        // Alterar filme existente
        response = await fetch(`/api/filmes/${editando.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
      } else {
        // Adicionar novo filme
        response = await fetch('/api/filmes', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
      }

      if (response.ok) {
        alert(editando ? 'Filme alterado com sucesso!' : 'Filme adicionado com sucesso!');
        fecharModal();
        carregarFilmes({});
      } else {
        const error = await response.json();
        alert(error.mensagem || 'Erro ao salvar filme');
      }
    } catch (error) {
      console.error('Erro:', error);
      alert('Erro ao salvar filme');
    }
  };

  const removerFilme = async (id) => {
    if (!window.confirm('Tem certeza que deseja remover este filme?')) {
      return;
    }

    try {
      const response = await fetch(`/api/filmes/${id}`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(getFuncionarioPayload())
      });

      if (response.ok) {
        alert('Filme removido com sucesso!');
        carregarFilmes({});
      } else {
        const error = await response.json();
        alert(error.mensagem || 'Erro ao remover filme');
      }
    } catch (error) {
      console.error('Erro:', error);
      alert('Erro ao remover filme');
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        <div style={{ textAlign: 'center', padding: '60px', color: 'rgba(255,255,255,0.7)' }}>
          <div className="spinner"></div>
          Carregando filmes...
        </div>
      </div>
    );
  }

  const estatisticas = {
    total: filmes.length,
    emCartaz: filmes.filter(f => f.status === 'EM_CARTAZ').length,
    emBreve: filmes.filter(f => f.status === 'EM_BREVE').length,
    retirados: filmes.filter(f => f.status === 'RETIRADO').length
  };

  const handleFiltroSubmit = (e) => {
    e.preventDefault();
    carregarFilmes({});
  };

  const limparFiltros = () => {
    setFiltros({ status: 'TODOS', busca: '' });
    carregarFilmes({ status: 'TODOS', busca: '' });
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <div className="page-title-section">
          <h1 className="page-title">
            Catálogo de Filmes
          </h1>
          <p className="page-subtitle">
            Gerencie o catálogo de filmes do cinema • {filmes.length} resultados
          </p>
        </div>
        <button className="btn-primary" onClick={() => abrirModal()}>
          <AddIcon size={18} /> Adicionar Filme
        </button>
      </div>

      {/* Filtros */}
      <form className="filter-bar" onSubmit={handleFiltroSubmit}>
        <input
          type="text"
          placeholder="Buscar por título..."
          value={filtros.busca}
          onChange={(e) => setFiltros({ ...filtros, busca: e.target.value })}
          className="filter-input"
        />
        <select
          value={filtros.status}
          onChange={(e) => setFiltros({ ...filtros, status: e.target.value })}
          className="filter-select"
        >
          {statusOpcoes.map(opcao => (
            <option key={opcao.value} value={opcao.value}>{opcao.label}</option>
          ))}
        </select>
        <button type="submit" className="btn-secondary">
          <SearchIcon size={16} /> Aplicar
        </button>
        <button type="button" className="btn-tertiary" onClick={limparFiltros}>
          Limpar
        </button>
      </form>

      {/* Estatísticas */}
      <div className="stats-grid-main">
        <div className="stat-card">
          <div className="stat-header">
            <span className="stat-label">Total de Filmes</span>
            <div className="stat-icon-circle purple"></div>
          </div>
          <div className="stat-value">{estatisticas.total}</div>
          <div className="stat-footer neutro">
            <span>em cartaz</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-header">
            <span className="stat-label">Disponíveis</span>
            <div className="stat-icon-circle green"></div>
          </div>
          <div className="stat-value">
            {estatisticas.emCartaz}
          </div>
          <div className="stat-footer neutro">
            <span>ativos</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-header">
            <span className="stat-label">Em breve</span>
            <div className="stat-icon-circle blue"></div>
          </div>
          <div className="stat-value">{estatisticas.emBreve}</div>
          <div className="stat-footer neutro">
            <span>planejados</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-header">
            <span className="stat-label">Retirados</span>
            <div className="stat-icon-circle orange"></div>
          </div>
          <div className="stat-value">{estatisticas.retirados}</div>
          <div className="stat-footer neutro">
            <span>fora de cartaz</span>
          </div>
        </div>
      </div>

      {/* Lista de Filmes */}
      <div className="section-container">
        <h2 className="section-title">Filmes em Cartaz</h2>

        {filmes.length === 0 ? (
          <div style={{ 
            textAlign: 'center', 
            padding: '60px', 
            background: 'rgba(30,20,60,0.4)',
            borderRadius: '16px',
            border: '1px dashed rgba(139,92,246,0.3)'
          }}>
            <h3 style={{ color: 'white', marginBottom: '10px' }}>Nenhum filme cadastrado</h3>
            <p style={{ color: 'rgba(255,255,255,0.6)' }}>Adicione o primeiro filme ao catálogo</p>
          </div>
        ) : (
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Título</th>
                  <th>Classificação</th>
                  <th>Duração</th>
                  <th>Status</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {filmes.map((filme) => (
                  <tr key={filme.id}>
                    <td>#{filme.id}</td>
                    <td>
                      <strong style={{ color: 'white' }}>{filme.titulo}</strong>
                      <div style={{ fontSize: '12px', color: 'rgba(255,255,255,0.5)', marginTop: '4px' }}>
                        {filme.sinopse?.substring(0, 60)}...
                      </div>
                    </td>
                    <td>{filme.classificacaoEtaria}</td>
                    <td>{filme.duracao} min</td>
                    <td>
                      <span className={`badge ${filme.status === 'EM_CARTAZ' ? 'ativa' : filme.status === 'EM_BREVE' ? 'pendente' : 'inativa'}`}>
                        {filme.status}
                      </span>
                    </td>
                    <td>
                      <button 
                        className="btn-secondary" 
                        onClick={() => abrirModal(filme)}
                        style={{ padding: '6px 12px', fontSize: '12px', marginRight: '8px', display: 'inline-flex', alignItems: 'center', gap: '4px' }}
                      >
                        <EditIcon size={14} /> Editar
                      </button>
                      <button 
                        className="btn-danger" 
                        onClick={() => removerFilme(filme.id)}
                        style={{ padding: '6px 12px', fontSize: '12px', display: 'inline-flex', alignItems: 'center', gap: '4px' }}
                      >
                        <DeleteIcon size={14} /> Remover
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal de Adicionar/Editar */}
      {showModal && (
        <div className="modal-overlay" onClick={fecharModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editando ? 'Editar Filme' : 'Adicionar Novo Filme'}</h2>
              <button className="modal-close" onClick={fecharModal}>×</button>
            </div>
            
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Título *</label>
                <input
                  type="text"
                  value={formData.titulo}
                  onChange={(e) => setFormData({...formData, titulo: e.target.value})}
                  required
                  placeholder="Ex: Avatar: O Caminho da Água"
                />
              </div>

              <div className="form-group">
                <label>Sinopse *</label>
                <textarea
                  value={formData.sinopse}
                  onChange={(e) => setFormData({...formData, sinopse: e.target.value})}
                  required
                  rows="4"
                  placeholder="Descreva o filme..."
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Classificação Etária *</label>
                  <select
                    value={formData.classificacaoEtaria}
                    onChange={(e) => setFormData({...formData, classificacaoEtaria: e.target.value})}
                    required
                  >
                    <option value="">Selecione</option>
                    <option value="LIVRE">Livre</option>
                    <option value="10">10 anos</option>
                    <option value="12">12 anos</option>
                    <option value="14">14 anos</option>
                    <option value="16">16 anos</option>
                    <option value="18">18 anos</option>
                  </select>
                </div>

                <div className="form-group">
                  <label>Duração (minutos) *</label>
                  <input
                    type="number"
                    value={formData.duracao}
                    onChange={(e) => setFormData({...formData, duracao: e.target.value})}
                    required
                    min="1"
                    placeholder="120"
                  />
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn-secondary" onClick={fecharModal}>
                  Cancelar
                </button>
                <button type="submit" className="btn-primary">
                  {editando ? 'Salvar Alterações' : 'Adicionar Filme'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Filmes;

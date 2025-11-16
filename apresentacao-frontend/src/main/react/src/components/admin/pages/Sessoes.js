import React, { useState, useEffect } from 'react';
import './PageStyles.css';

const Sessoes = ({ usuario }) => {
  const [sessoes, setSessoes] = useState([]);
  const [filmes, setFilmes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editando, setEditando] = useState(null);
  const [formData, setFormData] = useState({
    filmeId: '',
    horario: '',
    sala: '1'
  });

  useEffect(() => {
    carregarDados();
  }, []);

  const carregarDados = async () => {
    try {
      setLoading(true);
      // Carregar filmes
      const resFilmes = await fetch('/api/filmes/em-cartaz');
      const dadosFilmes = await resFilmes.json();
      setFilmes(dadosFilmes);

      // Carregar sessões de todos os filmes
      const todasSessoes = [];
      for (const filme of dadosFilmes) {
        const resSessoes = await fetch(`/api/sessoes/filme/${filme.id}`);
        const sessoesFilme = await resSessoes.json();
        todasSessoes.push(...sessoesFilme.map(s => ({ ...s, filme })));
      }
      setSessoes(todasSessoes);
    } catch (err) {
      console.error('Erro ao carregar dados:', err);
    } finally {
      setLoading(false);
    }
  };

  const formatarDataHora = (dataHora) => {
    const data = new Date(dataHora);
    return data.toLocaleString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const abrirModal = (sessao = null) => {
    if (sessao) {
      setEditando(sessao);
      const dataFormatada = new Date(sessao.horario).toISOString().slice(0, 16);
      setFormData({
        filmeId: sessao.filme?.id || '',
        horario: dataFormatada,
        sala: sessao.sala || '1'
      });
    } else {
      setEditando(null);
      setFormData({
        filmeId: '',
        horario: '',
        sala: '1'
      });
    }
    setShowModal(true);
  };

  const fecharModal = () => {
    setShowModal(false);
    setEditando(null);
    setFormData({ filmeId: '', horario: '', sala: '1' });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      // Validar se o horário foi preenchido
      if (!formData.horario) {
        alert('Por favor, preencha a data e horário');
        return;
      }

      // Converter horário do datetime-local para ISO string
      const horarioISO = formData.horario.includes('T') 
        ? new Date(formData.horario).toISOString()
        : new Date(formData.horario + ':00').toISOString();

      const payload = {
        filmeId: parseInt(formData.filmeId),
        horario: horarioISO,
        sala: formData.sala
      };

      let response;
      if (editando) {
        // Modificar sessão existente
        response = await fetch(`/api/sessoes/${editando.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            novoHorario: horarioISO
          })
        });
      } else {
        // Criar nova sessão
        response = await fetch('/api/sessoes', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
      }

      if (response.ok) {
        alert(editando ? 'Sessão modificada com sucesso!' : 'Sessão criada com sucesso!');
        fecharModal();
        carregarDados();
      } else {
        const error = await response.json();
        alert(error.mensagem || 'Erro ao salvar sessão');
      }
    } catch (error) {
      console.error('Erro:', error);
      alert('Erro ao salvar sessão');
    }
  };

  const removerSessao = async (id) => {
    if (!window.confirm('Tem certeza que deseja cancelar esta sessão?')) {
      return;
    }

    try {
      const response = await fetch(`/api/sessoes/${id}`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          nome: usuario.nome,
          cargo: usuario.cargo
        })
      });

      if (response.ok) {
        alert('Sessão cancelada com sucesso!');
        carregarDados();
      } else {
        const error = await response.json();
        alert(error.mensagem || 'Erro ao cancelar sessão');
      }
    } catch (error) {
      console.error('Erro:', error);
      alert('Erro ao cancelar sessão');
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        <div style={{ textAlign: 'center', padding: '60px', color: 'rgba(255,255,255,0.7)' }}>
          <div className="spinner" style={{ margin: '0 auto 20px' }}></div>
          Carregando sessões...
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <div className="page-title-section">
          <h1 className="page-title">
            <span className="page-icon"></span>
            Gerenciar Sessões
          </h1>
          <p className="page-subtitle">
            Programação de sessões de cinema • {sessoes.length} sessões ativas
          </p>
        </div>
        <button className="btn-primary" onClick={() => abrirModal()}>
           Nova Sessão
        </button>
      </div>

      {/* Estatísticas Rápidas */}
      <div className="stats-grid-main">
        <div className="stat-card">
          <div className="stat-header">
            <span className="stat-label">Total de Sessões</span>
            <div className="stat-icon-circle purple"></div>
          </div>
          <div className="stat-value">{sessoes.length}</div>
          <div className="stat-footer neutro">
            <span>programadas</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-header">
            <span className="stat-label">Filmes Ativos</span>
            <div className="stat-icon-circle blue"></div>
          </div>
          <div className="stat-value">{filmes.length}</div>
          <div className="stat-footer neutro">
            <span>em cartaz</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-header">
            <span className="stat-label">Sessões Hoje</span>
            <div className="stat-icon-circle green"></div>
          </div>
          <div className="stat-value">
            {sessoes.filter(s => {
              const hoje = new Date().toDateString();
              const sessaoData = new Date(s.horario).toDateString();
              return hoje === sessaoData;
            }).length}
          </div>
          <div className="stat-footer neutro">
            <span>em andamento</span>
          </div>
        </div>
      </div>

      {/* Tabela de Sessões */}
      <div className="section-container">
        <h2 className="section-title">Lista de Sessões Programadas</h2>

        {sessoes.length === 0 ? (
          <div style={{ 
            textAlign: 'center', 
            padding: '60px', 
            background: 'rgba(30,20,60,0.4)',
            borderRadius: '16px',
            border: '1px dashed rgba(139,92,246,0.3)'
          }}>
            <div style={{ fontSize: '64px', marginBottom: '20px', opacity: 0.5 }}></div>
            <h3 style={{ color: 'white', marginBottom: '10px' }}>Nenhuma sessão encontrada</h3>
            <p style={{ color: 'rgba(255,255,255,0.6)' }}>Crie a primeira sessão para começar</p>
          </div>
        ) : (
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Filme</th>
                  <th>Data/Hora</th>
                  <th>Assentos</th>
                  <th>Status</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {sessoes.map((sessao) => (
                  <tr key={sessao.id}>
                    <td>#{sessao.id}</td>
                    <td>
                      <strong style={{ color: 'white' }}>
                        {sessao.filme?.titulo || 'Filme não encontrado'}
                      </strong>
                    </td>
                    <td>{formatarDataHora(sessao.horario)}</td>
                    <td>
                      {sessao.assentosDisponiveis || 0} livres / {sessao.totalAssentos || 0} total
                    </td>
                    <td>
                      <span className={`badge ${
                        sessao.status === 'DISPONIVEL' ? 'ativa' : 
                        sessao.status === 'ESGOTADA' ? 'inativa' : 'pendente'
                      }`}>
                        {sessao.status}
                      </span>
                    </td>
                    <td>
                      <button 
                        className="btn-secondary" 
                        onClick={() => abrirModal(sessao)}
                        style={{ padding: '6px 12px', fontSize: '12px', marginRight: '8px' }}
                      >
                         Modificar
                      </button>
                      <button 
                        className="btn-danger" 
                        onClick={() => removerSessao(sessao.id)}
                        style={{ padding: '6px 12px', fontSize: '12px' }}
                      >
                         Cancelar
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal de Criar/Modificar Sessão */}
      {showModal && (
        <div className="modal-overlay" onClick={fecharModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editando ? ' Modificar Sessão' : ' Criar Nova Sessão'}</h2>
              <button className="modal-close" onClick={fecharModal}></button>
            </div>
            
            <form onSubmit={handleSubmit}>
              {!editando && (
                <div className="form-group">
                  <label>Filme *</label>
                  <select
                    value={formData.filmeId}
                    onChange={(e) => setFormData({...formData, filmeId: e.target.value})}
                    required
                  >
                    <option value="">Selecione um filme</option>
                    {filmes.map(filme => (
                      <option key={filme.id} value={filme.id}>
                        {filme.titulo} ({filme.duracao} min)
                      </option>
                    ))}
                  </select>
                </div>
              )}

              <div className="form-group">
                <label>Data e Horário *</label>
                <input
                  type="datetime-local"
                  value={formData.horario}
                  onChange={(e) => setFormData({...formData, horario: e.target.value})}
                  required
                />
              </div>

              {!editando && (
                <div className="form-group">
                  <label>Sala *</label>
                  <select
                    value={formData.sala}
                    onChange={(e) => setFormData({...formData, sala: e.target.value})}
                    required
                  >
                    <option value="1">Sala 1</option>
                    <option value="2">Sala 2</option>
                    <option value="3">Sala 3</option>
                    <option value="4">Sala 4</option>
                  </select>
                </div>
              )}

              <div className="modal-footer">
                <button type="button" className="btn-secondary" onClick={fecharModal}>
                  Cancelar
                </button>
                <button type="submit" className="btn-primary">
                  {editando ? 'Salvar Alterações' : 'Criar Sessão'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Sessoes;

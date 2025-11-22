import React, { useState, useEffect } from 'react';
import './PageStyles.css';
import { AddIcon, DeleteIcon, SearchIcon, ViewIcon, SaveIcon, CancelIcon } from '../Icons';

const Programacao = ({ usuario }) => {
  const [programacoes, setProgramacoes] = useState([]);
  const [sessoes, setSessoes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    periodoInicio: '',
    periodoFim: '',
    sessaoIds: []
  });

  const cargoUsuario = (
    usuario?.cargo ||
    (usuario?.tipo === 'ADMIN' ? 'GERENTE' : 'ATENDENTE') ||
    'GERENTE'
  ).toUpperCase();

  useEffect(() => {
    carregarDados();
  }, []);

  const carregarDados = async () => {
    try {
      setLoading(true);
      const [resProgramacoes, resSessoes] = await Promise.all([
        fetch('/api/programacoes'),
        fetch('/api/sessoes?status=DISPONIVEL')
      ]);

      const dataProgramacoes = await resProgramacoes.json();
      const dataSessoes = await resSessoes.json();

      setProgramacoes(Array.isArray(dataProgramacoes) ? dataProgramacoes : []);
      setSessoes(Array.isArray(dataSessoes) ? dataSessoes : []);
    } catch (error) {
      console.error('Erro ao carregar dados:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatarData = (data) => {
    if (!data) return '-';
    return new Date(data).toLocaleDateString('pt-BR');
  };

  const abrirModal = () => {
    const hoje = new Date();
    const proximaSemana = new Date(hoje);
    proximaSemana.setDate(proximaSemana.getDate() + 7);

    setFormData({
      periodoInicio: hoje.toISOString().split('T')[0],
      periodoFim: proximaSemana.toISOString().split('T')[0],
      sessaoIds: []
    });
    setShowModal(true);
  };

  const fecharModal = () => {
    setShowModal(false);
    setFormData({ periodoInicio: '', periodoFim: '', sessaoIds: [] });
  };

  const toggleSessao = (sessaoId) => {
    setFormData(prev => {
      const ids = prev.sessaoIds.includes(sessaoId)
        ? prev.sessaoIds.filter(id => id !== sessaoId)
        : [...prev.sessaoIds, sessaoId];
      return { ...prev, sessaoIds: ids };
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (formData.sessaoIds.length === 0) {
      alert('Selecione pelo menos uma sessão');
      return;
    }

    try {
      const payload = {
        periodoInicio: formData.periodoInicio,
        periodoFim: formData.periodoFim,
        sessaoIds: formData.sessaoIds,
        funcionario: {
          nome: usuario?.nome || 'Administrador',
          cargo: cargoUsuario
        }
      };

      const response = await fetch('/api/programacoes', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        alert('Programação criada com sucesso!');
        fecharModal();
        carregarDados();
      } else {
        const error = await response.json();
        alert(error.mensagem || 'Erro ao criar programação');
      }
    } catch (error) {
      console.error('Erro:', error);
      alert('Erro ao criar programação');
    }
  };

  // Filtra sessões disponíveis dentro do período selecionado
  const sessoesFiltradas = sessoes.filter(s => {
    if (!formData.periodoInicio || !formData.periodoFim) return true;
    const horario = new Date(s.horario);
    const inicio = new Date(formData.periodoInicio);
    const fim = new Date(formData.periodoFim);
    fim.setHours(23, 59, 59);
    return horario >= inicio && horario <= fim;
  });

  if (loading) {
    return (
      <div className="page-container">
        <div style={{ textAlign: 'center', padding: '60px', color: 'rgba(255,255,255,0.7)' }}>
          <div className="spinner"></div>
          Carregando programações...
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <div className="page-title-section">
          <h1 className="page-title">Programação Semanal</h1>
          <p className="page-subtitle">Gerencie a programação do cinema • {programacoes.length} programações</p>
        </div>
        <button className="btn-primary" onClick={abrirModal}>
          <AddIcon size={18} /> Nova Programação
        </button>
      </div>

      <div className="stats-grid-main">
        <div className="stat-card">
          <div className="stat-header">
            <span className="stat-label">Total de Programações</span>
            <div className="stat-icon-circle purple"></div>
          </div>
          <div className="stat-value">{programacoes.length}</div>
        </div>
        <div className="stat-card">
          <div className="stat-header">
            <span className="stat-label">Sessões Disponíveis</span>
            <div className="stat-icon-circle green"></div>
          </div>
          <div className="stat-value">{sessoes.length}</div>
        </div>
      </div>

      <div className="section-container">
        <h2 className="section-title">Programações Cadastradas</h2>

        {programacoes.length === 0 ? (
          <div style={{
            textAlign: 'center',
            padding: '60px',
            background: 'rgba(30,20,60,0.4)',
            borderRadius: '16px',
            border: '1px dashed rgba(139,92,246,0.3)'
          }}>
            <h3 style={{ color: 'white', marginBottom: '10px' }}>Nenhuma programação cadastrada</h3>
            <p style={{ color: 'rgba(255,255,255,0.6)' }}>Crie a primeira programação semanal</p>
          </div>
        ) : (
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Período</th>
                  <th>Sessões</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {programacoes.map((prog) => (
                  <tr key={prog.id}>
                    <td>#{prog.id}</td>
                    <td>
                      <strong style={{ color: 'white' }}>
                        {formatarData(prog.periodoInicio)} a {formatarData(prog.periodoFim)}
                      </strong>
                    </td>
                    <td>
                      <div>
                        <strong>{prog.quantidadeSessoes || prog.sessoes?.length || 0}</strong> sessões
                        {prog.sessoes && prog.sessoes.length > 0 && (
                          <div style={{ fontSize: '12px', color: 'rgba(255,255,255,0.6)', marginTop: '4px' }}>
                            {prog.sessoes.slice(0, 3).map(s => s.filmeTitulo || `Sessão #${s.id}`).join(', ')}
                            {prog.sessoes.length > 3 && ` +${prog.sessoes.length - 3} mais`}
                          </div>
                        )}
                      </div>
                    </td>
                    <td>
                      <div className="table-actions">
                        <button className="btn-secondary" style={{ display: 'inline-flex', alignItems: 'center', gap: '6px' }}>
                          <ViewIcon size={14} /> Detalhes
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal de Nova Programação */}
      {showModal && (
        <div className="modal-overlay" onClick={fecharModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '700px' }}>
            <div className="modal-header">
              <h2>Nova Programação Semanal</h2>
              <button className="modal-close" onClick={fecharModal}>×</button>
            </div>

            <form onSubmit={handleSubmit}>
              <div className="form-row" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div className="form-group">
                  <label>Data Início *</label>
                  <input
                    type="date"
                    value={formData.periodoInicio}
                    onChange={(e) => setFormData({ ...formData, periodoInicio: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Data Fim *</label>
                  <input
                    type="date"
                    value={formData.periodoFim}
                    onChange={(e) => setFormData({ ...formData, periodoFim: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label>Sessões Disponíveis ({formData.sessaoIds.length} selecionadas)</label>
                <div style={{
                  maxHeight: '300px',
                  overflowY: 'auto',
                  border: '1px solid rgba(139,92,246,0.3)',
                  borderRadius: '8px',
                  padding: '12px'
                }}>
                  {sessoesFiltradas.length === 0 ? (
                    <p style={{ color: 'rgba(255,255,255,0.6)', textAlign: 'center', padding: '20px' }}>
                      Nenhuma sessão disponível no período selecionado
                    </p>
                  ) : (
                    sessoesFiltradas.map(sessao => (
                      <label
                        key={sessao.id}
                        style={{
                          display: 'flex',
                          alignItems: 'center',
                          gap: '12px',
                          padding: '10px',
                          borderRadius: '6px',
                          cursor: 'pointer',
                          background: formData.sessaoIds.includes(sessao.id) ? 'rgba(139,92,246,0.2)' : 'transparent',
                          marginBottom: '8px',
                          border: formData.sessaoIds.includes(sessao.id) ? '1px solid rgba(139,92,246,0.5)' : '1px solid transparent'
                        }}
                      >
                        <input
                          type="checkbox"
                          checked={formData.sessaoIds.includes(sessao.id)}
                          onChange={() => toggleSessao(sessao.id)}
                        />
                        <div style={{ flex: 1 }}>
                          <strong style={{ color: 'white' }}>
                            {sessao.filme?.titulo || sessao.filmeTitulo || `Filme #${sessao.filmeId}`}
                          </strong>
                          <div style={{ fontSize: '12px', color: 'rgba(255,255,255,0.6)' }}>
                            {sessao.sala} • {new Date(sessao.horario).toLocaleString('pt-BR')}
                          </div>
                        </div>
                        <span className={`badge ${sessao.status === 'DISPONIVEL' ? 'ativa' : 'inativa'}`}>
                          {sessao.status}
                        </span>
                      </label>
                    ))
                  )}
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn-secondary" onClick={fecharModal}>
                  <CancelIcon size={16} /> Cancelar
                </button>
                <button type="submit" className="btn-primary" disabled={formData.sessaoIds.length === 0}>
                  <SaveIcon size={16} /> Criar Programação
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Programacao;

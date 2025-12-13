import React, { useState, useEffect } from 'react';
import './PageStyles.css';
import { AddIcon, DeleteIcon, SearchIcon, ViewIcon, SaveIcon, CancelIcon } from '../Icons';
import ModalPortal from './ModalPortal';

const Programacao = ({ usuario }) => {
  const [programacoes, setProgramacoes] = useState([]);
  const [sessoes, setSessoes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [showDetalhesModal, setShowDetalhesModal] = useState(false);
  const [programacaoSelecionada, setProgramacaoSelecionada] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [filtroStatus, setFiltroStatus] = useState('TODAS');
  const [buscaTexto, setBuscaTexto] = useState('');
  const [formData, setFormData] = useState({
    periodoInicio: '',
    periodoFim: '',
    sessaoIds: []
  });
  const [erro, setErro] = useState(null);

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
    setErro(null);
    setShowModal(true);
  };

  const fecharModal = () => {
    if (submitting) return; // Não fecha se estiver salvando
    setShowModal(false);
    setFormData({ periodoInicio: '', periodoFim: '', sessaoIds: [] });
    setErro(null);
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
    setErro(null);

    // Validações do lado do cliente
    if (formData.sessaoIds.length === 0) {
      setErro('Selecione pelo menos uma sessão para a programação');
      return;
    }

    const inicio = new Date(formData.periodoInicio);
    const fim = new Date(formData.periodoFim);
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);

    if (inicio > fim) {
      setErro('A data de início deve ser anterior à data de fim');
      return;
    }

    if (fim < hoje) {
      setErro('Não é possível criar programação para períodos passados');
      return;
    }

    try {
      setSubmitting(true);
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
        alert('✅ Programação criada com sucesso!');
        fecharModal();
        carregarDados();
      } else {
        const error = await response.json();
        setErro(error.mensagem || 'Erro ao criar programação');
      }
    } catch (error) {
      console.error('Erro:', error);
      setErro('Erro de conexão. Tente novamente.');
    } finally {
      setSubmitting(false);
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

  // Filtra programações com base na busca e filtros
  const programacoesFiltradas = programacoes.filter(prog => {
    // Filtro de busca por texto
    if (buscaTexto) {
      const termo = buscaTexto.toLowerCase();
      const contemTexto =
        prog.id?.toString().includes(termo) ||
        prog.sessoes?.some(s =>
          s.filmeTitulo?.toLowerCase().includes(termo) ||
          s.sala?.toLowerCase().includes(termo)
        );
      if (!contemTexto) return false;
    }

    // Filtro por status (ativa, futura, passada)
    if (filtroStatus !== 'TODAS') {
      const hoje = new Date();
      hoje.setHours(0, 0, 0, 0);
      const inicio = new Date(prog.periodoInicio);
      const fim = new Date(prog.periodoFim);

      if (filtroStatus === 'ATIVA' && (inicio > hoje || fim < hoje)) return false;
      if (filtroStatus === 'FUTURA' && inicio <= hoje) return false;
      if (filtroStatus === 'PASSADA' && fim >= hoje) return false;
    }

    return true;
  });

  const obterStatusProgramacao = (prog) => {
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    const inicio = new Date(prog.periodoInicio);
    const fim = new Date(prog.periodoFim);

    if (fim < hoje) return { texto: 'Finalizada', classe: 'inativa' };
    if (inicio > hoje) return { texto: 'Futura', classe: 'pendente' };
    return { texto: 'Em Exibição', classe: 'ativa' };
  };

  const abrirDetalhes = (prog) => {
    setProgramacaoSelecionada(prog);
    setShowDetalhesModal(true);
  };

  const fecharDetalhes = () => {
    setShowDetalhesModal(false);
    setProgramacaoSelecionada(null);
  };

  const removerProgramacao = async (id) => {
    if (!window.confirm('Tem certeza que deseja remover esta programação? Esta ação não pode ser desfeita.')) {
      return;
    }

    try {
      const response = await fetch(`/api/programacoes/${id}`, {
        method: 'DELETE'
      });

      if (response.ok) {
        alert('✅ Programação removida com sucesso!');
        carregarDados();
      } else {
        const error = await response.json();
        alert('❌ ' + (error.mensagem || 'Erro ao remover programação'));
      }
    } catch (error) {
      console.error('Erro:', error);
      alert('❌ Erro de conexão. Tente novamente.');
    }
  };

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
        <div className="stat-card">
          <div className="stat-header">
            <span className="stat-label">Em Exibição</span>
            <div className="stat-icon-circle blue"></div>
          </div>
          <div className="stat-value">
            {programacoes.filter(p => {
              const hoje = new Date();
              hoje.setHours(0, 0, 0, 0);
              const inicio = new Date(p.periodoInicio);
              const fim = new Date(p.periodoFim);
              return inicio <= hoje && fim >= hoje;
            }).length}
          </div>
        </div>
      </div>

      <div className="section-container">
        <h2 className="section-title">Programações Cadastradas</h2>

        {/* Filtros e Busca */}
        <div style={{ display: 'flex', gap: '12px', marginBottom: '20px', flexWrap: 'wrap' }}>
          <div style={{ flex: 1, minWidth: '250px' }}>
            <div style={{ position: 'relative' }}>
              <SearchIcon size={16} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'rgba(255,255,255,0.4)' }} />
              <input
                type="text"
                placeholder="Buscar por ID, filme ou sala..."
                value={buscaTexto}
                onChange={(e) => setBuscaTexto(e.target.value)}
                style={{
                  width: '100%',
                  padding: '10px 12px 10px 40px',
                  background: 'rgba(30,20,60,0.4)',
                  border: '1px solid rgba(139,92,246,0.3)',
                  borderRadius: '8px',
                  color: 'white',
                  fontSize: '14px'
                }}
              />
            </div>
          </div>
          <div style={{ display: 'flex', gap: '8px' }}>
            {['TODAS', 'ATIVA', 'FUTURA', 'PASSADA'].map(status => (
              <button
                key={status}
                onClick={() => setFiltroStatus(status)}
                className={filtroStatus === status ? 'btn-primary' : 'btn-secondary'}
                style={{ fontSize: '13px', padding: '8px 16px' }}
              >
                {status === 'TODAS' ? 'Todas' : status === 'ATIVA' ? 'Em Exibição' : status === 'FUTURA' ? 'Futuras' : 'Finalizadas'}
              </button>
            ))}
          </div>
        </div>

        {programacoesFiltradas.length === 0 && programacoes.length > 0 ? (
          <div style={{
            textAlign: 'center',
            padding: '60px',
            background: 'rgba(30,20,60,0.4)',
            borderRadius: '16px',
            border: '1px dashed rgba(139,92,246,0.3)'
          }}>
            <h3 style={{ color: 'white', marginBottom: '10px' }}>Nenhuma programação encontrada</h3>
            <p style={{ color: 'rgba(255,255,255,0.6)' }}>Tente ajustar os filtros ou termo de busca</p>
          </div>
        ) : programacoes.length === 0 ? (
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
                  <th>Status</th>
                  <th>Período</th>
                  <th>Sessões</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {programacoesFiltradas.map((prog) => {
                  const status = obterStatusProgramacao(prog);
                  return (
                    <tr key={prog.id}>
                      <td>#{prog.id}</td>
                      <td>
                        <span className={`badge ${status.classe}`}>
                          {status.texto}
                        </span>
                      </td>
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
                          <button 
                            className="btn-secondary" 
                            onClick={() => abrirDetalhes(prog)}
                            style={{ display: 'inline-flex', alignItems: 'center', gap: '6px' }}
                          >
                            <ViewIcon size={14} /> Detalhes
                          </button>
                          <button
                            className="btn-secondary"
                            onClick={() => removerProgramacao(prog.id)}
                            style={{
                              display: 'inline-flex',
                              alignItems: 'center',
                              gap: '6px',
                              background: 'rgba(239, 68, 68, 0.1)',
                              border: '1px solid rgba(239, 68, 68, 0.3)',
                              color: '#ef4444'
                            }}
                          >
                            <DeleteIcon size={14} /> Remover
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal de Nova Programação */}
      <ModalPortal isOpen={showModal}>
        <div className="modal-overlay" onClick={fecharModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '700px' }}>
            <div className="modal-header">
              <h2>Nova Programação Semanal</h2>
              <button className="modal-close" onClick={fecharModal}>×</button>
            </div>

            <form onSubmit={handleSubmit}>
              {erro && (
                <div style={{
                  padding: '12px 16px',
                  background: 'rgba(239, 68, 68, 0.1)',
                  border: '1px solid rgba(239, 68, 68, 0.3)',
                  borderRadius: '8px',
                  color: '#ef4444',
                  marginBottom: '20px',
                  fontSize: '14px'
                }}>
                  ⚠️ {erro}
                </div>
              )}

              <div className="form-row" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div className="form-group">
                  <label>Data Início *</label>
                  <input
                    type="date"
                    value={formData.periodoInicio}
                    onChange={(e) => {
                      setFormData({ ...formData, periodoInicio: e.target.value });
                      setErro(null);
                    }}
                    required
                    disabled={submitting}
                  />
                </div>
                <div className="form-group">
                  <label>Data Fim *</label>
                  <input
                    type="date"
                    value={formData.periodoFim}
                    onChange={(e) => {
                      setFormData({ ...formData, periodoFim: e.target.value });
                      setErro(null);
                    }}
                    required
                    disabled={submitting}
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
                <button type="button" className="btn-secondary" onClick={fecharModal} disabled={submitting}>
                  <CancelIcon size={16} /> Cancelar
                </button>
                <button
                  type="submit"
                  className="btn-primary"
                  disabled={formData.sessaoIds.length === 0 || submitting}
                  style={{ position: 'relative' }}
                >
                  {submitting ? (
                    <>
                      <div className="spinner" style={{ width: '16px', height: '16px', borderWidth: '2px' }}></div>
                      Criando...
                    </>
                  ) : (
                    <>
                      <SaveIcon size={16} /> Criar Programação
                    </>
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      </ModalPortal>

      {/* Modal de Detalhes da Programação */}
      <ModalPortal isOpen={showDetalhesModal}>
        <div className="modal-overlay" onClick={fecharDetalhes}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '700px' }}>
            <div className="modal-header">
              <h2>Detalhes da Programação #{programacaoSelecionada?.id}</h2>
              <button className="modal-close" onClick={fecharDetalhes}>×</button>
            </div>

            {programacaoSelecionada && (
              <div style={{ padding: '20px' }}>
                {/* Informações do Período */}
                <div style={{
                  background: 'rgba(139,92,246,0.1)',
                  border: '1px solid rgba(139,92,246,0.3)',
                  borderRadius: '8px',
                  padding: '16px',
                  marginBottom: '20px'
                }}>
                  <h3 style={{ color: 'white', marginBottom: '12px', fontSize: '16px' }}>Período</h3>
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                    <div>
                      <div style={{ fontSize: '12px', color: 'rgba(255,255,255,0.6)', marginBottom: '4px' }}>Data Início</div>
                      <div style={{ color: 'white', fontWeight: '500' }}>{formatarData(programacaoSelecionada.periodoInicio)}</div>
                    </div>
                    <div>
                      <div style={{ fontSize: '12px', color: 'rgba(255,255,255,0.6)', marginBottom: '4px' }}>Data Fim</div>
                      <div style={{ color: 'white', fontWeight: '500' }}>{formatarData(programacaoSelecionada.periodoFim)}</div>
                    </div>
                  </div>
                  <div style={{ marginTop: '12px' }}>
                    <span className={`badge ${obterStatusProgramacao(programacaoSelecionada).classe}`}>
                      {obterStatusProgramacao(programacaoSelecionada).texto}
                    </span>
                  </div>
                </div>

                {/* Lista de Sessões */}
                <div>
                  <h3 style={{ color: 'white', marginBottom: '12px', fontSize: '16px' }}>
                    Sessões ({programacaoSelecionada.sessoes?.length || 0})
                  </h3>
                  <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
                    {programacaoSelecionada.sessoes && programacaoSelecionada.sessoes.length > 0 ? (
                      programacaoSelecionada.sessoes.map((sessao) => (
                        <div
                          key={sessao.id}
                          style={{
                            background: 'rgba(30,20,60,0.4)',
                            border: '1px solid rgba(139,92,246,0.3)',
                            borderRadius: '8px',
                            padding: '12px',
                            marginBottom: '12px'
                          }}
                        >
                          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: '8px' }}>
                            <div style={{ flex: 1 }}>
                              <div style={{ color: 'white', fontWeight: '500', marginBottom: '4px' }}>
                                {sessao.filmeTitulo || `Filme #${sessao.filmeId}`}
                              </div>
                              <div style={{ fontSize: '12px', color: 'rgba(255,255,255,0.6)' }}>
                                Sessão #{sessao.id} • {sessao.sala}
                              </div>
                            </div>
                            <span className={`badge ${sessao.status === 'DISPONIVEL' ? 'ativa' : sessao.status === 'ESGOTADA' ? 'inativa' : 'cancelada'}`}>
                              {sessao.status}
                            </span>
                          </div>
                          <div style={{ fontSize: '13px', color: 'rgba(255,255,255,0.7)' }}>
                            📅 {new Date(sessao.horario).toLocaleString('pt-BR', {
                              day: '2-digit',
                              month: '2-digit',
                              year: 'numeric',
                              hour: '2-digit',
                              minute: '2-digit'
                            })}
                          </div>
                        </div>
                      ))
                    ) : (
                      <p style={{ color: 'rgba(255,255,255,0.6)', textAlign: 'center', padding: '20px' }}>
                        Nenhuma sessão associada
                      </p>
                    )}
                  </div>
                </div>
              </div>
            )}

            <div className="modal-footer">
              <button className="btn-secondary" onClick={fecharDetalhes}>
                Fechar
              </button>
            </div>
          </div>
        </div>
      </ModalPortal>
    </div>
  );
};

export default Programacao;

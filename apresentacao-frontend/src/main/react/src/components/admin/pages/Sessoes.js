import React, { useState, useEffect, useMemo } from 'react';
import './PageStyles.css';
import { AddIcon, EditIcon, DeleteIcon, SearchIcon, ViewIcon, SaveIcon, CancelIcon } from '../Icons';

const statusOptions = [
  { label: 'Todos os status', value: 'TODOS' },
  { label: 'Disponíveis', value: 'DISPONIVEL' },
  { label: 'Esgotadas', value: 'ESGOTADA' },
  { label: 'Canceladas', value: 'CANCELADA' }
];

const estrategiasRemarcacao = [
  { label: 'Remarcar todos os ingressos', value: 'MASSA' },
  { label: 'Selecionar assentos', value: 'INDIVIDUAL' }
];

const Sessoes = ({ usuario }) => {
  const cargoUsuario = useMemo(() => (
    usuario?.cargo ||
    (usuario?.tipo === 'ADMIN' ? 'GERENTE' : 'ATENDENTE') ||
    'GERENTE'
  ).toUpperCase(), [usuario]);

  const getFuncionarioPayload = () => ({
    nome: usuario?.nome || 'Administrador',
    cargo: cargoUsuario
  });

  const [sessoes, setSessoes] = useState([]);
  const [filmes, setFilmes] = useState([]);
  const [indicadores, setIndicadores] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [showRemarcarModal, setShowRemarcarModal] = useState(false);
  const [sessaoSelecionada, setSessaoSelecionada] = useState(null);
  const [editando, setEditando] = useState(null);
  const [formData, setFormData] = useState({
    filmeId: '',
    horario: '',
    sala: 'Sala 1',
    capacidadeSala: 100
  });
  const [remarcacaoForm, setRemarcacaoForm] = useState({
    novoHorario: '',
    estrategia: 'MASSA',
    assentos: ''
  });
  const [filtros, setFiltros] = useState({
    filmeId: 'TODOS',
    status: 'TODOS',
    apenasAtivas: true
  });

  useEffect(() => {
    carregarDados({});
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const carregarDados = async (overrideFiltros = {}) => {
    try {
      setLoading(true);
      const filtrosAplicados = { ...filtros, ...overrideFiltros };
      const params = new URLSearchParams();
      if (filtrosAplicados.filmeId && filtrosAplicados.filmeId !== 'TODOS') {
        params.append('filmeId', filtrosAplicados.filmeId);
      }
      if (filtrosAplicados.status && filtrosAplicados.status !== 'TODOS') {
        params.append('status', filtrosAplicados.status);
      }
      if (filtrosAplicados.apenasAtivas) {
        params.append('apenasAtivas', 'true');
      }

      const [resFilmes, resSessoes, resIndicadores] = await Promise.all([
        fetch('/api/filmes/em-cartaz'),
        fetch(params.toString() ? `/api/sessoes?${params}` : '/api/sessoes'),
        fetch('/api/sessoes/indicadores')
      ]);

      if (!resFilmes.ok || !resSessoes.ok || !resIndicadores.ok) {
        throw new Error('Falha ao buscar dados de sessões');
      }

      const dadosFilmes = await resFilmes.json();
      const dadosSessoes = await resSessoes.json();
      const dadosIndicadores = await resIndicadores.json();

      setFilmes(dadosFilmes);
      setSessoes(dadosSessoes);
      setIndicadores(dadosIndicadores);
      setFiltros(filtrosAplicados);
    } catch (err) {
      console.error('Erro ao carregar dados:', err);
      alert('Erro ao carregar dados de sessões.');
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
        filmeId: sessao.filmeId,
        horario: dataFormatada,
        sala: sessao.sala,
        capacidadeSala: sessao.capacidade
      });
    } else {
      setEditando(null);
      setFormData({
        filmeId: '',
        horario: '',
        sala: 'Sala 1',
        capacidadeSala: 100
      });
    }
    setShowModal(true);
  };

  const abrirRemarcacaoModal = (sessao) => {
    setSessaoSelecionada(sessao);
    setRemarcacaoForm({
      novoHorario: new Date(sessao.horario).toISOString().slice(0, 16),
      estrategia: 'MASSA',
      assentos: ''
    });
    setShowRemarcarModal(true);
  };

  const fecharModal = () => {
    setShowModal(false);
    setEditando(null);
    setFormData({ filmeId: '', horario: '', sala: 'Sala 1', capacidadeSala: 100 });
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

      const payload = editando
        ? {
            novoHorario: horarioISO,
            novaSala: formData.sala,
            funcionario: getFuncionarioPayload()
          }
        : {
            filmeId: parseInt(formData.filmeId),
            horario: horarioISO,
            sala: formData.sala,
            capacidadeSala: parseInt(formData.capacidadeSala, 10) || 100,
            funcionario: getFuncionarioPayload()
          };

      const response = await fetch(editando ? `/api/sessoes/${editando.id}` : '/api/sessoes', {
        method: editando ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

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
          funcionario: getFuncionarioPayload(),
          nomeFuncionario: usuario?.nome,
          cargoFuncionario: cargoUsuario
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

  const handleRemarcacaoSubmit = async (e) => {
    e.preventDefault();
    if (!sessaoSelecionada) return;

    try {
      if (!remarcacaoForm.novoHorario) {
        alert('Informe o novo horário');
        return;
      }

      const payload = {
        novoHorario: new Date(remarcacaoForm.novoHorario).toISOString(),
        estrategia: remarcacaoForm.estrategia,
        assentos: remarcacaoForm.assentos
          ? remarcacaoForm.assentos.split(',').map(a => a.trim()).filter(Boolean)
          : [],
        funcionario: getFuncionarioPayload()
      };

      const response = await fetch(`/api/sessoes/${sessaoSelecionada.id}/remarcar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        alert('Ingressos remarcados com sucesso!');
        setShowRemarcarModal(false);
        setSessaoSelecionada(null);
        carregarDados({});
      } else {
        const error = await response.json();
        alert(error.mensagem || 'Erro ao remarcar ingressos');
      }
    } catch (error) {
      console.error('Erro ao remarcar ingressos:', error);
      alert('Erro ao remarcar ingressos');
    }
  };

  const handleFiltroChange = (campo, valor) => {
    const novosFiltros = { ...filtros, [campo]: valor };
    carregarDados(novosFiltros);
  };

  const resetFiltros = () => {
    const defaultFiltros = { filmeId: 'TODOS', status: 'TODOS', apenasAtivas: true };
    carregarDados(defaultFiltros);
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
            Programação completa do cinema • {sessoes.length} sessões listadas
          </p>
        </div>
        <button className="btn-primary" onClick={() => abrirModal()}>
          <AddIcon size={18} /> Nova Sessão
        </button>
      </div>

      {indicadores && (
        <div className="stats-grid-main">
          <div className="stat-card">
            <div className="stat-header">
              <span className="stat-label">Total de Sessões</span>
              <div className="stat-icon-circle purple"></div>
            </div>
            <div className="stat-value">{indicadores.total}</div>
            <div className="stat-footer neutro">
              <span>{indicadores.ativas} ativas • {indicadores.canceladas} canceladas</span>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-header">
              <span className="stat-label">Sessões Hoje</span>
              <div className="stat-icon-circle green"></div>
            </div>
            <div className="stat-value">{indicadores.sessoesHoje}</div>
            <div className="stat-footer neutro">
              <span>{indicadores.sessoesSemana} esta semana</span>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-header">
              <span className="stat-label">Ocupação Média</span>
              <div className="stat-icon-circle orange"></div>
            </div>
            <div className="stat-value">{(indicadores.ocupacaoMedia * 100).toFixed(0)}%</div>
            <div className="stat-footer neutro">
              <span>{indicadores.ingressosReservados} reservas / {indicadores.ingressosDisponiveis} livres</span>
            </div>
          </div>
        </div>
      )}

      <form className="filter-bar" onSubmit={(e) => e.preventDefault()}>
        <select
          className="filter-select"
          value={filtros.filmeId}
          onChange={(e) => handleFiltroChange('filmeId', e.target.value)}
        >
          <option value="TODOS">Todos os filmes</option>
          {filmes.map(filme => (
            <option key={filme.id} value={filme.id}>{filme.titulo}</option>
          ))}
        </select>

        <select
          className="filter-select"
          value={filtros.status}
          onChange={(e) => handleFiltroChange('status', e.target.value)}
        >
          {statusOptions.map(opt => (
            <option key={opt.value} value={opt.value}>{opt.label}</option>
          ))}
        </select>

        <label className="filter-toggle">
          <input
            type="checkbox"
            checked={filtros.apenasAtivas}
            onChange={(e) => handleFiltroChange('apenasAtivas', e.target.checked)}
          />
          <span>Mostrar apenas sessões ativas</span>
        </label>

        <button type="button" className="btn-tertiary" onClick={resetFiltros}>
          <SearchIcon size={16} /> Limpar filtros
        </button>
      </form>

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
                  <th>Sala</th>
                  <th>Data/Hora</th>
                  <th>Capacidade</th>
                  <th>Ocupação</th>
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
                        {sessao.filmeTitulo || 'Filme não encontrado'}
                      </strong>
                    </td>
                    <td>{sessao.sala}</td>
                    <td>{formatarDataHora(sessao.horario)}</td>
                    <td>{sessao.capacidade} lugares</td>
                    <td>
                      <div style={{ display: 'flex', flexDirection: 'column' }}>
                        <strong>{(sessao.ocupacao * 100).toFixed(0)}%</strong>
                        <small style={{ color: 'rgba(255,255,255,0.6)' }}>
                          {sessao.assentosReservados} vend • {sessao.assentosDisponiveis} disp
                        </small>
                      </div>
                    </td>
                    <td>
                      <span className={`badge ${
                        sessao.status === 'DISPONIVEL' ? 'ativa' : 
                        sessao.status === 'ESGOTADA' ? 'inativa' : 'cancelada'
                      }`}>
                        {sessao.status}
                      </span>
                    </td>
                    <td>
                      <div className="table-actions">
                        <button 
                          className="btn-secondary" 
                          onClick={() => abrirModal(sessao)}
                          style={{display: 'inline-flex', alignItems: 'center', gap: '6px'}}
                        >
                          <EditIcon size={14} /> Modificar
                        </button>
                        <button 
                          className="btn-tertiary"
                          onClick={() => abrirRemarcacaoModal(sessao)}
                          style={{display: 'inline-flex', alignItems: 'center', gap: '6px'}}
                        >
                          <ViewIcon size={14} /> Remarcar
                        </button>
                        <button 
                          className="btn-danger" 
                          onClick={() => removerSessao(sessao.id)}
                          style={{display: 'inline-flex', alignItems: 'center', gap: '6px'}}
                        >
                          <DeleteIcon size={14} /> Cancelar
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
                <>
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
                </>
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

              <div className="modal-footer">
                <button type="button" className="btn-secondary" onClick={fecharModal}>
                  <CancelIcon size={16} /> Cancelar
                </button>
                <button type="submit" className="btn-primary">
                  <SaveIcon size={16} /> {editando ? 'Salvar Alterações' : 'Criar Sessão'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Remarcar Sessão */}
      {showRemarcarModal && sessaoSelecionada && (
        <div className="modal-overlay" onClick={() => setShowRemarcarModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Remarcar Ingressos da Sessão #{sessaoSelecionada.id}</h2>
              <button className="modal-close" onClick={() => setShowRemarcarModal(false)}></button>
            </div>

            <form onSubmit={handleRemarcacaoSubmit}>
              <div className="form-group">
                <label>Novo Horário *</label>
                <input
                  type="datetime-local"
                  value={remarcacaoForm.novoHorario}
                  onChange={(e) => setRemarcacaoForm({ ...remarcacaoForm, novoHorario: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label>Estrategia de Remarcação *</label>
                <select
                  value={remarcacaoForm.estrategia}
                  onChange={(e) => setRemarcacaoForm({ ...remarcacaoForm, estrategia: e.target.value })}
                >
                  {estrategiasRemarcacao.map(opt => (
                    <option key={opt.value} value={opt.value}>{opt.label}</option>
                  ))}
                </select>
              </div>

              {remarcacaoForm.estrategia === 'INDIVIDUAL' && (
                <div className="form-group">
                  <label>Assentos (separados por vírgula)</label>
                  <textarea
                    placeholder="A1, A2, B3"
                    value={remarcacaoForm.assentos}
                    onChange={(e) => setRemarcacaoForm({ ...remarcacaoForm, assentos: e.target.value })}
                  />
                </div>
              )}

              <div className="modal-footer">
                <button type="button" className="btn-secondary" onClick={() => setShowRemarcarModal(false)}>
                  <CancelIcon size={16} /> Fechar
                </button>
                <button type="submit" className="btn-primary">
                  <SaveIcon size={16} /> Confirmar Remarcação
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

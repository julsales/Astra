import React, { useState, useEffect, useMemo } from 'react';
import './PageStyles.css';
import { AddIcon, EditIcon, DeleteIcon, SearchIcon, SaveIcon, CancelIcon } from '../Icons';

const statusOptions = [
  { label: 'Todos os status', value: 'TODOS' },
  { label: 'Disponíveis', value: 'DISPONIVEL' },
  { label: 'Esgotadas', value: 'ESGOTADA' },
  { label: 'Indisponíveis', value: 'INDISPONIVEL' },
  { label: 'Canceladas', value: 'CANCELADA' }
];

// Estrategia de remarcação removida — a API agora decide: se enviar assentos, remarca individual; caso contrário, remarca em massa.

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
  const [salas, setSalas] = useState([]);
  const [indicadores, setIndicadores] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editando, setEditando] = useState(null);
  const [formData, setFormData] = useState({
    filmeId: '',
    horario: '',
    salaId: ''
  });
  const [filtros, setFiltros] = useState({
    filmeId: 'TODOS',
    status: 'TODOS'
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

      const [resFilmes, resSessoes, resIndicadores, resSalas] = await Promise.all([
        fetch('/api/filmes/em-cartaz'),
        fetch(params.toString() ? `/api/sessoes?${params}` : '/api/sessoes'),
        fetch('/api/sessoes/indicadores'),
        fetch('/api/salas')
      ]);

      if (!resFilmes.ok || !resSessoes.ok || !resIndicadores.ok || !resSalas.ok) {
        throw new Error('Falha ao buscar dados de sessões');
      }

      const dadosFilmes = await resFilmes.json();
      const dadosSessoes = await resSessoes.json();
      const dadosIndicadores = await resIndicadores.json();
      const dadosSalas = await resSalas.json();

      setFilmes(dadosFilmes);
      setSessoes(dadosSessoes);
      setIndicadores(dadosIndicadores);
      setSalas(dadosSalas);
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
        salaId: sessao.salaId || ''
      });
    } else {
      setEditando(null);
      setFormData({
        filmeId: '',
        horario: '',
        salaId: salas.length > 0 ? salas[0].id : ''
      });
    }
    setShowModal(true);
  };

  const fecharModal = () => {
    setShowModal(false);
    setEditando(null);
    setFormData({ filmeId: '', horario: '', salaId: '' });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      // Validar se o horário foi preenchido (apenas ao criar)
      if (!editando && !formData.horario) {
        alert('Por favor, preencha a data e horário');
        return;
      }

      // Validar se sala foi selecionada
      if (!formData.salaId) {
        alert('Por favor, selecione uma sala');
        return;
      }

      // Converter horário do datetime-local para ISO string
      const horarioISO = formData.horario.includes('T')
        ? new Date(formData.horario).toISOString()
        : new Date(formData.horario + ':00').toISOString();

      const payload = editando
        ? {
            horario: horarioISO,
            salaId: parseInt(formData.salaId),
            funcionario: getFuncionarioPayload()
          }
        : {
            filmeId: parseInt(formData.filmeId),
            horario: horarioISO,
            salaId: parseInt(formData.salaId),
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

  const handleFiltroChange = (campo, valor) => {
    const novosFiltros = { ...filtros, [campo]: valor };
    carregarDados(novosFiltros);
  };

  const resetFiltros = () => {
    const defaultFiltros = { filmeId: 'TODOS', status: 'TODOS' };
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
              <span className="stat-label">Taxa Média de Ocupação</span>
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
                        {sessao.filme?.titulo || sessao.filmeTitulo || 'Filme não encontrado'}
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

              <div className="form-group">
                <label>Sala *</label>
                <select
                  value={formData.salaId}
                  onChange={(e) => setFormData({...formData, salaId: e.target.value})}
                  required
                >
                  <option value="">Selecione uma sala</option>
                  {salas.map(sala => (
                    <option key={sala.id} value={sala.id}>
                      {sala.nome} - {sala.capacidade} lugares ({sala.tipo})
                    </option>
                  ))}
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
    </div>
  );
};

export default Sessoes;

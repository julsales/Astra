import React, { useState, useEffect, useMemo } from 'react';
import './PageStyles.css';
import { AddIcon, EditIcon, DeleteIcon, SearchIcon, ViewIcon } from '../Icons';

const Usuarios = ({ usuario }) => {
  const [clientes, setClientes] = useState([]);
  const [funcionarios, setFuncionarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [tipoFiltro, setTipoFiltro] = useState('todos');
  const [busca, setBusca] = useState('');
  const [modalAberto, setModalAberto] = useState(false);
  const [formulario, setFormulario] = useState({ nome: '', email: '', senha: '', cargo: 'ATENDENTE' });
  const [alvoEdicao, setAlvoEdicao] = useState(null);
  const [feedback, setFeedback] = useState(null);
  const [processando, setProcessando] = useState(false);

  useEffect(() => {
    carregarDados();
  }, []);

  const carregarDados = async () => {
    try {
      setLoading(true);
      const [resClientes, resFuncionarios] = await Promise.all([
        fetch('/api/clientes'),
        fetch('/api/funcionarios')
      ]);

      const listaClientes = await resClientes.json();
      const listaFuncionarios = await resFuncionarios.json();

      setClientes(listaClientes || []);
      setFuncionarios(listaFuncionarios || []);
    } catch (error) {
      console.error('Erro ao carregar usuários:', error);
      setFeedback({ tipo: 'erro', mensagem: 'Não foi possível carregar os dados. Tente novamente.' });
    } finally {
      setLoading(false);
    }
  };

  const payloadAutorizacao = () => ({
    nome: usuario?.nome || 'Administrador',
    cargo: usuario?.funcao || 'GERENTE'
  });

  const abrirModalNovo = () => {
    setAlvoEdicao(null);
    setFormulario({ nome: '', email: '', senha: '', cargo: 'ATENDENTE' });
    setModalAberto(true);
  };

  const abrirModalEdicao = (func) => {
    setAlvoEdicao(func);
    setFormulario({ nome: func.nome, email: func.email || '', senha: '', cargo: func.cargo });
    setModalAberto(true);
  };

  const fecharModal = () => {
    setModalAberto(false);
    setFormulario({ nome: '', email: '', senha: '', cargo: 'ATENDENTE' });
    setAlvoEdicao(null);
  };

  const salvarFuncionario = async () => {
    if (!formulario.nome.trim()) {
      setFeedback({ tipo: 'erro', mensagem: 'Informe o nome do funcionário.' });
      return;
    }

    if (!alvoEdicao && !formulario.email.trim()) {
      setFeedback({ tipo: 'erro', mensagem: 'Informe o email do funcionário.' });
      return;
    }

    if (!alvoEdicao && !formulario.senha.trim()) {
      setFeedback({ tipo: 'erro', mensagem: 'Informe a senha do funcionário.' });
      return;
    }

    try {
      setProcessando(true);
      const metodo = alvoEdicao ? 'PUT' : 'POST';
      const url = alvoEdicao ? `/api/funcionarios/${alvoEdicao.id}` : '/api/funcionarios';
      
      const payload = {
        nome: formulario.nome,
        cargo: formulario.cargo,
        autorizacao: payloadAutorizacao()
      };

      // Adicionar email e senha apenas ao criar novo funcionário
      if (!alvoEdicao) {
        payload.email = formulario.email;
        payload.senha = formulario.senha;
      }

      const resposta = await fetch(url, {
        method: metodo,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!resposta.ok) {
        const erro = await resposta.json().catch(() => ({}));
        throw new Error(erro?.mensagem || 'Falha ao comunicar com o servidor.');
      }

      setFeedback({ tipo: 'sucesso', mensagem: 'Funcionário salvo com sucesso!' });
      fecharModal();
      carregarDados();
    } catch (error) {
      console.error(error);
      setFeedback({ tipo: 'erro', mensagem: error.message });
    } finally {
      setProcessando(false);
    }
  };

  const removerFuncionario = async (func) => {
    if (!window.confirm(`Confirma remover ${func.nome}?`)) {
      return;
    }
    try {
      setProcessando(true);
      const resposta = await fetch(`/api/funcionarios/${func.id}`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payloadAutorizacao())
      });

      if (!resposta.ok) {
        const erro = await resposta.json().catch(() => ({}));
        throw new Error(erro?.mensagem || 'Não foi possível remover.');
      }

      setFeedback({ tipo: 'sucesso', mensagem: 'Funcionário removido.' });
      carregarDados();
    } catch (error) {
      console.error(error);
      setFeedback({ tipo: 'erro', mensagem: error.message });
    } finally {
      setProcessando(false);
    }
  };

  const todosUsuarios = useMemo(() => ([
    ...clientes.map(c => ({ id: c.id, nome: c.nome, email: c.email, tipo: 'Cliente', status: 'Ativo' })),
    ...funcionarios.map(f => ({ id: f.id, nome: f.nome, email: f.email || '-', tipo: f.cargo, cargo: f.cargo, status: 'Ativo' }))
  ]), [clientes, funcionarios]);

  const usuariosFiltrados = useMemo(() => todosUsuarios.filter(u => {
    const termo = busca.toLowerCase();
    const passaBusca = !busca ||
      u.nome.toLowerCase().includes(termo) ||
      (u.email && u.email.toLowerCase().includes(termo));
    const passaTipo = tipoFiltro === 'todos' || u.tipo === tipoFiltro;
    return passaBusca && passaTipo;
  }), [todosUsuarios, busca, tipoFiltro]);

  const contadores = {
    total: todosUsuarios.length,
    clientes: clientes.length,
    funcionarios: funcionarios.filter(f => f.cargo === 'ATENDENTE').length,
    admins: funcionarios.filter(f => f.cargo === 'GERENTE').length,
    ativos: todosUsuarios.length,
    bloqueados: 0
  };

  if (loading) {
    return (
      <div className="page-container">
        <div style={{ textAlign: 'center', padding: '60px', color: 'rgba(255,255,255,0.7)' }}>
          <div className="spinner"></div>
          Carregando usuários...
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <div className="page-title-section">
          <h1 className="page-title">Gerenciar Usuários</h1>
          <p className="page-subtitle">Controle de clientes e funcionários</p>
        </div>
        <div style={{ display: 'flex', gap: '12px' }}>
          <button className="btn-secondary" onClick={carregarDados}>
            <SearchIcon size={16} /> Atualizar
          </button>
          <button className="btn-primary" onClick={abrirModalNovo}>
            <AddIcon size={18} /> Novo Funcionário
          </button>
        </div>
      </div>

      {feedback && (
        <div className={`alert ${feedback.tipo === 'erro' ? 'alert-error' : 'alert-success'}`}>
          {feedback.mensagem}
        </div>
      )}

      <div className="stats-grid-main" style={{ gridTemplateColumns: 'repeat(5, 1fr)' }}>
        {[
          { label: 'Total', value: contadores.total, color: 'white' },
          { label: 'Clientes', value: contadores.clientes, color: '#3B82F6' },
          { label: 'Funcionários', value: contadores.funcionarios, color: '#8B5CF6' },
          { label: 'Gerentes', value: contadores.admins, color: '#FBBF24' },
          { label: 'Ativos', value: contadores.ativos, color: '#34D399' }
        ].map(item => (
          <div className="stat-card" key={item.label}>
            <div className="stat-label">{item.label}</div>
            <div className="stat-value" style={{ fontSize: '28px', color: item.color }}>{item.value}</div>
          </div>
        ))}
      </div>

      <div className="filters-row">
        <input
          type="text"
          placeholder="Buscar por nome ou email..."
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
          className="input-control"
        />
        <select
          value={tipoFiltro}
          onChange={(e) => setTipoFiltro(e.target.value)}
          className="input-control"
          style={{ maxWidth: '220px' }}
        >
          <option value="todos">Todos os tipos</option>
          <option value="Cliente">Cliente</option>
          <option value="ATENDENTE">Funcionário</option>
          <option value="GERENTE">Gerente</option>
        </select>
      </div>

      <div className="section-container">
        <h2 className="section-title">Usuários cadastrados</h2>
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Nome</th>
                <th>Tipo</th>
                <th>Email</th>
                <th>Status</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {usuariosFiltrados.length === 0 && (
                <tr>
                  <td colSpan="5" style={{ textAlign: 'center', padding: '40px', color: 'rgba(255,255,255,0.6)' }}>
                    Nenhum registro encontrado
                  </td>
                </tr>
              )}
              {usuariosFiltrados.map((item) => (
                <tr key={`${item.tipo}-${item.id}`}>
                  <td>
                    <strong style={{ color: 'white' }}>{item.nome}</strong>
                  </td>
                  <td>
                    <span className="badge" style={{
                      background: item.tipo === 'Cliente' ? 'rgba(59,130,246,0.2)' : item.tipo === 'GERENTE' ? 'rgba(251,191,36,0.2)' : 'rgba(139,92,246,0.2)',
                      borderColor: item.tipo === 'Cliente' ? '#3B82F6' : item.tipo === 'GERENTE' ? '#FBBF24' : '#8B5CF6',
                      color: item.tipo === 'Cliente' ? '#3B82F6' : item.tipo === 'GERENTE' ? '#FBBF24' : '#C4B5FD'
                    }}>
                      {item.tipo === 'Cliente' ? 'Cliente' : item.tipo}
                    </span>
                  </td>
                  <td style={{ color: 'rgba(255,255,255,0.8)' }}>{item.email || '-'}</td>
                  <td>
                    <span className={`badge ${item.status === 'Ativo' ? 'ativa' : 'inativa'}`}>{item.status}</span>
                  </td>
                  <td>
                    {item.tipo !== 'Cliente' ? (
                      <div className="actions-inline">
                        <button className="btn-icon" title="Editar" onClick={() => abrirModalEdicao(item)}>
                          <EditIcon size={16} />
                        </button>
                        <button className="btn-icon danger" title="Remover" onClick={() => removerFuncionario(item)}>
                          <DeleteIcon size={16} />
                        </button>
                      </div>
                    ) : (
                      <span style={{ color: 'rgba(255,255,255,0.4)', fontSize: '12px' }}>somente leitura</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {modalAberto && (
        <div className="modal-overlay">
          <div className="modal-card">
            <h3>{alvoEdicao ? 'Editar funcionário' : 'Novo funcionário'}</h3>
            <div className="modal-body">
              <label>Nome completo *</label>
              <input
                className="input-control"
                value={formulario.nome}
                onChange={(e) => setFormulario({ ...formulario, nome: e.target.value })}
                placeholder="Ex.: Ana Souza"
              />
              
              {!alvoEdicao && (
                <>
                  <label>Email *</label>
                  <input
                    type="email"
                    className="input-control"
                    value={formulario.email}
                    onChange={(e) => setFormulario({ ...formulario, email: e.target.value })}
                    placeholder="Ex.: ana.souza@astra.com"
                  />
                  
                  <label>Senha *</label>
                  <input
                    type="password"
                    className="input-control"
                    value={formulario.senha}
                    onChange={(e) => setFormulario({ ...formulario, senha: e.target.value })}
                    placeholder="Senha de acesso"
                  />
                </>
              )}
              
              <label>Cargo *</label>
              <select
                className="input-control"
                value={formulario.cargo}
                onChange={(e) => setFormulario({ ...formulario, cargo: e.target.value })}
              >
                <option value="ATENDENTE">Atendente</option>
                <option value="GERENTE">Gerente</option>
              </select>
            </div>
            <div className="modal-actions">
              <button className="btn-secondary" onClick={fecharModal}>
                <DeleteIcon size={16} /> Cancelar
              </button>
              <button className="btn-primary" disabled={processando} onClick={salvarFuncionario}>
                <AddIcon size={16} /> {processando ? 'Salvando...' : 'Salvar'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Usuarios;

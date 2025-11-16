import React, { useState, useEffect } from 'react';
import './PageStyles.css';

const Usuarios = ({ usuario }) => {
  const [usuarios, setUsuarios] = useState([]);
  const [funcionarios, setFuncionarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [tipoFiltro, setTipoFiltro] = useState('todos');
  const [busca, setBusca] = useState('');

  useEffect(() => {
    carregarUsuarios();
  }, []);

  const carregarUsuarios = async () => {
    try {
      setLoading(true);
      
      // Buscar clientes
      const resClientes = await fetch('/api/clientes');
      const clientes = await resClientes.json();
      
      // Buscar funcionários
      const resFuncionarios = await fetch('/api/funcionarios');
      const funcionariosData = await resFuncionarios.json();
      
      setUsuarios(clientes);
      setFuncionarios(funcionariosData);
    } catch (error) {
      console.error('Erro ao carregar usuários:', error);
      alert('Erro ao carregar usuários');
    } finally {
      setLoading(false);
    }
  };

  const todosUsuarios = [
    ...usuarios.map(u => ({ ...u, tipo: 'Cliente', status: 'Ativo' })),
    ...funcionarios.map(f => ({ ...f, tipo: f.funcao || 'Funcionário', status: 'Ativo' }))
  ];

  const usuariosFiltrados = todosUsuarios.filter(u => {
    const passaBusca = !busca || 
      u.nome.toLowerCase().includes(busca.toLowerCase()) ||
      u.email.toLowerCase().includes(busca.toLowerCase());
    
    const passaTipo = tipoFiltro === 'todos' || u.tipo === tipoFiltro;
    
    return passaBusca && passaTipo;
  });

  const contadores = {
    total: todosUsuarios.length,
    clientes: usuarios.length,
    funcionarios: funcionarios.filter(f => f.funcao === 'ATENDENTE').length,
    admins: funcionarios.filter(f => f.funcao === 'GERENTE').length,
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
          <h1 className="page-title">
            <span className="page-icon"></span>
            Gerenciar Usuários
          </h1>
          <p className="page-subtitle">
            Controle usuários, permissões e atividades
          </p>
        </div>
        <button className="btn-primary" onClick={carregarUsuarios}>
          Atualizar
        </button>
      </div>

      {/* Estatísticas */}
      <div className="stats-grid-main" style={{ gridTemplateColumns: 'repeat(6, 1fr)' }}>
        <div className="stat-card">
          <div className="stat-label">Total</div>
          <div className="stat-value" style={{ fontSize: '28px' }}>{contadores.total}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Clientes</div>
          <div className="stat-value" style={{ fontSize: '28px', color: '#3B82F6' }}>{contadores.clientes}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Funcionários</div>
          <div className="stat-value" style={{ fontSize: '28px', color: '#8B5CF6' }}>{contadores.funcionarios}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Admins</div>
          <div className="stat-value" style={{ fontSize: '28px', color: '#FFA500' }}>{contadores.admins}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Ativos</div>
          <div className="stat-value" style={{ fontSize: '28px', color: '#34D399' }}>{contadores.ativos}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Bloqueados</div>
          <div className="stat-value" style={{ fontSize: '28px', color: '#FF6B6B' }}>{contadores.bloqueados}</div>
        </div>
      </div>

      {/* Abas */}
      <div style={{
        display: 'flex',
        gap: '8px',
        marginBottom: '20px',
        borderBottom: '1px solid rgba(139,92,246,0.2)'
      }}>
        <button style={{
          padding: '12px 24px',
          background: 'linear-gradient(135deg, #8B5CF6 0%, #7C3AED 100%)',
          border: 'none',
          borderRadius: '8px 8px 0 0',
          color: 'white',
          fontWeight: 600,
          cursor: 'pointer'
        }}>
          Usuários
        </button>
        <button style={{
          padding: '12px 24px',
          background: 'transparent',
          border: 'none',
          borderRadius: '8px 8px 0 0',
          color: 'rgba(255,255,255,0.6)',
          fontWeight: 600,
          cursor: 'pointer'
        }}>
          Log de Atividades
        </button>
      </div>

      {/* Filtros */}
      <div style={{
        display: 'flex',
        gap: '12px',
        marginBottom: '20px',
        padding: '16px',
        background: 'rgba(30,20,60,0.4)',
        borderRadius: '12px',
        border: '1px solid rgba(139,92,246,0.2)'
      }}>
        <input 
          type="text" 
          placeholder="Buscar por nome ou email..." 
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
          style={{
            flex: 1,
            padding: '10px 16px',
            background: 'rgba(139,92,246,0.1)',
            border: '1px solid rgba(139,92,246,0.3)',
            borderRadius: '8px',
            color: 'white',
            fontSize: '14px'
          }}
        />
        <select 
          value={tipoFiltro}
          onChange={(e) => setTipoFiltro(e.target.value)}
          style={{
            padding: '10px 16px',
            background: 'rgba(139,92,246,0.1)',
            border: '1px solid rgba(139,92,246,0.3)',
            borderRadius: '8px',
            color: 'white',
            fontSize: '14px',
            cursor: 'pointer'
          }}
        >
          <option value="todos">Todos os tipos</option>
          <option value="Cliente">Cliente</option>
          <option value="ATENDENTE">Funcionário</option>
          <option value="GERENTE">Administrador</option>
        </select>
      </div>

      {/* Tabela */}
      <div className="section-container">
        <h2 className="section-title">Lista de Usuários</h2>
        <p style={{ color: 'rgba(255,255,255,0.6)', fontSize: '14px', marginBottom: '20px' }}>
          Gerencie todos os usuários do sistema
        </p>

        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Usuário</th>
                <th>Tipo</th>
                <th>Status</th>
                <th>Último Login</th>
                <th>Ingressos</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {usuariosFiltrados.length === 0 ? (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '40px', color: 'rgba(255,255,255,0.5)' }}>
                    Nenhum usuário encontrado
                  </td>
                </tr>
              ) : (
                usuariosFiltrados.map((user, index) => (
                  <tr key={`${user.tipo}-${user.id || index}`}>
                    <td>
                      <div>
                        <strong style={{ color: 'white', display: 'block' }}>{user.nome}</strong>
                        <small style={{ color: 'rgba(255,255,255,0.5)' }}>{user.email}</small>
                      </div>
                    </td>
                    <td>
                      <span className="badge" style={{
                        background: user.tipo === 'Cliente' ? 'rgba(59,130,246,0.2)' :
                                   user.tipo === 'ATENDENTE' ? 'rgba(139,92,246,0.2)' :
                                   'rgba(255,165,0,0.2)',
                        color: user.tipo === 'Cliente' ? '#3B82F6' :
                               user.tipo === 'ATENDENTE' ? '#8B5CF6' :
                               '#FFA500',
                        border: `1px solid ${user.tipo === 'Cliente' ? '#3B82F6' :
                                            user.tipo === 'ATENDENTE' ? '#8B5CF6' :
                                            '#FFA500'}`
                      }}>
                        {user.tipo}
                      </span>
                    </td>
                    <td>
                      <span className={`badge ${user.status === 'Ativo' ? 'ativa' : 'inativa'}`}>
                        {user.status}
                      </span>
                    </td>
                    <td>
                      <small style={{ color: 'rgba(255,255,255,0.7)' }}>
                        -
                      </small>
                    </td>
                    <td>
                      <strong style={{ color: 'white' }}>-</strong>
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: '8px' }}>
                        <button style={{
                          padding: '6px 10px',
                          background: 'rgba(139,92,246,0.15)',
                          border: '1px solid rgba(139,92,246,0.3)',
                          borderRadius: '6px',
                          color: 'white',
                          cursor: 'pointer',
                          fontSize: '16px'
                        }} title="Editar">
                          ✏️
                        </button>
                        <button style={{
                          padding: '6px 10px',
                          background: 'rgba(59,130,246,0.15)',
                          border: '1px solid rgba(59,130,246,0.3)',
                          borderRadius: '6px',
                          color: '#3B82F6',
                          cursor: 'pointer',
                          fontSize: '16px'
                        }} title="Bloquear">
                          
                        </button>
                        <button style={{
                          padding: '6px 10px',
                          background: 'rgba(255,59,48,0.15)',
                          border: '1px solid rgba(255,59,48,0.3)',
                          borderRadius: '6px',
                          color: '#FF6B6B',
                          cursor: 'pointer',
                          fontSize: '16px'
                        }} title="Excluir">
                          
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Usuarios;

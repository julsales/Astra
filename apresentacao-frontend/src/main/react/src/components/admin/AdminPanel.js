import React, { useState } from 'react';
import './AdminPanel.css';
import Overview from './pages/Overview';
import Sessoes from './pages/Sessoes';
import Filmes from './pages/Filmes';
import Promocoes from './pages/Promocoes';
import Relatorios from './pages/Relatorios';
import Usuarios from './pages/Usuarios';
import {
  OverviewIcon,
  FilmeIcon,
  SessaoIcon,
  BomboniereIcon,
  RelatorioIcon,
  UsuarioIcon,
} from './Icons';

const AdminPanel = ({ usuario, onLogout }) => {
  const [paginaAtiva, setPaginaAtiva] = useState('overview');

  const menu = [
    { id: 'overview', nome: 'Overview', icone: <OverviewIcon size={20} /> },
    { id: 'filmes', nome: 'Filmes', icone: <FilmeIcon size={20} /> },
    { id: 'sessoes', nome: 'Sessões', icone: <SessaoIcon size={20} /> },
    { id: 'promocoes', nome: 'Bomboniere', icone: <BomboniereIcon size={20} /> },
    { id: 'relatorios', nome: 'Relatórios', icone: <RelatorioIcon size={20} /> },
    { id: 'usuarios', nome: 'Usuários', icone: <UsuarioIcon size={20} /> },
  ];

  const renderizarPagina = () => {
    switch (paginaAtiva) {
      case 'overview':
        return <Overview usuario={usuario} />;
      case 'sessoes':
        return <Sessoes usuario={usuario} />;
      case 'filmes':
        return <Filmes usuario={usuario} />;
      case 'promocoes':
        return <Promocoes usuario={usuario} />;
      case 'relatorios':
        return <Relatorios usuario={usuario} />;
      case 'usuarios':
        return <Usuarios usuario={usuario} />;
      default:
        return <Overview usuario={usuario} />;
    }
  };

  return (
    <div className="admin-panel">
      {/* Header Superior */}
      <header className="admin-header">
        <div className="header-left">
          <div className="logo-admin">
            <img src="/logo.png" alt="Astra Cinemas" className="logo-header-image" />
          </div>
          <div className="admin-badge">Administrador</div>
        </div>

        <div className="header-right">
          <div className="user-info-header">
            <span className="user-name">{usuario?.nome || 'Usuário'}</span>
            {usuario?.funcao && (
              <span style={{ fontSize: '12px', color: 'rgba(255,255,255,0.6)', marginLeft: '8px' }}>
                ({usuario.funcao})
              </span>
            )}
          </div>
          <button className="logout-btn-header" onClick={onLogout}>
            Sair
          </button>
        </div>
      </header>

      {/* Barra de Progresso */}
      <div className="progress-bar-container">
        <div className="progress-bar-fill"></div>
      </div>

      {/* Container Principal */}
      <div className="admin-content-wrapper">
        {/* Menu de Navegação */}
        <nav className="admin-nav">
          {menu.map((item) => (
            <button
              key={item.id}
              className={`nav-item ${paginaAtiva === item.id ? 'active' : ''}`}
              onClick={() => setPaginaAtiva(item.id)}
            >
              <span className="nav-icon">{item.icone}</span>
              <span className="nav-label">{item.nome}</span>
            </button>
          ))}
        </nav>

        {/* Conteúdo da Página */}
        <main className="admin-main-content">
          {renderizarPagina()}
        </main>
      </div>

      {/* Footer */}
      <footer className="admin-footer">
        <p>© 2025 Astra Cinemas • Sistema de Gerenciamento de Cinema</p>
      </footer>
    </div>
  );
};

export default AdminPanel;

import React, { useState } from 'react';
import './Dashboard.css';
import GerenciarFilmes from './GerenciarFilmes';
import GerenciarSessoes from './GerenciarSessoes';

const Dashboard = ({ usuario, onLogout }) => {
  const [telaAtiva, setTelaAtiva] = useState('inicio');

  const renderizarConteudo = () => {
    switch (telaAtiva) {
      case 'filmes':
        return <GerenciarFilmes usuario={usuario} />;
      case 'sessoes':
        return <GerenciarSessoes usuario={usuario} />;
      default:
        return (
          <div className="inicio-content">
            <div className="welcome-card">
              <div className="welcome-icon"></div>
              <h2>Bem-vindo ao Sistema Astra</h2>
              <p className="user-info">
                Logado como: <strong>{usuario.nome}</strong>
              </p>
              <p className="user-role">
                Função: <span className={`badge ${usuario.funcao.toLowerCase()}`}>
                  {usuario.funcao}
                </span>
              </p>
            </div>

            <div className="cards-grid">
              {usuario.funcao === 'GERENTE' && (
                <>
                  <div 
                    className="feature-card" 
                    onClick={() => setTelaAtiva('filmes')}
                  >
                    <div className="card-icon"></div>
                    <h3>Gerenciar Filmes</h3>
                    <p>Remover filmes do catálogo</p>
                    <div className="card-badge">GERENTE</div>
                  </div>

                  <div 
                    className="feature-card" 
                    onClick={() => setTelaAtiva('sessoes')}
                  >
                    <div className="card-icon"></div>
                    <h3>Gerenciar Sessões</h3>
                    <p>Criar novas sessões de cinema</p>
                    <div className="card-badge">GERENTE</div>
                  </div>
                </>
              )}

              <div className="feature-card disabled">
                <div className="card-icon"></div>
                <h3>Bomboniere</h3>
                <p>Em desenvolvimento</p>
              </div>

              <div className="feature-card disabled">
                <div className="card-icon"></div>
                <h3>Vendas</h3>
                <p>Em desenvolvimento</p>
              </div>
            </div>

            {usuario.funcao === 'ATENDENTE' && (
              <div className="info-box">
                <div className="info-icon">ℹ️</div>
                <p>
                  <strong>Acesso Restrito:</strong> Algumas funcionalidades estão 
                  disponíveis apenas para gerentes. Entre em contato com seu supervisor 
                  para mais informações.
                </p>
              </div>
            )}
          </div>
        );
    }
  };

  return (
    <div className="dashboard-container">
      <nav className="sidebar">
        <div className="sidebar-header">
          <div className="logo-mini">
            <svg className="star-icon-mini" viewBox="0 0 100 100">
              <defs>
                <radialGradient id="starGradientMini">
                  <stop offset="0%" stopColor="#FFA500" />
                  <stop offset="100%" stopColor="#FF8C00" />
                </radialGradient>
              </defs>
              <polygon points="50,15 61,38 85,41 67,58 72,82 50,70 28,82 33,58 15,41 39,38" fill="url(#starGradientMini)" />
            </svg>
            <span className="logo-text-mini">ASTRA</span>
          </div>
        </div>

        <ul className="menu">
          <li 
            className={telaAtiva === 'inicio' ? 'active' : ''}
            onClick={() => setTelaAtiva('inicio')}
          >
            <span>Início</span>
          </li>

          {usuario.funcao === 'GERENTE' && (
            <>
              <li 
                className={telaAtiva === 'filmes' ? 'active' : ''}
                onClick={() => setTelaAtiva('filmes')}
              >
                <span className="menu-icon"></span>
                <span>Filmes</span>
              </li>

              <li 
                className={telaAtiva === 'sessoes' ? 'active' : ''}
                onClick={() => setTelaAtiva('sessoes')}
              >
                <span className="menu-icon"></span>
                <span>Sessões</span>
              </li>
            </>
          )}

          <li className="disabled">
            <span className="menu-icon"></span>
            <span>Bomboniere</span>
          </li>

          <li className="disabled">
            <span className="menu-icon"></span>
            <span>Vendas</span>
          </li>
        </ul>

        <div className="sidebar-footer">
          <div className="user-card">
            <div className="user-avatar">
              {usuario.nome.charAt(0).toUpperCase()}
            </div>
            <div className="user-details">
              <div className="user-name">{usuario.nome}</div>
              <div className="user-function">{usuario.funcao}</div>
            </div>
          </div>
          <button className="logout-button" onClick={onLogout}>
            <span></span>
            Sair
          </button>
        </div>
      </nav>

      <main className="main-content">
        <header className="top-bar">
          <h1 className="page-title">
            {telaAtiva === 'inicio' && 'Dashboard'}
            {telaAtiva === 'filmes' && 'Gerenciar Filmes'}
            {telaAtiva === 'sessoes' && 'Gerenciar Sessões'}
          </h1>
          <div className="top-bar-actions">
            <div className="notification-badge">
              <span className="badge-count">3</span>
            </div>
          </div>
        </header>

        <div className="content-area">
          {renderizarConteudo()}
        </div>
      </main>
    </div>
  );
};

export default Dashboard;

import React, { useState } from 'react';
import './ClienteNovo.css';
import HomeCliente from './pages/HomeCliente';
import CompraIngresso from './pages/CompraIngresso';
import Sucesso from './pages/Sucesso';
import Stars from '../Stars';
import logo from '../unnamed-removebg-preview.png';

const ClientePainel = ({ usuario, onLogout }) => {
  const [tela, setTela] = useState('home'); // 'home', 'compraIngresso', 'sucesso'
  const [abaHome, setAbaHome] = useState('filmes'); // 'filmes' ou 'ingressos'
  const [sessaoSelecionada, setSessaoSelecionada] = useState(null);
  const [filmeSelecionado, setFilmeSelecionado] = useState(null);
  const [compraConcluida, setCompraConcluida] = useState(null);

  const handleIniciarCompra = (sessao, filme) => {
    setSessaoSelecionada(sessao);
    setFilmeSelecionado(filme);
    setTela('compraIngresso');
  };

  const handleVoltarHome = () => {
    setTela('home');
    setAbaHome('filmes');
    setSessaoSelecionada(null);
    setFilmeSelecionado(null);
    setCompraConcluida(null);
  };

  const handleCompraConcluida = (compra) => {
    setCompraConcluida(compra);
    setTela('sucesso');
  };

  const handleAbrirIngressos = () => {
    setTela('home');
    setAbaHome('ingressos');
    setSessaoSelecionada(null);
    setFilmeSelecionado(null);
    setCompraConcluida(null);
  };

  return (
    <div className="cliente-novo-container">
      <Stars />
      {/* Header fixo - igual ao protótipo */}
      <header className="header-novo">
        <div className="header-logo">
          <img src={logo} alt="Astra Cinemas" className="logo-image" />
        </div>

        <div className="header-usuario">
          {usuario.tipo === 'CLIENTE' && (
            <span className="badge-cliente-novo">Cliente</span>
          )}
          <button className="btn-conta-demo" onClick={onLogout}>
            <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
              <circle cx="8" cy="5" r="3" />
              <path d="M2 14C2 11 5 9 8 9C11 9 14 11 14 14" />
            </svg>
            Conta Demo
          </button>
          <button className="btn-sair-novo" onClick={onLogout}>
            Sair
          </button>
        </div>
      </header>

      {/* Conteúdo principal */}
      <main className="main-content-novo">
        {tela === 'home' && (
          <HomeCliente 
            usuario={usuario} 
            onIniciarCompra={handleIniciarCompra}
            abaInicial={abaHome}
          />
        )}
        
        {tela === 'compraIngresso' && (
          <CompraIngresso 
            sessao={sessaoSelecionada}
            filme={filmeSelecionado}
            usuario={usuario}
            onVoltar={handleVoltarHome}
            onConcluir={handleCompraConcluida}
          />
        )}
        
        {tela === 'sucesso' && compraConcluida && (
          <Sucesso 
            compra={compraConcluida}
            onVoltarInicio={handleVoltarHome}
            onAbrirIngressos={handleAbrirIngressos}
          />
        )}
      </main>

      {/* Footer */}
      <footer className="footer-novo">
        <p>© 2025 Astra Cinemas · Protótipo de Sistema de Reservas · Dados simulados para demonstração</p>
      </footer>
    </div>
  );
};

export default ClientePainel;

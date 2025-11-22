import React, { useState } from 'react';
import './App.css';
import Login from './components/Login';
import AdminPanel from './components/admin/AdminPanel';
import ClientePainel from './components/cliente/ClientePainel';
import FuncionarioPanel from './components/funcionario/FuncionarioPanel';
import Stars from './components/Stars';
import CosmicNebula from './components/CosmicNebula';

function App() {
  const [usuarioLogado, setUsuarioLogado] = useState(() => {
    try {
      const raw = window.localStorage.getItem('usuarioLogado');
      return raw ? JSON.parse(raw) : null;
    } catch (e) {
      return null;
    }
  });

  const handleLogin = (usuario) => {
    setUsuarioLogado(usuario);
    try { window.localStorage.setItem('usuarioLogado', JSON.stringify(usuario)); } catch (e) { /* ignore */ }
  };

  const handleLogout = () => {
    setUsuarioLogado(null);
    try { window.localStorage.removeItem('usuarioLogado'); } catch (e) { /* ignore */ }
  };

  // Se usuário está logado, decide qual painel mostrar
  if (usuarioLogado) {
    return (
      <div className="App">
        <Stars />
        <CosmicNebula />
        <div className="diamond-star"></div>
        {usuarioLogado.tipo === 'ADMIN' && (
          <AdminPanel usuario={usuarioLogado} onLogout={handleLogout} />
        )}
        {usuarioLogado.tipo === 'FUNCIONARIO' && (
          <FuncionarioPanel usuario={usuarioLogado} onLogout={handleLogout} />
        )}
        {usuarioLogado.tipo === 'CLIENTE' && (
          <ClientePainel usuario={usuarioLogado} onLogout={handleLogout} />
        )}
      </div>
    );
  }

  // Se não está logado, mostra Login
  return (
    <div className="App">
      <Stars />
      <CosmicNebula />
      <div className="diamond-star"></div>
      <Login onLogin={handleLogin} />
    </div>
  );
}

export default App;

import React, { useState } from 'react';
import './App.css';
import Login from './components/Login';
import AdminPanel from './components/admin/AdminPanel';
import Stars from './components/Stars';
import CosmicNebula from './components/CosmicNebula';

function App() {
  const [usuarioLogado, setUsuarioLogado] = useState(null);

  const handleLogin = (usuario) => {
    setUsuarioLogado(usuario);
  };

  const handleLogout = () => {
    setUsuarioLogado(null);
  };

  // Se usuário está logado, mostra o Painel Administrativo
  if (usuarioLogado) {
    return (
      <div className="App">
        <CosmicNebula />
        <div className="diamond-star"></div>
        <AdminPanel usuario={usuarioLogado} onLogout={handleLogout} />
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

import React, { useState } from 'react';
import './Login.css';

const Login = ({ onLogin }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro('');
    setCarregando(true);
    
    try {
      // AUTENTICAÇÃO REAL COM BACKEND - SEM MOCK!
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: email,
          senha: password
        })
      });

      if (!response.ok) {
        const errorData = await response.json();
        setErro(errorData.mensagem || 'Credenciais inválidas');
        setCarregando(false);
        return;
      }

      const usuario = await response.json();
      onLogin(usuario);
    } catch (error) {
      console.error('Erro ao fazer login:', error);
      setErro('Erro ao conectar com o servidor. Verifique se o backend está rodando.');
      setCarregando(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <div className="logo-container">
          <div className="logo">
            <img src="/logo.png" alt="Astra Cinemas" className="logo-image" />
          </div>
        </div>

        <div className="welcome-section">
          <svg className="film-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="2" y="3" width="20" height="18" rx="2" stroke="#8B5CF6" strokeWidth="2"/>
            <rect x="6" y="3" width="2" height="4" fill="#8B5CF6"/>
            <rect x="11" y="3" width="2" height="4" fill="#8B5CF6"/>
            <rect x="16" y="3" width="2" height="4" fill="#8B5CF6"/>
            <rect x="6" y="17" width="2" height="4" fill="#8B5CF6"/>
            <rect x="11" y="17" width="2" height="4" fill="#8B5CF6"/>
            <rect x="16" y="17" width="2" height="4" fill="#8B5CF6"/>
          </svg>
          <h2 className="welcome-text">Bem-vindo</h2>
          <p className="subtitle-text">Entre para acessar o sistema</p>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">E-mail</label>
            <input
              type="email"
              id="email"
              placeholder="seu@email.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Senha</label>
            <input
              type="password"
              id="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          {erro && (
            <div className="error-message">
              {erro}
            </div>
          )}

          <div className="demo-credentials">
            <p><strong>Credenciais Demo:</strong></p>
            <p>Email: admin@astra.com</p>
            <p>Senha: demo123</p>
          </div>

          <button type="submit" className="login-button" disabled={carregando}>
            {carregando ? 'Entrando...' : 'Entrar'}
          </button>
        </form>

        <div className="demo-note">
          Protótipo de demonstração - Astra Cinemas
        </div>

        <div className="footer-note">
          Backend: Spring Boot + JPA/Hibernate + Spring Security
        </div>
      </div>
    </div>
  );
};

export default Login;

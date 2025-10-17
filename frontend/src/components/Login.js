import React, { useState } from 'react';
import './Login.css';

const Login = ({ onRegisterClick }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email,
          password,
          userType: 'cliente',
        }),
      });

      const data = await response.json();
      
      if (data.success) {
        alert(`✅ ${data.message}\nBem-vindo como ${data.userType}!`);
        console.log('Login bem-sucedido:', data);
      } else {
        alert('❌ Falha no login. Tente novamente.');
      }
    } catch (error) {
      console.error('Erro ao fazer login:', error);
      alert('❌ Erro ao conectar com o servidor.');
    }
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <div className="logo-container">
          <div className="logo">
            <svg className="star-icon" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
              <defs>
                <radialGradient id="starGradient">
                  <stop offset="0%" stopColor="#FFA500" />
                  <stop offset="100%" stopColor="#FF8C00" />
                </radialGradient>
              </defs>
              <polygon points="50,15 61,38 85,41 67,58 72,82 50,70 28,82 33,58 15,41 39,38" fill="url(#starGradient)" />
              <circle cx="50" cy="50" r="12" fill="#FFD700" />
            </svg>
            <span className="logo-text">ASTRA</span>
          </div>
          <div className="logo-subtitle">CINEMAS</div>
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

          <div className="remember-me">
            <input
              type="checkbox"
              id="rememberMe"
              checked={rememberMe}
              onChange={(e) => setRememberMe(e.target.checked)}
            />
            <label htmlFor="rememberMe">Lembrar-me</label>
          </div>

          <button type="submit" className="login-button">
            Entrar
          </button>
        </form>

        <div className="register-link">
          Não tem uma conta?{' '}
          <button onClick={onRegisterClick} className="link-button">
            Cadastre-se
          </button>
        </div>

        <div className="demo-note">
          💡 Protótipo de demonstração - Astra Cinemas
        </div>

        <div className="footer-note">
          Dados simulados para demonstração - Sem backend real
        </div>
      </div>
    </div>
  );
};

export default Login;

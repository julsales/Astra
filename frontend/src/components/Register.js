import React, { useState } from 'react';
import './Register.css';

const Register = ({ onBackToLogin }) => {
  const [formData, setFormData] = useState({
    nome: '',
    email: '',
    senha: '',
    confirmarSenha: '',
    cpf: '',
    telefone: '',
    dataNascimento: ''
  });

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Limpa o erro do campo quando o usuário começa a digitar
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.nome.trim()) {
      newErrors.nome = 'Nome é obrigatório';
    }

    if (!formData.email.trim()) {
      newErrors.email = 'Email é obrigatório';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Email inválido';
    }

    if (!formData.senha) {
      newErrors.senha = 'Senha é obrigatória';
    } else if (formData.senha.length < 6) {
      newErrors.senha = 'Senha deve ter no mínimo 6 caracteres';
    }

    if (formData.senha !== formData.confirmarSenha) {
      newErrors.confirmarSenha = 'As senhas não coincidem';
    }

    if (!formData.cpf.trim()) {
      newErrors.cpf = 'CPF é obrigatório';
    } else if (!/^\d{11}$/.test(formData.cpf.replace(/\D/g, ''))) {
      newErrors.cpf = 'CPF inválido';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    try {
      const response = await fetch('/api/clientes', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          nome: formData.nome,
          email: formData.email,
          senha: formData.senha,
          cpf: formData.cpf.replace(/\D/g, ''),
          telefone: formData.telefone,
          dataNascimento: formData.dataNascimento
        }),
      });

      if (response.ok) {
        alert(' Cadastro realizado com sucesso!\nFaça login para continuar.');
        onBackToLogin();
      } else {
        const data = await response.json();
        alert(` Erro: ${data.message || 'Não foi possível realizar o cadastro'}`);
      }
    } catch (error) {
      console.error('Erro ao cadastrar:', error);
      alert(' Erro ao conectar com o servidor.');
    }
  };

  return (
    <div className="register-container">
      <div className="register-box">
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
          <h2 className="welcome-text">Criar Conta</h2>
          <p className="subtitle-text">Cadastre-se para reservar seus ingressos</p>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="nome">Nome Completo</label>
            <input
              type="text"
              id="nome"
              name="nome"
              placeholder="Seu nome completo"
              value={formData.nome}
              onChange={handleChange}
              className={errors.nome ? 'error' : ''}
            />
            {errors.nome && <span className="error-message">{errors.nome}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="email">E-mail</label>
            <input
              type="email"
              id="email"
              name="email"
              placeholder="seu@email.com"
              value={formData.email}
              onChange={handleChange}
              className={errors.email ? 'error' : ''}
            />
            {errors.email && <span className="error-message">{errors.email}</span>}
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="senha">Senha</label>
              <input
                type="password"
                id="senha"
                name="senha"
                placeholder="••••••••"
                value={formData.senha}
                onChange={handleChange}
                className={errors.senha ? 'error' : ''}
              />
              {errors.senha && <span className="error-message">{errors.senha}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="confirmarSenha">Confirmar Senha</label>
              <input
                type="password"
                id="confirmarSenha"
                name="confirmarSenha"
                placeholder="••••••••"
                value={formData.confirmarSenha}
                onChange={handleChange}
                className={errors.confirmarSenha ? 'error' : ''}
              />
              {errors.confirmarSenha && <span className="error-message">{errors.confirmarSenha}</span>}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="cpf">CPF</label>
              <input
                type="text"
                id="cpf"
                name="cpf"
                placeholder="000.000.000-00"
                value={formData.cpf}
                onChange={handleChange}
                maxLength="14"
                className={errors.cpf ? 'error' : ''}
              />
              {errors.cpf && <span className="error-message">{errors.cpf}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="telefone">Telefone</label>
              <input
                type="tel"
                id="telefone"
                name="telefone"
                placeholder="(00) 00000-0000"
                value={formData.telefone}
                onChange={handleChange}
                maxLength="15"
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="dataNascimento">Data de Nascimento</label>
            <input
              type="date"
              id="dataNascimento"
              name="dataNascimento"
              value={formData.dataNascimento}
              onChange={handleChange}
            />
          </div>

          <button type="submit" className="register-button">
            Cadastrar
          </button>

          <button type="button" className="back-button" onClick={onBackToLogin}>
            Voltar ao Login
          </button>
        </form>

        <div className="demo-note">
          Seus dados serão armazenados com segurança
        </div>
      </div>
    </div>
  );
};

export default Register;

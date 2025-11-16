import React, { useState, useEffect } from 'react';
import './GerenciarSessoes.css';

const GerenciarSessoes = ({ usuario }) => {
  const [filmes, setFilmes] = useState([]);
  const [sessoes, setSessoes] = useState([]);
  const [filmeSelecionado, setFilmeSelecionado] = useState(null);
  const [loading, setLoading] = useState(true);
  const [mostrarFormulario, setMostrarFormulario] = useState(false);
  const [processando, setProcessando] = useState(false);
  
  const [formData, setFormData] = useState({
    filmeId: '',
    data: '',
    horario: '',
    capacidadeSala: 100,
  });

  useEffect(() => {
    carregarFilmes();
  }, []);

  useEffect(() => {
    if (filmeSelecionado) {
      carregarSessoes(filmeSelecionado.id);
    }
  }, [filmeSelecionado]);

  const carregarFilmes = async () => {
    try {
      setLoading(true);
      const response = await fetch('/api/filmes/em-cartaz');
      if (!response.ok) throw new Error('Erro ao carregar filmes');
      const data = await response.json();
      setFilmes(data);
    } catch (err) {
      console.error('Erro:', err);
      alert('Erro ao carregar filmes');
    } finally {
      setLoading(false);
    }
  };

  const carregarSessoes = async (filmeId) => {
    try {
      const response = await fetch(`/api/sessoes/filme/${filmeId}`);
      if (!response.ok) throw new Error('Erro ao carregar sessões');
      const data = await response.json();
      setSessoes(data);
    } catch (err) {
      console.error('Erro:', err);
      setSessoes([]);
    }
  };

  const selecionarFilme = (filme) => {
    setFilmeSelecionado(filme);
    setFormData({ ...formData, filmeId: filme.id });
    setMostrarFormulario(false);
  };

  const abrirFormulario = () => {
    if (!filmeSelecionado) {
      alert('Selecione um filme primeiro!');
      return;
    }
    setMostrarFormulario(true);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const criarSessao = async (e) => {
    e.preventDefault();
    
    if (!filmeSelecionado) {
      alert('Selecione um filme!');
      return;
    }

    try {
      setProcessando(true);

      // Combina data e horário em um único timestamp
      const dataHora = `${formData.data}T${formData.horario}:00`;
      
      const requestBody = {
        filmeId: filmeSelecionado.id,
        horario: dataHora,
        capacidadeSala: parseInt(formData.capacidadeSala),
        funcionarioId: usuario.id,
        funcao: usuario.funcao,
      };

      const response = await fetch('/api/sessoes', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestBody),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.erro || 'Erro ao criar sessão');
      }

      // Sucesso
      alert(' Sessão criada com sucesso!');
      
      // Limpa o formulário
      setFormData({
        filmeId: filmeSelecionado.id,
        data: '',
        horario: '',
        capacidadeSala: 100,
      });
      
      // Recarrega as sessões
      await carregarSessoes(filmeSelecionado.id);
      setMostrarFormulario(false);
    } catch (err) {
      alert(' Erro: ' + err.message);
      console.error('Erro ao criar sessão:', err);
    } finally {
      setProcessando(false);
    }
  };

  const formatarDataHora = (dataHora) => {
    const data = new Date(dataHora);
    const dia = String(data.getDate()).padStart(2, '0');
    const mes = String(data.getMonth() + 1).padStart(2, '0');
    const ano = data.getFullYear();
    const hora = String(data.getHours()).padStart(2, '0');
    const min = String(data.getMinutes()).padStart(2, '0');
    return `${dia}/${mes}/${ano} às ${hora}:${min}`;
  };

  const getDataMinima = () => {
    const hoje = new Date();
    return hoje.toISOString().split('T')[0];
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Carregando filmes...</p>
      </div>
    );
  }

  return (
    <div className="gerenciar-sessoes">
      <div className="header-section">
        <div>
          <h2>Gerenciar Sessões</h2>
          <p className="subtitle">
            Crie e visualize sessões de cinema
          </p>
        </div>
        {filmeSelecionado && (
          <button onClick={abrirFormulario} className="btn-criar">
             Nova Sessão
          </button>
        )}
      </div>

      <div className="sessoes-layout">
        {/* Lista de Filmes */}
        <div className="filmes-sidebar">
          <h3>Filmes Disponíveis</h3>
          {filmes.length === 0 ? (
            <p className="texto-vazio">Nenhum filme disponível</p>
          ) : (
            <div className="filmes-lista">
              {filmes.map((filme) => (
                <div
                  key={filme.id}
                  className={`filme-item ${
                    filmeSelecionado?.id === filme.id ? 'ativo' : ''
                  }`}
                  onClick={() => selecionarFilme(filme)}
                >
                  <div className="filme-icon"></div>
                  <div className="filme-dados">
                    <div className="filme-nome">{filme.titulo}</div>
                    <div className="filme-meta">
                      {filme.duracao} min • {filme.genero}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Conteúdo Principal */}
        <div className="sessoes-content">
          {!filmeSelecionado ? (
            <div className="estado-vazio">
              <div className="vazio-icon"></div>
              <h3>Selecione um filme</h3>
              <p>Escolha um filme da lista ao lado para ver ou criar sessões</p>
            </div>
          ) : (
            <>
              {/* Informações do Filme */}
              <div className="filme-detalhado">
                <div className="filme-poster-grande">
                  <span className="poster-icon-grande"></span>
                </div>
                <div className="filme-info-detalhada">
                  <h2>{filmeSelecionado.titulo}</h2>
                  <div className="badges-filme">
                    <span className="badge-genero">{filmeSelecionado.genero}</span>
                    <span className="badge-duracao">⏱️ {filmeSelecionado.duracao} min</span>
                    <span className="badge-classificacao">
                      {filmeSelecionado.classificacaoEtaria}
                    </span>
                  </div>
                  {filmeSelecionado.sinopse && (
                    <p className="sinopse">{filmeSelecionado.sinopse}</p>
                  )}
                </div>
              </div>

              {/* Formulário de Criar Sessão */}
              {mostrarFormulario && (
                <div className="formulario-sessao">
                  <div className="formulario-header">
                    <h3>Nova Sessão</h3>
                    <button
                      className="btn-fechar"
                      onClick={() => setMostrarFormulario(false)}
                    >
                      
                    </button>
                  </div>
                  <form onSubmit={criarSessao}>
                    <div className="form-row">
                      <div className="form-group">
                        <label>Data da Sessão</label>
                        <input
                          type="date"
                          name="data"
                          value={formData.data}
                          onChange={handleInputChange}
                          min={getDataMinima()}
                          required
                        />
                      </div>
                      <div className="form-group">
                        <label>Horário</label>
                        <input
                          type="time"
                          name="horario"
                          value={formData.horario}
                          onChange={handleInputChange}
                          required
                        />
                      </div>
                    </div>

                    <div className="form-group">
                      <label>Capacidade da Sala</label>
                      <input
                        type="number"
                        name="capacidadeSala"
                        value={formData.capacidadeSala}
                        onChange={handleInputChange}
                        min="10"
                        max="500"
                        step="10"
                        required
                      />
                      <small>Número de assentos disponíveis (10 a 500)</small>
                    </div>

                    <div className="form-actions">
                      <button
                        type="button"
                        className="btn-cancelar"
                        onClick={() => setMostrarFormulario(false)}
                        disabled={processando}
                      >
                        Cancelar
                      </button>
                      <button
                        type="submit"
                        className="btn-salvar"
                        disabled={processando}
                      >
                        {processando ? 'Criando...' : 'Criar Sessão'}
                      </button>
                    </div>
                  </form>
                </div>
              )}

              {/* Lista de Sessões */}
              <div className="sessoes-lista-container">
                <h3>Sessões Programadas</h3>
                {sessoes.length === 0 ? (
                  <div className="sessoes-vazio">
                    <p> Nenhuma sessão programada</p>
                    <small>Clique em "Nova Sessão" para criar</small>
                  </div>
                ) : (
                  <div className="sessoes-grid">
                    {sessoes.map((sessao) => (
                      <div key={sessao.id} className="sessao-card">
                        <div className="sessao-header">
                          <span className="sessao-status disponivel">
                            {sessao.status}
                          </span>
                          <span className="sessao-id">ID: {sessao.id}</span>
                        </div>
                        <div className="sessao-info">
                          <div className="info-item">
                            <span className="info-icon"></span>
                            <span>{formatarDataHora(sessao.horario)}</span>
                          </div>
                          <div className="info-item">
                            <span className="info-icon">🪑</span>
                            <span>
                              {Object.keys(sessao.assentosDisponiveis || {}).length} assentos
                            </span>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default GerenciarSessoes;

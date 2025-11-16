import React, { useEffect, useState } from 'react';
import './PagesStyles.css';

const Filmes = ({ usuario, onSelecionarSessao, onAbrirIngressos }) => {
  const [filmes, setFilmes] = useState([]);
  const [sessoesPorFilme, setSessoesPorFilme] = useState({});
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState('');

  const carregarFilmesESessoes = async () => {
    setCarregando(true);
    setErro('');

    try {
      const resFilmes = await fetch('/api/filmes/em-cartaz');
      if (!resFilmes.ok) throw new Error('Erro ao carregar filmes');
      
      const filmesData = await resFilmes.json();
      setFilmes(filmesData);

      const sessoesPorFilmeTemp = {};
      for (const filme of filmesData) {
        try {
          const resSessoes = await fetch(`/api/sessoes/filme/${filme.id}`);
          if (resSessoes.ok) {
            const sessoesData = await resSessoes.json();
            sessoesPorFilmeTemp[filme.id] = sessoesData
              .filter(s => s.status === 'DISPONIVEL')
              .sort((a, b) => new Date(a.horario) - new Date(b.horario));
          }
        } catch (e) {
          sessoesPorFilmeTemp[filme.id] = [];
        }
      }
      setSessoesPorFilme(sessoesPorFilmeTemp);
    } catch (e) {
      setErro('Não foi possível carregar os filmes.');
    } finally {
      setCarregando(false);
    }
  };

  useEffect(() => {
    carregarFilmesESessoes();
  }, []);

  const formatarHorario = (horarioIso) => {
    const data = new Date(horarioIso);
    return data.toLocaleString('pt-BR', {
      weekday: 'short',
      day: '2-digit',
      month: 'short',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <div className="page-container">
      <header className="page-header">
        <div>
          <h1>🎬 Filmes em Cartaz</h1>
          <p className="page-subtitle">
            Escolha seu filme e reserve seus ingressos!
          </p>
        </div>
        <div className="page-header-actions">
          <button 
            className="btn-refresh" 
            onClick={carregarFilmesESessoes}
            disabled={carregando}
          >
            {carregando ? '🔄 Atualizando...' : '🔄 Atualizar'}
          </button>
          <button
            className="btn-ver-ingressos"
            onClick={onAbrirIngressos}
          >
            🎟️ Meus ingressos
          </button>
        </div>
      </header>

      {erro && <div className="alert-error">{erro}</div>}

      <div className="filmes-grid">
        {carregando && filmes.length === 0 ? (
          <div className="loading-card">Carregando filmes...</div>
        ) : filmes.length === 0 ? (
          <div className="empty-card">Nenhum filme em cartaz no momento</div>
        ) : (
          filmes.map((filme) => {
            const sessoes = sessoesPorFilme[filme.id] || [];
            return (
              <div key={filme.id} className="filme-card">
                <div className="filme-header">
                  <h2>{filme.titulo}</h2>
                  <span className="filme-badge">
                    {filme.classificacaoEtaria} • {filme.duracao} min
                  </span>
                </div>

                <div className="filme-sinopse">
                  <p>{filme.sinopse}</p>
                </div>

                {sessoes.length > 0 ? (
                  <div className="sessoes-container">
                    <h3>🎫 Sessões Disponíveis</h3>
                    <div className="sessoes-list">
                      {sessoes.map((sessao) => (
                        <div key={sessao.id} className="sessao-item">
                          <div className="sessao-info">
                            <strong>{formatarHorario(sessao.horario)}</strong>
                            <span className="sessao-sala">{sessao.sala}</span>
                            <span className={`sessao-vagas ${sessao.assentosDisponiveis > 10 ? 'ok' : 'pouco'}`}>
                              {sessao.assentosDisponiveis} vagas
                            </span>
                          </div>
                          <button 
                            className="btn-selecionar"
                            onClick={() => onSelecionarSessao(sessao, filme)}
                          >
                            Selecionar
                          </button>
                        </div>
                      ))}
                    </div>
                  </div>
                ) : (
                  <div className="sem-sessoes">
                    <p>⏰ Nenhuma sessão disponível</p>
                    <small>Aguarde novas sessões em breve</small>
                  </div>
                )}
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};

export default Filmes;

import React, { useState, useEffect } from 'react';
import { useMeusIngressos } from '../../../hooks/useMeusIngressos';

const posterFallback = 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=900&q=80';
const getPoster = (url) => {
  if (!url) return posterFallback;
  const trimmed = url.trim();
  return trimmed.length ? trimmed : posterFallback;
};

const HomeCliente = ({ usuario, onIniciarCompra }) => {
  const [abaAtiva, setAbaAtiva] = useState('filmes'); // 'filmes' ou 'ingressos'
  const [filmes, setFilmes] = useState([]);
  const [sessoesPorFilme, setSessoesPorFilme] = useState({});
  const [carregando, setCarregando] = useState(true);
  const [ingressoQrAberto, setIngressoQrAberto] = useState(null);
  
  const { ingressos, sincronizarComBackend } = useMeusIngressos(usuario);

  useEffect(() => {
    carregarFilmesESessoes();
    // tentar sincronizar ingressos com backend quando usuário estiver logado
    if (usuario && usuario.id) {
      sincronizarComBackend();
    }
  }, [sincronizarComBackend, usuario]);

  const carregarFilmesESessoes = async () => {
    setCarregando(true);
    try {
      const resFilmes = await fetch('/api/filmes/em-cartaz');
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
      console.error('Erro ao carregar filmes:', e);
    } finally {
      setCarregando(false);
    }
  };

  const formatarData = (iso) => {
    const data = new Date(iso);
    return data.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric' });
  };

  const formatarHora = (iso) => {
    const data = new Date(iso);
    return data.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  };

  return (
    <div className="home-cliente-wrapper">
      {/* Hero Section */}
      <section className="hero-section">
        <h1 className="hero-title">Bem-vindo ao Astra Cinemas</h1>
        <p className="hero-subtitle">Escolha seu filme e reserve seus ingressos com apenas alguns cliques</p>
      </section>

      {/* Tabs de navegação */}
      <nav className="tabs-navigation">
        <button 
          className={`tab-btn ${abaAtiva === 'filmes' ? 'active' : ''}`}
          onClick={() => setAbaAtiva('filmes')}
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <rect x="2" y="7" width="20" height="15" rx="2" />
            <path d="M16 3v4M8 3v4M2 11h20" />
          </svg>
          Filmes em Cartaz
        </button>
        <button 
          className={`tab-btn ${abaAtiva === 'ingressos' ? 'active' : ''}`}
          onClick={() => setAbaAtiva('ingressos')}
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M3 7v10c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2z" />
            <path d="M3 7l9 6 9-6" />
          </svg>
          Meus Ingressos
        </button>
      </nav>

      {/* Conteúdo das abas */}
      {abaAtiva === 'filmes' && (
        <section className="secao-filmes">
          {carregando ? (
            <div className="loading-state">
              <div className="spinner"></div>
              <p>Carregando filmes...</p>
            </div>
          ) : filmes.length === 0 ? (
            <div className="empty-state">
              <p>Nenhum filme em cartaz no momento</p>
            </div>
          ) : (
            <div className="filmes-lista">
              {filmes.map((filme) => {
                const sessoes = sessoesPorFilme[filme.id] || [];
                return (
                  <article key={filme.id} className="filme-card-novo">
                    <div
                      className="filme-poster"
                      style={{ backgroundImage: `url(${getPoster(filme.imagemUrl)})` }}
                    >
                      <div className="poster-overlay" />
                    </div>

                    <div className="filme-info">
                      <div className="filme-cabecalho">
                        <h3 className="filme-titulo">{filme.titulo}</h3>
                        <span className="filme-classificacao">{filme.classificacaoEtaria}</span>
                      </div>
                      
                      <p className="filme-codigo">Código: {filme.codigo || `ASTRA${String(filme.id).padStart(3, '0')}`}</p>
                      
                      <p className="filme-sinopse">{filme.sinopse}</p>
                      
                      <div className="filme-detalhes">
                        <span>{filme.duracao} min</span>
                        <span>{filme.status}</span>
                      </div>

                      {sessoes.length > 0 && (
                        <div className="sessoes-disponiveis">
                          <h4>Próximas Sessões</h4>
                          <div className="sessoes-grid-novo">
                            {sessoes.slice(0, 6).map((sessao) => (
                              <button
                                key={sessao.id}
                                className="sessao-card-novo"
                                onClick={() => onIniciarCompra(sessao, filme)}
                              >
                                <div className="sessao-data">
                                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                    <rect x="3" y="4" width="18" height="18" rx="2" />
                                    <path d="M16 2v4M8 2v4M3 10h18" />
                                  </svg>
                                  {formatarData(sessao.horario)}
                                </div>
                                <div className="sessao-hora">
                                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                    <circle cx="12" cy="12" r="10" />
                                    <path d="M12 6v6l4 2" />
                                  </svg>
                                  {formatarHora(sessao.horario)}
                                </div>
                                <div className="sessao-sala">
                                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                    <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z" />
                                  </svg>
                                  {sessao.sala}
                                </div>
                                <div className="sessao-assento">
                                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                    <path d="M4 19v-9a2 2 0 012-2h12a2 2 0 012 2v9M4 10V8a2 2 0 012-2h12a2 2 0 012 2v2" />
                                  </svg>
                                  Assento {sessao.assentosDisponiveis > 0 ? `D${sessao.id % 16 + 1}` : '-'}
                                </div>
                              </button>
                            ))}
                          </div>
                        </div>
                      )}
                    </div>
                  </article>
                );
              })}
            </div>
          )}
          
          {/* Alerta de cancelamento */}

        </section>
      )}

      {abaAtiva === 'ingressos' && (
        <section className="secao-ingressos">
          <div className="ingressos-header">
            <h2>Meus Ingressos</h2>
            <p>Gerencie seus ingressos comprados</p>
          </div>

          {ingressos.length === 0 ? (
            <div className="empty-state">
              <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
              <p>Você ainda não possui ingressos</p>
              <button className="btn-primary-novo" onClick={() => setAbaAtiva('filmes')}>
                Ver filmes em cartaz
              </button>
            </div>
          ) : (
            <div className="ingressos-lista">
              {ingressos.map((ingresso) => (
                <article key={ingresso.id} className="ingresso-card-novo">
                  <div className="ingresso-header-novo">
                    <div>
                      <h3>{ingresso.filme?.titulo || 'Filme'}</h3>
                      <p className="ingresso-codigo-pequeno">Código: {ingresso.codigo}</p>
                    </div>
                    <span className={`badge-status ${ingresso.status === 'VALIDADO' ? 'validado' : ingresso.status?.toLowerCase()}`}>
                      {ingresso.status === 'VALIDADO' ? 'VALIDADO' :
                        ingresso.status === 'VALIDADO' ? 'VALIDADO' :
                        ingresso.status === 'CONFIRMADO' ? '✓ Ativo' :
                        ingresso.status === 'PENDENTE' ? 'Pendente' :
                        ingresso.status}
                    </span>
                  </div>

                  <div className="ingresso-detalhes-novo">
                    <div className="detalhe-item">
                      <span className="label">Data:</span>
                      <span className="valor">{formatarData(ingresso.sessao?.horario || ingresso.dataCompra)}</span>
                    </div>
                    <div className="detalhe-item">
                      <span className="label">Horário:</span>
                      <span className="valor">{formatarHora(ingresso.sessao?.horario || ingresso.dataCompra)}</span>
                    </div>
                    <div className="detalhe-item">
                      <span className="label">Sala:</span>
                      <span className="valor">{ingresso.sessao?.sala || 'Sala 1'}</span>
                    </div>
                    <div className="detalhe-item">
                      <span className="label">Assento:</span>
                      <span className="valor">{ingresso.assentos?.join(', ') || 'D5'}</span>
                    </div>
                  </div>

                  <div className="ingresso-footer-novo">
                    <div className="ingresso-tipo">
                      <span className="label">Inteira</span>
                    </div>
                    <div className="ingresso-preco">
                      <span className="valor-ingresso">R$ {(ingresso.total || 35).toFixed(2)}</span>
                    </div>
                  </div>

                  <button 
                    className="btn-qr-code-novo"
                    onClick={() => setIngressoQrAberto(ingresso)}
                  >
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <rect x="3" y="3" width="18" height="18" rx="2" />
                      <path d="M7 7h.01M7 12h.01M7 17h.01M12 7h.01M12 12h.01M12 17h.01M17 7h.01M17 12h.01M17 17h.01" />
                    </svg>
                    QR Code
                  </button>
                </article>
              ))}
            </div>
          )}
        </section>
      )}

      {/* Modal QR Code - IGUAL AO PROTÓTIPO */}
      {ingressoQrAberto && (
        <div className="modal-qr-overlay" onClick={() => setIngressoQrAberto(null)}>
          <div className="modal-qr-content" onClick={(e) => e.stopPropagation()}>
            <button className="modal-qr-close" onClick={() => setIngressoQrAberto(null)}>
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M18 6L6 18M6 6l12 12" />
              </svg>
            </button>

            <div className="modal-qr-header">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                <rect x="3" y="3" width="18" height="18" rx="2" />
                <path d="M7 7h.01M7 12h.01M7 17h.01M12 7h.01M12 12h.01M12 17h.01M17 7h.01M17 12h.01M17 17h.01" />
              </svg>
              <h3>QR Code do Ingresso</h3>
              <p>Apresente este código na entrada do cinema</p>
            </div>

            <div className="modal-qr-code">
              {ingressoQrAberto.qrCode ? (
                <img src={ingressoQrAberto.qrCode} alt="QR Code" />
              ) : (
                <div className="qr-placeholder-modal">
                  <svg width="200" height="200" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="0.5">
                    <rect x="2" y="2" width="20" height="20" rx="2" />
                    <rect x="5" y="5" width="4" height="4" fill="currentColor" />
                    <rect x="15" y="5" width="4" height="4" fill="currentColor" />
                    <rect x="5" y="15" width="4" height="4" fill="currentColor" />
                    <rect x="11" y="11" width="2" height="2" fill="currentColor" />
                  </svg>
                </div>
              )}
            </div>

            <div className="modal-qr-info">
              <h4>{ingressoQrAberto.filme?.titulo}</h4>
              <p className="qr-codigo">Código: {ingressoQrAberto.codigo}</p>
              
              <div className="qr-detalhes-grid">
                <div className="qr-detalhe">
                  <span className="label">Data:</span>
                  <span className="valor">{formatarData(ingressoQrAberto.sessao?.horario || ingressoQrAberto.dataCompra)}</span>
                </div>
                <div className="qr-detalhe">
                  <span className="label">Horário:</span>
                  <span className="valor">{formatarHora(ingressoQrAberto.sessao?.horario || ingressoQrAberto.dataCompra)}</span>
                </div>
                <div className="qr-detalhe">
                  <span className="label">Sala:</span>
                  <span className="valor">{ingressoQrAberto.sessao?.sala || 'Sala 1'}</span>
                </div>
                <div className="qr-detalhe">
                  <span className="label">Assento:</span>
                  <span className="valor">{ingressoQrAberto.assentos?.join(', ') || 'D5'}</span>
                </div>
              </div>

              <div className="qr-preco-final">
                <span>Inteira</span>
                <span className="preco">R$ {(ingressoQrAberto.total || 35).toFixed(2)}</span>
              </div>
            </div>

            <div className="modal-qr-acoes">
              <button className="btn-secondary-novo">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M7 10l5 5 5-5M12 15V3" />
                </svg>
                Baixar
              </button>
              <button className="btn-primary-novo">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="18" cy="5" r="3" />
                  <circle cx="6" cy="12" r="3" />
                  <circle cx="18" cy="19" r="3" />
                  <path d="M8.59 13.51l6.83 3.98M15.41 6.51l-6.82 3.98" />
                </svg>
                Compartilhar
              </button>
            </div>

            <div className="modal-qr-avisos">
              <div className="aviso-item">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <span>Chegue 15 minutos antes da sessão</span>
              </div>
              <div className="aviso-item">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M3 3h18M3 7h18M3 11h18M3 15h18M3 19h18" />
                </svg>
                <span>Apresente este QR Code na entrada</span>
              </div>
              <div className="aviso-item">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M12 2a10 10 0 100 20 10 10 0 000-20zM9 12h6" />
                </svg>
                <span>Tenha documento de identidade em mãos</span>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default HomeCliente;

import React, { useState, useEffect } from 'react';
import './GerenciarFilmes.css';

const GerenciarFilmes = ({ usuario }) => {
  const [filmes, setFilmes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filmeSelecionado, setFilmeSelecionado] = useState(null);
  const [mostrarModal, setMostrarModal] = useState(false);
  const [processando, setProcessando] = useState(false);

  useEffect(() => {
    carregarFilmes();
  }, []);

  const carregarFilmes = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await fetch('/api/filmes/em-cartaz');
      
      if (!response.ok) {
        throw new Error('Erro ao carregar filmes');
      }
      
      const data = await response.json();
      setFilmes(data);
    } catch (err) {
      setError('Não foi possível carregar os filmes. Tente novamente.');
      console.error('Erro ao carregar filmes:', err);
    } finally {
      setLoading(false);
    }
  };

  const verificarPodeRemover = async (filmeId) => {
    try {
      const response = await fetch(`/api/filmes/${filmeId}/pode-remover`);
      const data = await response.json();
      return data.podeRemover;
    } catch (err) {
      console.error('Erro ao verificar:', err);
      return false;
    }
  };

  const abrirModalRemocao = async (filme) => {
    setFilmeSelecionado(filme);
    const podeRemover = await verificarPodeRemover(filme.id);
    setFilmeSelecionado({ ...filme, podeRemover });
    setMostrarModal(true);
  };

  const fecharModal = () => {
    setMostrarModal(false);
    setFilmeSelecionado(null);
  };

  const confirmarRemocao = async () => {
    if (!filmeSelecionado || !filmeSelecionado.podeRemover) {
      return;
    }

    try {
      setProcessando(true);
      
      const response = await fetch(`/api/filmes/${filmeSelecionado.id}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          funcionarioId: usuario.id,
          funcao: usuario.funcao,
        }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.erro || 'Erro ao remover filme');
      }

      // Sucesso - recarrega a lista
      await carregarFilmes();
      fecharModal();
      
      // Mostra mensagem de sucesso
      alert(' Filme removido do catálogo com sucesso!');
    } catch (err) {
      alert(' Erro: ' + err.message);
      console.error('Erro ao remover filme:', err);
    } finally {
      setProcessando(false);
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Carregando filmes...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container">
        <div className="error-icon"></div>
        <p>{error}</p>
        <button onClick={carregarFilmes} className="btn-retry">
          Tentar Novamente
        </button>
      </div>
    );
  }

  return (
    <div className="gerenciar-filmes">
      <div className="header-section">
        <div>
          <h2>Filmes em Cartaz</h2>
          <p className="subtitle">
            Gerencie os filmes disponíveis no catálogo do cinema
          </p>
        </div>
        <button onClick={carregarFilmes} className="btn-refresh">
           Atualizar
        </button>
      </div>

      {filmes.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icon"></div>
          <h3>Nenhum filme em cartaz</h3>
          <p>Não há filmes disponíveis no momento.</p>
        </div>
      ) : (
        <div className="filmes-grid">
          {filmes.map((filme) => (
            <div key={filme.id} className="filme-card">
              <div className="filme-poster">
                <div className="poster-placeholder">
                  <span className="poster-icon"></span>
                </div>
                <div className="filme-status">
                  <span className="status-badge em-cartaz">
                    {filme.status}
                  </span>
                </div>
              </div>
              
              <div className="filme-info">
                <h3 className="filme-titulo">{filme.titulo}</h3>
                
                <div className="filme-detalhes">
                  <div className="detalhe-item">
                    <span className="detalhe-label">Gênero:</span>
                    <span className="detalhe-valor">{filme.genero}</span>
                  </div>
                  <div className="detalhe-item">
                    <span className="detalhe-label">Duração:</span>
                    <span className="detalhe-valor">{filme.duracao} min</span>
                  </div>
                  <div className="detalhe-item">
                    <span className="detalhe-label">Classificação:</span>
                    <span className="classificacao-badge">
                      {filme.classificacaoEtaria}
                    </span>
                  </div>
                </div>

                {filme.sinopse && (
                  <p className="filme-sinopse">
                    {filme.sinopse.length > 100
                      ? filme.sinopse.substring(0, 100) + '...'
                      : filme.sinopse}
                  </p>
                )}

                <button
                  onClick={() => abrirModalRemocao(filme)}
                  className="btn-remover"
                >
                   Remover do Catálogo
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modal de Confirmação */}
      {mostrarModal && filmeSelecionado && (
        <div className="modal-overlay" onClick={fecharModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Remover Filme</h3>
              <button className="btn-close" onClick={fecharModal}>
                
              </button>
            </div>

            <div className="modal-body">
              <div className="filme-preview">
                <div className="preview-icon"></div>
                <div>
                  <h4>{filmeSelecionado.titulo}</h4>
                  <p>{filmeSelecionado.genero} • {filmeSelecionado.duracao} min</p>
                </div>
              </div>

              {filmeSelecionado.podeRemover ? (
                <div className="alert alert-warning">
                  <div className="alert-icon"></div>
                  <div>
                    <p><strong>Atenção!</strong></p>
                    <p>
                      Tem certeza que deseja remover este filme do catálogo?
                      O filme será marcado como RETIRADO e não poderá ser exibido.
                    </p>
                  </div>
                </div>
              ) : (
                <div className="alert alert-error">
                  <div className="alert-icon"></div>
                  <div>
                    <p><strong>Não é possível remover!</strong></p>
                    <p>
                      Este filme possui sessões futuras agendadas. 
                      Cancele ou aguarde a conclusão das sessões antes de removê-lo.
                    </p>
                  </div>
                </div>
              )}
            </div>

            <div className="modal-footer">
              <button
                onClick={fecharModal}
                className="btn-secondary"
                disabled={processando}
              >
                Cancelar
              </button>
              <button
                onClick={confirmarRemocao}
                className="btn-danger"
                disabled={!filmeSelecionado.podeRemover || processando}
              >
                {processando ? 'Removendo...' : 'Confirmar Remoção'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default GerenciarFilmes;

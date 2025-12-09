import React, { useState, useEffect, useCallback } from 'react';
import { RefreshCw, Film, Clock, Users, AlertTriangle, CheckCircle, Calendar, MapPin, Search, ChevronRight, ChevronDown, Armchair, X, Ticket, ArrowRight, Printer, Share2 } from 'lucide-react';
import Modal from '../shared/Modal';
import { formatarDataHora, formatarMoeda } from '../../utils/formatters';
import './RemarcarNovo.css';

/**
 * Componente de Mapa de Assentos Visual
 */
const MapaAssentos = ({ assentos, selecionados, onSelecionar, modo = 'selecao', quantidadeMaxima = null }) => {
  // Organiza assentos por fileira (A, B, C, etc.)
  const organizarPorFileira = () => {
    const fileiras = {};
    Object.entries(assentos).forEach(([id, disponivel]) => {
      const fileira = id.charAt(0);
      if (!fileiras[fileira]) {
        fileiras[fileira] = [];
      }
      fileiras[fileira].push({ id, disponivel });
    });

    // Ordena assentos dentro de cada fileira
    Object.keys(fileiras).forEach(fileira => {
      fileiras[fileira].sort((a, b) => {
        const numA = parseInt(a.id.substring(1));
        const numB = parseInt(b.id.substring(1));
        return numA - numB;
      });
    });

    return fileiras;
  };

  const fileiras = organizarPorFileira();
  const fileirasOrdenadas = Object.keys(fileiras).sort();

  const handleClick = (assento) => {
    if (!assento.disponivel) return;

    const jaSelecionado = selecionados.includes(assento.id);

    if (jaSelecionado) {
      onSelecionar(selecionados.filter(id => id !== assento.id));
    } else {
      if (quantidadeMaxima && selecionados.length >= quantidadeMaxima) {
        // Substitui o primeiro selecionado
        onSelecionar([...selecionados.slice(1), assento.id]);
      } else {
        onSelecionar([...selecionados, assento.id]);
      }
    }
  };

  return (
    <div className="mapa-assentos-container">
      <div className="mapa-tela">
        <div className="tela-cinema">TELA</div>
      </div>

      <div className="mapa-grid">
        {fileirasOrdenadas.map(fileira => (
          <div key={fileira} className="fileira">
            <span className="fileira-label">{fileira}</span>
            <div className="assentos-fileira">
              {fileiras[fileira].map(assento => {
                const selecionado = selecionados.includes(assento.id);
                return (
                  <button
                    key={assento.id}
                    className={`assento-visual ${!assento.disponivel ? 'ocupado' : ''} ${selecionado ? 'selecionado' : ''}`}
                    onClick={() => handleClick(assento)}
                    disabled={!assento.disponivel}
                    title={assento.disponivel ? `Assento ${assento.id}` : `Assento ${assento.id} (Ocupado)`}
                  >
                    <Armchair size={18} />
                    <span className="assento-numero">{assento.id.substring(1)}</span>
                  </button>
                );
              })}
            </div>
            <span className="fileira-label">{fileira}</span>
          </div>
        ))}
      </div>

      <div className="mapa-legenda">
        <div className="legenda-item">
          <div className="legenda-box disponivel"></div>
          <span>Disponível</span>
        </div>
        <div className="legenda-item">
          <div className="legenda-box ocupado"></div>
          <span>Ocupado</span>
        </div>
        <div className="legenda-item">
          <div className="legenda-box selecionado"></div>
          <span>Selecionado</span>
        </div>
      </div>
    </div>
  );
};

/**
 * Componente Principal de Remarcação
 */
const RemarcarNovo = () => {
  // Estados principais
  const [etapa, setEtapa] = useState(1); // 1: Buscar compra, 2: Selecionar ingressos, 3: Escolher sessão, 4: Selecionar assentos
  const [carregando, setCarregando] = useState(false);

  // Estados de busca
  const [termoBusca, setTermoBusca] = useState('');
  const [comprasEncontradas, setComprasEncontradas] = useState([]);
  const [compraExpandida, setCompraExpandida] = useState(null);

  // Estados de seleção
  const [compraSelecionada, setCompraSelecionada] = useState(null);
  const [ingressosSelecionados, setIngressosSelecionados] = useState([]);
  const [motivoTecnico, setMotivoTecnico] = useState('');

  // Estados de sessão destino
  const [filmes, setFilmes] = useState([]);
  const [sessoes, setSessoes] = useState([]);
  const [filmeExpandido, setFilmeExpandido] = useState(null);
  const [sessaoDestino, setSessaoDestino] = useState(null);

  // Estados de mapa de assentos
  const [mapaAssentos, setMapaAssentos] = useState({});
  const [assentosSelecionados, setAssentosSelecionados] = useState([]);

  // Estados para modal de sucesso
  const [modalSucesso, setModalSucesso] = useState(false);
  const [dadosRemarcacao, setDadosRemarcacao] = useState(null);

  // Carregar dados iniciais
  useEffect(() => {
    carregarComprasAtivas();
    carregarFilmesESessoes();
  }, []);

  const carregarComprasAtivas = async () => {
    try {
      const response = await fetch('/api/funcionario/compras/ativas');
      if (response.ok) {
        const data = await response.json();
        // Enriquecer compras com informações de sessão e filme
        const comprasEnriquecidas = await Promise.all(data.map(async (compra) => {
          if (compra.ingressos && compra.ingressos.length > 0) {
            const primeiroIngresso = compra.ingressos[0];
            try {
              const sessaoRes = await fetch(`/api/sessoes/${primeiroIngresso.sessaoId}`);
              if (sessaoRes.ok) {
                const sessaoData = await sessaoRes.json();
                compra.sessaoInfo = sessaoData;
              }
            } catch (e) {
              console.error('Erro ao carregar sessão:', e);
            }
          }
          return compra;
        }));
        setComprasEncontradas(comprasEnriquecidas);
      }
    } catch (error) {
      console.error('Erro ao carregar compras:', error);
    }
  };

  const carregarFilmesESessoes = async () => {
    try {
      const [resFilmes, resSessoes] = await Promise.all([
        fetch('/api/filmes/em-cartaz'),
        fetch('/api/sessoes')
      ]);

      if (resFilmes.ok) {
        const filmesData = await resFilmes.json();
        setFilmes(filmesData);
      }

      if (resSessoes.ok) {
        const sessoesData = await resSessoes.json();
        // Filtrar apenas sessões futuras e disponíveis
        const agora = new Date();
        const sessoesFuturas = sessoesData.filter(s =>
          new Date(s.horario) > agora && s.status === 'DISPONIVEL'
        );
        setSessoes(sessoesFuturas);
      }
    } catch (error) {
      console.error('Erro ao carregar filmes e sessões:', error);
    }
  };

  const buscarCompra = async () => {
    if (!termoBusca.trim()) return;

    setCarregando(true);
    try {
      const response = await fetch(`/api/funcionario/compras/buscar?termo=${encodeURIComponent(termoBusca)}`);
      if (response.ok) {
        const data = await response.json();
        setComprasEncontradas(data);
      }
    } catch (error) {
      console.error('Erro ao buscar compra:', error);
    } finally {
      setCarregando(false);
    }
  };

  const selecionarCompra = (compra) => {
    setCompraSelecionada(compra);
    setIngressosSelecionados([]);
    setEtapa(2);
  };

  const toggleIngressoSelecionado = (ingresso) => {
    const jaExiste = ingressosSelecionados.find(i => i.id === ingresso.id);
    if (jaExiste) {
      setIngressosSelecionados(ingressosSelecionados.filter(i => i.id !== ingresso.id));
    } else {
      setIngressosSelecionados([...ingressosSelecionados, ingresso]);
    }
  };

  const avancarParaSessoes = () => {
    if (ingressosSelecionados.length === 0) {
      alert('Selecione pelo menos um ingresso para remarcar');
      return;
    }
    if (!motivoTecnico.trim()) {
      alert('Por favor, descreva o motivo técnico da remarcação');
      return;
    }
    setEtapa(3);
  };

  const selecionarSessaoDestino = async (sessao) => {
    setSessaoDestino(sessao);
    setCarregando(true);

    try {
      const response = await fetch(`/api/sessoes/${sessao.id}/assentos`);
      if (response.ok) {
        const data = await response.json();
        setMapaAssentos(data.assentos || {});
        setAssentosSelecionados([]);
        setEtapa(4);
      }
    } catch (error) {
      console.error('Erro ao carregar assentos:', error);
      alert('Erro ao carregar mapa de assentos');
    } finally {
      setCarregando(false);
    }
  };

  const confirmarRemarcacao = async () => {
    if (assentosSelecionados.length !== ingressosSelecionados.length) {
      alert(`Selecione ${ingressosSelecionados.length} assento(s) para os ingressos remarcados`);
      return;
    }

    setCarregando(true);
    try {
      // Remarcar cada ingresso
      const remarcacoes = ingressosSelecionados.map((ingresso, index) => ({
        ingressoId: ingresso.id,
        novaSessaoId: sessaoDestino.id,
        novoAssentoId: assentosSelecionados[index],
        motivoTecnico: motivoTecnico
      }));

      const response = await fetch('/api/funcionario/ingressos/remarcar-multiplos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ remarcacoes })
      });

      const data = await response.json();

      if (response.ok) {
        // Preparar dados para o modal de sucesso
        const filmeDestino = filmes.find(f => f.id === sessaoDestino.filmeId);
        setDadosRemarcacao({
          quantidade: remarcacoes.length,
          ingressos: ingressosSelecionados.map((ing, idx) => ({
            qrCode: ing.qrCode,
            assentoOriginal: ing.assento,
            novoAssento: assentosSelecionados[idx]
          })),
          sessaoOriginal: compraSelecionada?.sessaoInfo,
          sessaoDestino: {
            ...sessaoDestino,
            filmeTitulo: filmeDestino?.titulo || `Filme #${sessaoDestino.filmeId}`
          },
          motivo: motivoTecnico,
          dataRemarcacao: new Date().toISOString(),
          clienteNome: compraSelecionada?.clienteNome || 'Cliente'
        });
        setModalSucesso(true);
      } else {
        alert(`Erro: ${data.erro || 'Falha ao remarcar ingressos'}`);
      }
    } catch (error) {
      console.error('Erro ao remarcar:', error);
      alert('Erro de conexão com o servidor');
    } finally {
      setCarregando(false);
    }
  };

  const fecharModalSucesso = () => {
    setModalSucesso(false);
    setDadosRemarcacao(null);
    resetarFluxo();
  };

  const resetarFluxo = () => {
    setEtapa(1);
    setCompraSelecionada(null);
    setIngressosSelecionados([]);
    setMotivoTecnico('');
    setSessaoDestino(null);
    setMapaAssentos({});
    setAssentosSelecionados([]);
    setFilmeExpandido(null);
    carregarComprasAtivas();
  };

  const voltarEtapa = () => {
    if (etapa === 4) {
      setSessaoDestino(null);
      setMapaAssentos({});
      setAssentosSelecionados([]);
      setEtapa(3);
    } else if (etapa === 3) {
      setEtapa(2);
    } else if (etapa === 2) {
      setCompraSelecionada(null);
      setIngressosSelecionados([]);
      setMotivoTecnico('');
      setEtapa(1);
    }
  };

  // Agrupa sessões por filme
  const sessoesPorFilme = () => {
    const agrupado = {};
    sessoes.forEach(sessao => {
      const filme = filmes.find(f => f.id === sessao.filmeId);
      const filmeId = sessao.filmeId;
      if (!agrupado[filmeId]) {
        agrupado[filmeId] = {
          filme: filme || { id: filmeId, titulo: `Filme #${filmeId}` },
          sessoes: []
        };
      }
      agrupado[filmeId].sessoes.push(sessao);
    });
    return Object.values(agrupado);
  };

  // Renderização da Etapa 1: Buscar/Selecionar Compra
  const renderEtapa1 = () => (
    <div className="etapa-container">
      <div className="etapa-header">
        <div className="etapa-numero">1</div>
        <div className="etapa-info">
          <h3>Buscar Compra</h3>
          <p>Busque por QR Code, nome do cliente ou ID da compra</p>
        </div>
      </div>

      <div className="busca-container">
        <div className="busca-input-wrapper">
          <Search size={20} />
          <input
            type="text"
            value={termoBusca}
            onChange={(e) => setTermoBusca(e.target.value)}
            placeholder="Digite QR Code, nome ou ID..."
            onKeyPress={(e) => e.key === 'Enter' && buscarCompra()}
          />
          <button onClick={buscarCompra} disabled={carregando}>
            {carregando ? 'Buscando...' : 'Buscar'}
          </button>
        </div>
      </div>

      <div className="compras-lista">
        <h4>Compras com Ingressos Ativos ({comprasEncontradas.length})</h4>

        {comprasEncontradas.length === 0 ? (
          <div className="empty-state">
            <Users size={48} />
            <p>Nenhuma compra encontrada</p>
          </div>
        ) : (
          comprasEncontradas.map(compra => (
            <div
              key={compra.id}
              className={`compra-card ${compraExpandida === compra.id ? 'expandida' : ''}`}
            >
              <div
                className="compra-header"
                onClick={() => setCompraExpandida(compraExpandida === compra.id ? null : compra.id)}
              >
                <div className="compra-info-completa">
                  <div className="compra-linha-principal">
                    <span className="compra-id">Compra #{compra.id}</span>
                    <span className="compra-cliente">{compra.clienteNome || 'Cliente desconhecido'}</span>
                    <span className="compra-ingressos">{compra.ingressos?.length || 0} ingresso(s)</span>
                  </div>

                  {compra.sessaoInfo && (
                    <div className="compra-sessao-info">
                      <div className="info-badge filme">
                        <Film size={14} />
                        <span>{compra.sessaoInfo.filme?.titulo || compra.sessaoInfo.filmeTitulo || `Filme #${compra.sessaoInfo.filmeId}`}</span>
                      </div>
                      <div className="info-badge horario">
                        <Calendar size={14} />
                        <span>{formatarDataHora(compra.sessaoInfo.horario)}</span>
                      </div>
                      <div className="info-badge sala">
                        <MapPin size={14} />
                        <span>{compra.sessaoInfo.sala}</span>
                      </div>
                    </div>
                  )}

                  {compra.ingressos && compra.ingressos.length > 0 && (
                    <div className="compra-assentos-linha">
                      <Users size={14} />
                      <span>Assentos: {compra.ingressos.map(i => i.assento).join(', ')}</span>
                    </div>
                  )}
                </div>
                <div className="compra-actions">
                  <button
                    className="btn-selecionar"
                    onClick={(e) => { e.stopPropagation(); selecionarCompra(compra); }}
                  >
                    Selecionar <ChevronRight size={16} />
                  </button>
                  {compraExpandida === compra.id ? <ChevronDown size={20} /> : <ChevronRight size={20} />}
                </div>
              </div>

              {compraExpandida === compra.id && compra.ingressos && (
                <div className="compra-ingressos-preview">
                  {compra.ingressos.map(ing => (
                    <div key={ing.id} className="ingresso-preview">
                      <div className="preview-qr">{ing.qrCode}</div>
                      <div className="preview-detalhes">
                        <span className="preview-assento">
                          <MapPin size={12} />
                          Assento: {ing.assento}
                        </span>
                        <span className="preview-tipo">{ing.tipo}</span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );

  // Renderização da Etapa 2: Selecionar Ingressos
  const renderEtapa2 = () => (
    <div className="etapa-container">
      <div className="etapa-header">
        <div className="etapa-numero">2</div>
        <div className="etapa-info">
          <h3>Selecionar Ingressos para Remarcar</h3>
          <p>Compra #{compraSelecionada?.id} - Selecione quais ingressos deseja remarcar</p>
        </div>
      </div>

      {compraSelecionada?.sessaoInfo && (
        <div className="compra-contexto-box">
          <h4>Sessão Atual</h4>
          <div className="contexto-info">
            <div className="contexto-badge">
              <Film size={18} />
              <span>{compraSelecionada.sessaoInfo.filme?.titulo || compraSelecionada.sessaoInfo.filmeTitulo || `Filme #${compraSelecionada.sessaoInfo.filmeId}`}</span>
            </div>
            <div className="contexto-badge">
              <Calendar size={18} />
              <span>{formatarDataHora(compraSelecionada.sessaoInfo.horario)}</span>
            </div>
            <div className="contexto-badge">
              <MapPin size={18} />
              <span>{compraSelecionada.sessaoInfo.sala}</span>
            </div>
            {compraSelecionada.sessaoInfo.filme?.duracao && (
              <div className="contexto-badge">
                <Clock size={18} />
                <span>{compraSelecionada.sessaoInfo.filme.duracao} min</span>
              </div>
            )}
          </div>
        </div>
      )}

      <div className="motivo-container">
        <label>Motivo Técnico da Remarcação *</label>
        <textarea
          value={motivoTecnico}
          onChange={(e) => setMotivoTecnico(e.target.value)}
          placeholder="Descreva o motivo técnico (ex: problema no projetor, ar-condicionado com defeito, etc.)"
          rows={3}
        />
      </div>

      <div className="ingressos-selecao">
        <h4>Ingressos da Compra</h4>
        <p className="hint">Clique para selecionar/desselecionar os ingressos que deseja remarcar</p>

        <div className="ingressos-grid">
          {compraSelecionada?.ingressos?.map(ingresso => {
            const selecionado = ingressosSelecionados.find(i => i.id === ingresso.id);
            return (
              <div
                key={ingresso.id}
                className={`ingresso-card ${selecionado ? 'selecionado' : ''}`}
                onClick={() => toggleIngressoSelecionado(ingresso)}
              >
                <div className="ingresso-check">
                  {selecionado && <CheckCircle size={24} />}
                </div>
                <div className="ingresso-detalhes">
                  <div className="qr-code">{ingresso.qrCode}</div>
                  <div className="info-row">
                    <MapPin size={14} />
                    <span>Assento: {ingresso.assento}</span>
                  </div>
                  <div className="info-row">
                    <Clock size={14} />
                    <span>{ingresso.tipo}</span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      <div className="etapa-footer">
        <button className="btn-voltar" onClick={voltarEtapa}>
          ← Voltar
        </button>
        <div className="selecao-info">
          {ingressosSelecionados.length} ingresso(s) selecionado(s)
        </div>
        <button
          className="btn-avancar"
          onClick={avancarParaSessoes}
          disabled={ingressosSelecionados.length === 0 || !motivoTecnico.trim()}
        >
          Escolher Nova Sessão →
        </button>
      </div>
    </div>
  );

  // Renderização da Etapa 3: Escolher Sessão de Destino
  const renderEtapa3 = () => (
    <div className="etapa-container">
      <div className="etapa-header">
        <div className="etapa-numero">3</div>
        <div className="etapa-info">
          <h3>Escolher Nova Sessão</h3>
          <p>Remarcando {ingressosSelecionados.length} ingresso(s) - Selecione a sessão de destino</p>
        </div>
      </div>

      <div className="sessoes-por-filme">
        {sessoesPorFilme().map(({ filme, sessoes: sessoeFilme }) => (
          <div key={filme.id} className="filme-grupo">
            <div
              className="filme-header-grupo"
              onClick={() => setFilmeExpandido(filmeExpandido === filme.id ? null : filme.id)}
            >
              <div className="filme-info-grupo">
                <Film size={24} />
                <h4>{filme.titulo}</h4>
                <span className="badge-sessoes">{sessoeFilme.length} sessão(ões)</span>
              </div>
              {filmeExpandido === filme.id ? <ChevronDown size={24} /> : <ChevronRight size={24} />}
            </div>

            {filmeExpandido === filme.id && (
              <div className="sessoes-lista-filme">
                {sessoeFilme.map(sessao => (
                  <div
                    key={sessao.id}
                    className="sessao-destino-card"
                    onClick={() => selecionarSessaoDestino(sessao)}
                  >
                    <div className="sessao-destino-info">
                      <div className="sessao-sala">
                        <MapPin size={16} />
                        <span>{sessao.sala}</span>
                      </div>
                      <div className="sessao-horario">
                        <Calendar size={16} />
                        <span>{formatarDataHora(sessao.horario)}</span>
                      </div>
                      <div className="sessao-ocupacao">
                        <Users size={16} />
                        <span>{sessao.assentosDisponiveis || '?'} disponíveis</span>
                      </div>
                    </div>
                    <button className="btn-ver-mapa">
                      Ver Mapa de Assentos →
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>

      <div className="etapa-footer">
        <button className="btn-voltar" onClick={voltarEtapa}>
          ← Voltar
        </button>
      </div>
    </div>
  );

  // Renderização da Etapa 4: Selecionar Assentos no Mapa
  const renderEtapa4 = () => (
    <div className="etapa-container">
      <div className="etapa-header">
        <div className="etapa-numero">4</div>
        <div className="etapa-info">
          <h3>Selecionar Novos Assentos</h3>
          <p>
            {sessaoDestino?.filmeTitulo || filmes.find(f => f.id === sessaoDestino?.filmeId)?.titulo} - {sessaoDestino?.sala}
            <br />
            <small>{formatarDataHora(sessaoDestino?.horario)}</small>
          </p>
        </div>
      </div>

      <div className="mapa-instrucoes">
        <AlertTriangle size={20} />
        <span>
          Selecione <strong>{ingressosSelecionados.length}</strong> assento(s) para os ingressos remarcados
        </span>
      </div>

      <MapaAssentos
        assentos={mapaAssentos}
        selecionados={assentosSelecionados}
        onSelecionar={setAssentosSelecionados}
        quantidadeMaxima={ingressosSelecionados.length}
      />

      <div className="resumo-remarcacao">
        <h4>Resumo da Remarcação</h4>
        <div className="resumo-grid">
          {ingressosSelecionados.map((ing, idx) => (
            <div key={ing.id} className="resumo-item">
              <div className="de">
                <span className="label">DE:</span>
                <span>{ing.qrCode}</span>
                <span>Assento {ing.assento}</span>
              </div>
              <div className="seta">→</div>
              <div className="para">
                <span className="label">PARA:</span>
                <span>{assentosSelecionados[idx] || '(selecione)'}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      <div className="etapa-footer">
        <button className="btn-voltar" onClick={voltarEtapa}>
          ← Voltar
        </button>
        <button
          className="btn-confirmar-remarcacao"
          onClick={confirmarRemarcacao}
          disabled={assentosSelecionados.length !== ingressosSelecionados.length || carregando}
        >
          {carregando ? (
            <>
              <div className="spinner-small"></div>
              Remarcando...
            </>
          ) : (
            <>
              <RefreshCw size={18} />
              Confirmar Remarcação ({ingressosSelecionados.length} ingresso(s))
            </>
          )}
        </button>
      </div>
    </div>
  );

  return (
    <div className="remarcar-novo-container">
      <div className="remarcar-header-principal">
        <div className="header-icon">
          <RefreshCw size={36} />
        </div>
        <div className="header-texto">
          <h2>Remarcação de Ingressos</h2>
          <p>Remarque ingressos para qualquer filme e sessão disponível</p>
        </div>
      </div>

      {/* Indicador de Etapas */}
      <div className="etapas-indicador">
        <div className={`etapa-step ${etapa >= 1 ? 'ativo' : ''} ${etapa > 1 ? 'completo' : ''}`}>
          <div className="step-numero">1</div>
          <span>Buscar Compra</span>
        </div>
        <div className="step-linha"></div>
        <div className={`etapa-step ${etapa >= 2 ? 'ativo' : ''} ${etapa > 2 ? 'completo' : ''}`}>
          <div className="step-numero">2</div>
          <span>Selecionar Ingressos</span>
        </div>
        <div className="step-linha"></div>
        <div className={`etapa-step ${etapa >= 3 ? 'ativo' : ''} ${etapa > 3 ? 'completo' : ''}`}>
          <div className="step-numero">3</div>
          <span>Escolher Sessão</span>
        </div>
        <div className="step-linha"></div>
        <div className={`etapa-step ${etapa >= 4 ? 'ativo' : ''}`}>
          <div className="step-numero">4</div>
          <span>Mapa de Assentos</span>
        </div>
      </div>

      {/* Conteúdo da Etapa Atual */}
      <div className="etapa-content">
        {etapa === 1 && renderEtapa1()}
        {etapa === 2 && renderEtapa2()}
        {etapa === 3 && renderEtapa3()}
        {etapa === 4 && renderEtapa4()}
      </div>

      {/* Modal de Sucesso da Remarcação */}
      {modalSucesso && dadosRemarcacao && (
        <div className="modal-sucesso-overlay" onClick={fecharModalSucesso}>
          <div className="modal-sucesso-content" onClick={(e) => e.stopPropagation()}>
            <button className="modal-close-btn" onClick={fecharModalSucesso}>
              <X size={24} />
            </button>

            <div className="modal-sucesso-header">
              <div className="sucesso-icon-wrapper">
                <CheckCircle size={64} />
              </div>
              <h2>Remarcação Concluída!</h2>
              <p>{dadosRemarcacao.quantidade} ingresso(s) remarcado(s) com sucesso</p>
            </div>

            <div className="modal-sucesso-body">
              {/* Resumo da Transição */}
              <div className="transicao-visual">
                <div className="sessao-box origem">
                  <span className="sessao-label">Sessão Original</span>
                  <div className="sessao-info-box">
                    <Film size={18} />
                    <span>{dadosRemarcacao.sessaoOriginal?.filme?.titulo || dadosRemarcacao.sessaoOriginal?.filmeTitulo || 'Filme'}</span>
                  </div>
                  <div className="sessao-info-box">
                    <Calendar size={16} />
                    <span>{formatarDataHora(dadosRemarcacao.sessaoOriginal?.horario)}</span>
                  </div>
                  <div className="sessao-info-box">
                    <MapPin size={16} />
                    <span>{dadosRemarcacao.sessaoOriginal?.sala}</span>
                  </div>
                </div>

                <div className="transicao-seta">
                  <ArrowRight size={32} />
                </div>

                <div className="sessao-box destino">
                  <span className="sessao-label">Nova Sessão</span>
                  <div className="sessao-info-box">
                    <Film size={18} />
                    <span>{dadosRemarcacao.sessaoDestino?.filmeTitulo}</span>
                  </div>
                  <div className="sessao-info-box">
                    <Calendar size={16} />
                    <span>{formatarDataHora(dadosRemarcacao.sessaoDestino?.horario)}</span>
                  </div>
                  <div className="sessao-info-box">
                    <MapPin size={16} />
                    <span>{dadosRemarcacao.sessaoDestino?.sala}</span>
                  </div>
                </div>
              </div>

              {/* Lista de Ingressos Remarcados */}
              <div className="ingressos-remarcados-lista">
                <h4><Ticket size={18} /> Ingressos Remarcados</h4>
                {dadosRemarcacao.ingressos.map((ing, idx) => (
                  <div key={idx} className="ingresso-remarcado-item">
                    <span className="qr-badge">{ing.qrCode}</span>
                    <span className="assento-transicao">
                      <span className="assento-original">{ing.assentoOriginal}</span>
                      <ArrowRight size={14} />
                      <span className="assento-novo">{ing.novoAssento}</span>
                    </span>
                  </div>
                ))}
              </div>

              {/* Informações Adicionais */}
              <div className="info-adicional">
                <div className="info-row">
                  <span className="info-label">Cliente:</span>
                  <span className="info-valor">{dadosRemarcacao.clienteNome}</span>
                </div>
                <div className="info-row">
                  <span className="info-label">Motivo:</span>
                  <span className="info-valor">{dadosRemarcacao.motivo}</span>
                </div>
                <div className="info-row">
                  <span className="info-label">Data/Hora:</span>
                  <span className="info-valor">{formatarDataHora(dadosRemarcacao.dataRemarcacao)}</span>
                </div>
              </div>

              {/* Aviso ao Cliente */}
              <div className="aviso-cliente-box">
                <AlertTriangle size={20} />
                <div>
                  <strong>Importante:</strong>
                  <p>O cliente pode visualizar o ingresso atualizado na aba "Meus Ingressos" do aplicativo.</p>
                </div>
              </div>
            </div>

            <div className="modal-sucesso-footer">
              <button className="btn-imprimir" onClick={() => window.print()}>
                <Printer size={18} />
                Imprimir Comprovante
              </button>
              <button className="btn-nova-remarcacao" onClick={fecharModalSucesso}>
                <RefreshCw size={18} />
                Nova Remarcação
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default RemarcarNovo;

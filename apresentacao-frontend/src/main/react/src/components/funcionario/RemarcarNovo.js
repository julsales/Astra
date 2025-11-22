import React, { useState, useEffect } from 'react';
import { RefreshCw, Film, Clock, Users, AlertTriangle, CheckCircle, Calendar, MapPin } from 'lucide-react';
import Modal from '../shared/Modal';
import StatusBadge from '../shared/StatusBadge';
import { formatarDataHora, formatarMoeda } from '../../utils/formatters';
import './RemarcarNovo.css';

/**
 * Componente de Remarcação Inteligente de Ingressos
 * Agrupa sessões por filme e destaca sessões com problemas
 */
const RemarcarNovo = () => {
  const [filmes, setFilmes] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [filmeExpandido, setFilmeExpandido] = useState(null);
  const [sessaoSelecionada, setSessaoSelecionada] = useState(null);
  const [ingressoSelecionado, setIngressoSelecionado] = useState(null);
  const [modalRemarcar, setModalRemarcar] = useState(false);
  const [sessoesDestino, setSessoesDestino] = useState([]);
  const [motivoTecnico, setMotivoTecnico] = useState('');
  const [sessaoDestinoSelecionada, setSessaoDestinoSelecionada] = useState(null);
  const [assentosDisponiveis, setAssentosDisponiveis] = useState([]);
  const [assentoSelecionado, setAssentoSelecionado] = useState(null);
  const [carregandoAssentos, setCarregandoAssentos] = useState(false);

  useEffect(() => {
    carregarDados();
  }, []);

  const carregarDados = async () => {
    setCarregando(true);
    try {
      const response = await fetch('/api/funcionario/sessoes/para-remarcacao');
      if (response.ok) {
        const data = await response.json();
        setFilmes(data.filmes || []);
      }
    } catch (error) {
      console.error('Erro ao carregar dados:', error);
    } finally {
      setCarregando(false);
    }
  };

  const toggleFilme = (filmeId) => {
    setFilmeExpandido(filmeExpandido === filmeId ? null : filmeId);
  };

  const abrirModalRemarcacao = async (ingresso, sessao) => {
    setIngressoSelecionado(ingresso);
    setSessaoSelecionada(sessao);

    // Buscar TODAS as sessões disponíveis de TODOS os filmes (exceto a sessão atual)
    try {
      const response = await fetch('/api/sessoes');
      if (response.ok) {
        const todasSessoes = await response.json();

        // Filtrar apenas sessões disponíveis e futuras, excluindo a sessão atual
        const agora = new Date();
        const sessoesDisponiveis = todasSessoes.filter(s =>
          s.id !== sessao.sessaoId &&
          s.status === 'DISPONIVEL' &&
          new Date(s.horario) > agora
        );

        // Enriquecer com informações de filme
        const sessoesComFilme = await Promise.all(
          sessoesDisponiveis.map(async (s) => {
            try {
              const resFilme = await fetch(`/api/filmes/${s.filmeId}`);
              const filme = resFilme.ok ? await resFilme.json() : null;
              return {
                ...s,
                sessaoId: s.id,
                filmeTitulo: filme?.titulo || `Filme #${s.filmeId}`,
                statusSessao: s.status
              };
            } catch (err) {
              console.error('Erro ao buscar filme:', err);
              return {
                ...s,
                sessaoId: s.id,
                filmeTitulo: `Filme #${s.filmeId}`,
                statusSessao: s.status
              };
            }
          })
        );

        setSessoesDestino(sessoesComFilme);
      } else {
        console.error('Erro ao buscar sessões disponíveis');
        setSessoesDestino([]);
      }
    } catch (error) {
      console.error('Erro ao buscar sessões:', error);
      setSessoesDestino([]);
    }

    setModalRemarcar(true);
  };

  const selecionarSessaoDestino = async (sessao) => {
    setSessaoDestinoSelecionada(sessao);
    setCarregandoAssentos(true);

    try {
      // Buscar assentos disponíveis da sessão
      const response = await fetch(`/api/sessoes/${sessao.sessaoId}/assentos`);
      if (response.ok) {
        const data = await response.json();
        // Filtrar apenas assentos disponíveis
        const disponiveis = data.assentos ? data.assentos.filter(a => a.disponivel) : [];
        setAssentosDisponiveis(disponiveis);

        // Se houver o mesmo assento disponível, sugerir automaticamente
        const mesmoAssento = disponiveis.find(a => a.id === ingressoSelecionado.assento);
        if (mesmoAssento) {
          setAssentoSelecionado(mesmoAssento.id);
        } else if (disponiveis.length > 0) {
          // Caso contrário, selecionar o primeiro disponível
          setAssentoSelecionado(disponiveis[0].id);
        }
      } else {
        alert('Erro ao carregar assentos da sessão');
      }
    } catch (error) {
      console.error('Erro ao buscar assentos:', error);
      alert('Erro ao buscar assentos disponíveis');
    } finally {
      setCarregandoAssentos(false);
    }
  };

  const confirmarRemarcacao = async () => {
    if (!motivoTecnico.trim()) {
      alert('Por favor, informe o motivo técnico da remarcação');
      return;
    }

    if (!assentoSelecionado) {
      alert('Por favor, selecione um assento para a nova sessão');
      return;
    }

    try {
      const response = await fetch('/api/funcionario/ingressos/remarcar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ingressoId: ingressoSelecionado.id,
          novaSessaoId: sessaoDestinoSelecionada.sessaoId,
          novoAssentoId: assentoSelecionado,
          motivoTecnico: motivoTecnico
        })
      });

      const data = await response.json();

      if (response.ok && data.sucesso) {
        alert(`Ingresso remarcado com sucesso!\nNovo assento: ${assentoSelecionado}`);
        fecharModal();
        carregarDados();
      } else {
        alert(`Erro: ${data.erro || data.mensagem}`);
      }
    } catch (error) {
      console.error('Erro ao remarcar ingresso:', error);
      alert('Erro de conexão com o servidor');
    }
  };

  const fecharModal = () => {
    setModalRemarcar(false);
    setMotivoTecnico('');
    setIngressoSelecionado(null);
    setSessaoSelecionada(null);
    setSessaoDestinoSelecionada(null);
    setAssentosDisponiveis([]);
    setAssentoSelecionado(null);
  };

  const getStatusSessaoInfo = (statusSessao) => {
    switch (statusSessao) {
      case 'PROBLEMA_TECNICO':
        return { cor: 'red', icone: AlertTriangle, label: 'Problema Técnico' };
      case 'DISPONIVEL':
        return { cor: 'green', icone: CheckCircle, label: 'Disponível' };
      case 'ESGOTADA':
        return { cor: 'amber', icone: Users, label: 'Esgotada' };
      case 'CANCELADA':
        return { cor: 'gray', icone: AlertTriangle, label: 'Cancelada' };
      default:
        return { cor: 'blue', icone: Film, label: statusSessao };
    }
  };

  if (carregando) {
    return (
      <div className="remarcar-loading">
        <div className="spinner"></div>
        <p>Carregando dados de remarcação...</p>
      </div>
    );
  }

  if (filmes.length === 0) {
    return (
      <div className="remarcar-empty">
        <Film size={64} />
        <h3>Nenhuma sessão com ingressos</h3>
        <p>Não há ingressos ativos no momento</p>
      </div>
    );
  }

  return (
    <div className="remarcar-novo-container">
      <div className="remarcar-header-info">
        <div className="remarcar-header-icon">
          <RefreshCw size={32} />
        </div>
        <div>
          <h2>Remarcação Inteligente de Ingressos</h2>
          <p>Selecione uma sessão para ver seus ingressos e remarcar se necessário</p>
        </div>
      </div>

      <div className="remarcar-legenda">
        <h4>Legenda de Status:</h4>
        <div className="legenda-items">
          <div className="legenda-item problema">
            <AlertTriangle size={16} />
            <span>Problema Técnico - Necessita remarcação</span>
          </div>
          <div className="legenda-item disponivel">
            <CheckCircle size={16} />
            <span>Disponível - Funcionando normalmente</span>
          </div>
          <div className="legenda-item esgotada">
            <Users size={16} />
            <span>Esgotada - Sem assentos disponíveis</span>
          </div>
        </div>
      </div>

      <div className="filmes-lista">
        {filmes.map(filme => (
          <div key={filme.filmeId} className="filme-card">
            <div
              className="filme-header"
              onClick={() => toggleFilme(filme.filmeId)}
            >
              <div className="filme-info">
                <Film size={24} />
                <h3>{filme.filmeTitulo}</h3>
                <span className="sessoes-count">{filme.sessoes.length} sessões</span>
              </div>
              <div className={`expand-icon ${filmeExpandido === filme.filmeId ? 'expanded' : ''}`}>
                ▼
              </div>
            </div>

            {filmeExpandido === filme.filmeId && (
              <div className="sessoes-grid">
                {filme.sessoes.map(sessao => {
                  const statusInfo = getStatusSessaoInfo(sessao.statusSessao);
                  const StatusIcon = statusInfo.icone;
                  const temProblema = sessao.statusSessao === 'PROBLEMA_TECNICO';

                  return (
                    <div key={sessao.sessaoId} className={`sessao-card ${temProblema ? 'problema' : ''}`}>
                      <div className="sessao-header">
                        <div className={`sessao-status status-${statusInfo.cor}`}>
                          <StatusIcon size={20} />
                          <span>{statusInfo.label}</span>
                        </div>
                        {temProblema && (
                          <div className="badge-problema">
                            <AlertTriangle size={14} />
                            Requer Atenção
                          </div>
                        )}
                      </div>

                      <div className="sessao-detalhes">
                        <div className="detalhe-item">
                          <MapPin size={16} />
                          <span>{sessao.sala}</span>
                        </div>
                        <div className="detalhe-item">
                          <Clock size={16} />
                          <span>{formatarDataHora(sessao.horario)}</span>
                        </div>
                        <div className="detalhe-item">
                          <Users size={16} />
                          <span>{sessao.totalIngressos} ingresso(s)</span>
                        </div>
                      </div>

                      <div className="ingressos-lista">
                        {sessao.ingressos.map(ingresso => (
                          <div key={ingresso.id} className="ingresso-item">
                            <div className="ingresso-info">
                              <div className="ingresso-qr">{ingresso.qrCode}</div>
                              <div className="ingresso-detalhes">
                                <span className="assento">Assento: {ingresso.assento}</span>
                                <span className="tipo">{ingresso.tipo}</span>
                              </div>
                            </div>
                            <button
                              className="btn-remarcar-ingresso"
                              onClick={() => abrirModalRemarcacao(ingresso, sessao)}
                            >
                              <RefreshCw size={14} />
                              Remarcar
                            </button>
                          </div>
                        ))}
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        ))}
      </div>

      {/* Modal de Remarcação */}
      <Modal
        isOpen={modalRemarcar}
        onClose={fecharModal}
        title="Remarcar Ingresso"
        size="lg"
      >
        {ingressoSelecionado && sessaoSelecionada && (
          <div className="modal-remarcar-content">
            <div className="sessao-atual-info">
              <h4>Ingresso Atual</h4>
              <div className="info-grid">
                <div><strong>QR Code:</strong> {ingressoSelecionado.qrCode}</div>
                <div><strong>Assento:</strong> {ingressoSelecionado.assento}</div>
                <div><strong>Filme:</strong> {filmes.find(f => f.sessoes.some(s => s.sessaoId === sessaoSelecionada.sessaoId))?.filmeTitulo || 'N/A'}</div>
                <div><strong>Sessão Atual:</strong> {sessaoSelecionada.sala}</div>
                <div><strong>Horário:</strong> {formatarDataHora(sessaoSelecionada.horario)}</div>
              </div>
            </div>

            <div className="motivo-section">
              <label>Motivo Técnico *</label>
              <textarea
                value={motivoTecnico}
                onChange={(e) => setMotivoTecnico(e.target.value)}
                placeholder="Descreva o motivo da remarcação (ex: problema técnico na sala, falha no projetor, etc.)"
                rows={3}
                className="motivo-textarea"
              />
            </div>

            {!sessaoDestinoSelecionada ? (
              <div className="sessoes-destino-section">
                <h4>Selecione a Nova Sessão</h4>
                {sessoesDestino.length === 0 ? (
                  <p className="sem-sessoes">Não há outras sessões disponíveis para este filme</p>
                ) : (
                  <div className="sessoes-destino-lista">
                    {sessoesDestino.map(sessao => {
                      const statusInfo = getStatusSessaoInfo(sessao.statusSessao);
                      const StatusIcon = statusInfo.icone;

                      return (
                        <button
                          key={sessao.sessaoId}
                          className={`sessao-destino-btn ${sessao.statusSessao === 'DISPONIVEL' ? 'disponivel' : ''}`}
                          onClick={() => selecionarSessaoDestino(sessao)}
                          disabled={sessao.statusSessao !== 'DISPONIVEL'}
                        >
                          <div className="sessao-destino-info">
                            <div className={`status-indicator status-${statusInfo.cor}`}>
                              <StatusIcon size={18} />
                            </div>
                            <div className="sessao-destino-detalhes">
                              <strong>{sessao.filmeTitulo || `Filme #${sessao.filmeId}`}</strong>
                              <span>{sessao.sala} - {formatarDataHora(sessao.horario)}</span>
                            </div>
                          </div>
                          <div className="sessao-destino-meta">
                            <span className="ocupacao">{sessao.totalIngressos || 0} ingressos</span>
                            {sessao.statusSessao === 'DISPONIVEL' ? (
                              <span className="btn-label">Selecionar →</span>
                            ) : (
                              <span className="indisponivel-label">{statusInfo.label}</span>
                            )}
                          </div>
                        </button>
                      );
                    })}
                  </div>
                )}
              </div>
            ) : (
              <div className="assentos-section">
                <div className="sessao-selecionada-info">
                  <h4>Nova Sessão Selecionada</h4>
                  <div className="sessao-escolhida">
                    <MapPin size={16} />
                    <span>{sessaoDestinoSelecionada.sala}</span>
                    <Clock size={16} />
                    <span>{formatarDataHora(sessaoDestinoSelecionada.horario)}</span>
                    <button
                      className="btn-voltar-sessoes"
                      onClick={() => {
                        setSessaoDestinoSelecionada(null);
                        setAssentosDisponiveis([]);
                        setAssentoSelecionado(null);
                      }}
                    >
                      ← Trocar Sessão
                    </button>
                  </div>
                </div>

                <div className="assentos-selecao">
                  <h4>Selecione o Novo Assento *</h4>
                  {carregandoAssentos ? (
                    <div className="loading-assentos">
                      <div className="spinner-small"></div>
                      <p>Carregando assentos disponíveis...</p>
                    </div>
                  ) : assentosDisponiveis.length === 0 ? (
                    <p className="sem-assentos">Nenhum assento disponível nesta sessão</p>
                  ) : (
                    <>
                      <p className="assentos-hint">
                        {assentosDisponiveis.find(a => a.id === ingressoSelecionado.assento)
                          ? `✓ O assento original (${ingressoSelecionado.assento}) está disponível`
                          : `⚠ O assento original (${ingressoSelecionado.assento}) não está disponível. Selecione outro.`}
                      </p>
                      <div className="assentos-grid">
                        {assentosDisponiveis.map(assento => (
                          <button
                            key={assento.id}
                            className={`assento-btn ${assentoSelecionado === assento.id ? 'selecionado' : ''} ${assento.id === ingressoSelecionado.assento ? 'original' : ''}`}
                            onClick={() => setAssentoSelecionado(assento.id)}
                          >
                            {assento.id}
                            {assento.id === ingressoSelecionado.assento && (
                              <span className="badge-original">Original</span>
                            )}
                          </button>
                        ))}
                      </div>
                    </>
                  )}
                </div>

                <div className="modal-actions">
                  <button
                    className="btn-cancelar"
                    onClick={fecharModal}
                  >
                    Cancelar
                  </button>
                  <button
                    className="btn-confirmar"
                    onClick={confirmarRemarcacao}
                    disabled={!assentoSelecionado || carregandoAssentos}
                  >
                    <RefreshCw size={16} />
                    Confirmar Remarcação
                  </button>
                </div>
              </div>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
};

export default RemarcarNovo;

import React, { useCallback, useEffect, useMemo, useState } from 'react';
import './PagesStyles.css';
import { generateSeatMap } from '../../../utils/assentos';

const Assentos = ({ sessao, filme, onVoltar, onConfirmar }) => {
  const [assentos, setAssentos] = useState([]);
  const [assentosSelecionados, setAssentosSelecionados] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState('');
  const [detalhesSessao, setDetalhesSessao] = useState(sessao);

  const carregarAssentos = useCallback(async () => {
    setCarregando(true);
    setErro('');
    try {
      const res = await fetch(`/api/sessoes/${sessao.id}`);
      if (!res.ok) throw new Error('Falha ao buscar sessão');
      const data = await res.json();
      setDetalhesSessao(data);
      setAssentos(generateSeatMap({
        capacidade: data.capacidade,
        assentosDisponiveis: data.assentosDisponiveis,
        sessaoId: data.id,
      }));
    } catch (e) {
      console.error('Erro ao carregar sessão/assentos:', e);
      setErro('Não foi possível carregar os assentos em tempo real. Mostrando layout padrão.');
      setAssentos(generateSeatMap({
        capacidade: sessao.capacidade ?? 60,
        assentosDisponiveis: sessao.assentosDisponiveis ?? sessao.capacidade ?? 60,
        sessaoId: sessao.id,
      }));
    } finally {
      setCarregando(false);
    }
  }, [sessao.assentosDisponiveis, sessao.capacidade, sessao.id]);

  useEffect(() => {
    carregarAssentos();
  }, [carregarAssentos]);

  const toggleAssento = (assentoId) => {
    if (assentosSelecionados.includes(assentoId)) {
      setAssentosSelecionados(assentosSelecionados.filter(id => id !== assentoId));
    } else {
      setAssentosSelecionados([...assentosSelecionados, assentoId]);
    }
  };

  const formatarHorario = (horarioIso) => {
    const data = new Date(horarioIso);
    return data.toLocaleString('pt-BR', {
      weekday: 'long',
      day: '2-digit',
      month: 'long',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const calcularTotal = () => {
    return assentosSelecionados.length * 25.0; // Preço fixo por enquanto
  };

  const sessaoAtual = useMemo(() => detalhesSessao || sessao, [detalhesSessao, sessao]);

  return (
    <div className="page-container">
      <header className="page-header">
        <div>
          <h1>🎫 Escolha seus Assentos</h1>
          <div className="sessao-escolhida">
            <p><strong>{filme.titulo}</strong></p>
            <p>{formatarHorario(sessaoAtual.horario)} • {sessaoAtual.sala}</p>
          </div>
        </div>
        <button className="btn-voltar" onClick={onVoltar}>
          ← Voltar
        </button>
      </header>

      {erro && (
        <div className="alert-error">{erro}</div>
      )}

      {carregando ? (
        <div className="loading-card">Carregando assentos...</div>
      ) : (
        <>
          <div className="legenda-assentos">
            <div className="legenda-item">
              <span className="assento-disponivel-demo"></span>
              <span>Disponível</span>
            </div>
            <div className="legenda-item">
              <span className="assento-selecionado-demo"></span>
              <span>Selecionado</span>
            </div>
            <div className="legenda-item">
              <span className="assento-ocupado-demo"></span>
              <span>Ocupado</span>
            </div>
          </div>

          <div className="tela-cinema">
            <div className="tela-label">TELA</div>
          </div>

          <div className="assentos-grid">
            {assentos.map((assento) => (
              <button
                key={assento.id}
                className={`assento ${!assento.disponivel ? 'ocupado' : ''} ${assentosSelecionados.includes(assento.id) ? 'selecionado' : ''}`}
                onClick={() => assento.disponivel && toggleAssento(assento.id)}
                disabled={!assento.disponivel}
              >
                {assento.id}
              </button>
            ))}
          </div>

          <div className="resumo-selecao">
            <div className="resumo-info">
              <p><strong>Assentos selecionados:</strong> {assentosSelecionados.length > 0 ? assentosSelecionados.join(', ') : 'Nenhum'}</p>
              <p className="total-valor"><strong>Total:</strong> R$ {calcularTotal().toFixed(2)}</p>
            </div>
            <button 
              className="btn-continuar"
              onClick={() => onConfirmar(assentosSelecionados)}
              disabled={assentosSelecionados.length === 0}
            >
              Continuar para Bomboniere →
            </button>
          </div>
        </>
      )}
    </div>
  );
};

export default Assentos;

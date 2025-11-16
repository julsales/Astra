import React, { useMemo, useState } from 'react';
import './PagesStyles.css';

const formatarData = (iso) => {
  try {
    return new Date(iso).toLocaleString('pt-BR', {
      weekday: 'short',
      day: '2-digit',
      month: 'short',
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch (error) {
    return iso;
  }
};

const statusLabel = {
  CONFIRMADO: { text: 'Confirmado', className: 'status-confirmado' },
  UTILIZADO: { text: 'Utilizado', className: 'status-utilizado' },
  CANCELADO: { text: 'Cancelado', className: 'status-cancelado' },
};

const gerarStatus = (status) => statusLabel[status] ?? statusLabel.CONFIRMADO;

const QrModal = ({ ingresso, onClose }) => {
  if (!ingresso) return null;
  return (
    <div className="qr-modal-overlay" onClick={onClose}>
      <div className="qr-modal" onClick={(e) => e.stopPropagation()}>
        <button className="qr-close" onClick={onClose}>×</button>
        <p className="qr-title">QR Code do ingresso {ingresso.codigo}</p>
        {ingresso.qrCode ? (
          <img src={ingresso.qrCode} alt={`QR ${ingresso.codigo}`} />
        ) : (
          <span className="qr-placeholder">QR indisponível</span>
        )}
        <p className="qr-sessao">{ingresso.filme?.titulo} — {formatarData(ingresso.sessao?.horario)}</p>
      </div>
    </div>
  );
};

const MeusIngressos = ({ ingressos = [], onVoltar }) => {
  const [ingressoSelecionado, setIngressoSelecionado] = useState(null);

  const agrupados = useMemo(() => {
    return ingressos.reduce((acc, ingresso) => {
      const dia = new Date(ingresso.sessao?.horario ?? ingresso.dataCompra).toLocaleDateString('pt-BR');
      acc[dia] = acc[dia] ?? [];
      acc[dia].push(ingresso);
      return acc;
    }, {});
  }, [ingressos]);

  const diasOrdenados = Object.keys(agrupados).sort((a, b) => {
    const [diaA, mesA, anoA] = a.split('/').map(Number);
    const [diaB, mesB, anoB] = b.split('/').map(Number);
    return new Date(anoB, mesB - 1, diaB) - new Date(anoA, mesA - 1, diaA);
  });

  return (
    <div className="page-container">
      <header className="page-header">
        <div>
          <h1>🎟️ Meus Ingressos</h1>
          <p className="page-subtitle">Consulte seus ingressos e QR Codes</p>
        </div>
        <button className="btn-voltar" onClick={onVoltar}>← Voltar</button>
      </header>

      {ingressos.length === 0 ? (
        <div className="empty-card">
          <p>Você ainda não possui compras registradas.</p>
          <small>Finalize uma compra para que seus ingressos apareçam aqui.</small>
        </div>
      ) : (
        diasOrdenados.map((dia) => (
          <section key={dia} className="ingressos-dia">
            <h3>{dia}</h3>
            <div className="ingressos-grid">
              {agrupados[dia].map((ingresso) => {
                const assentos = Array.isArray(ingresso.assentos) ? ingresso.assentos : [];
                const status = gerarStatus(ingresso.status);
                const valorTotal = typeof ingresso.total === 'number' ? ingresso.total : 0;
                return (
                  <article key={ingresso.id} className="ingresso-card">
                    <div className="ingresso-card-header">
                      <div>
                        <h4>{ingresso.filme?.titulo}</h4>
                        <p>{formatarData(ingresso.sessao?.horario)}</p>
                      </div>
                      <span className={`status-badge ${status.className}`}>{status.text}</span>
                    </div>
                    <p className="ingresso-assentos">Assentos: {assentos.length > 0 ? assentos.join(', ') : 'Não informado'}</p>
                    <p className="ingresso-total">Total Pago: R$ {valorTotal.toFixed(2)}</p>
                    <div className="ingresso-acoes">
                      <button className="btn-secundario" onClick={() => setIngressoSelecionado(ingresso)}>
                        Mostrar QR Code
                      </button>
                    </div>
                  </article>
                );
              })}
            </div>
          </section>
        ))
      )}

      <QrModal ingresso={ingressoSelecionado} onClose={() => setIngressoSelecionado(null)} />
    </div>
  );
};

export default MeusIngressos;

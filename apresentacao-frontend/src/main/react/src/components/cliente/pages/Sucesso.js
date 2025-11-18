import React, { useMemo } from 'react';
import { Film, Ticket, Popcorn, DollarSign, Smartphone, Home } from 'lucide-react';
import './PagesStyles.css';

const Sucesso = ({ compra, fallbackCarrinho, onVoltarInicio, onAbrirIngressos }) => {
  const dados = useMemo(() => {
    if (compra) return compra;
    const subtotalProdutos = fallbackCarrinho.produtos.reduce((total, item) =>
      total + (item.preco * item.quantidade), 0
    );
    return {
      filme: fallbackCarrinho.filme,
      sessao: fallbackCarrinho.sessao,
      assentos: fallbackCarrinho.ingressos,
      produtos: fallbackCarrinho.produtos,
      total: fallbackCarrinho.totalIngressos + subtotalProdutos,
      codigo: Math.random().toString(36).substring(2, 10).toUpperCase(),
      qrCode: '',
    };
  }, [compra, fallbackCarrinho]);

  const totalPago = compra?.total ?? dados.total;
  const codigoCompra = compra?.codigo ?? dados.codigo;

  return (
    <div className="page-container sucesso-page">
      <div className="sucesso-card">
        <div className="sucesso-icon">✓</div>
        <h1>Compra Realizada com Sucesso!</h1>
        <p className="sucesso-subtitle">Seu pedido foi confirmado</p>

        <div className="codigo-compra">
          <p>Código do Pedido</p>
          <h2>{codigoCompra}</h2>
        </div>

        {dados.qrCode && (
          <div className="qr-preview">
            <img src={dados.qrCode} alt="QR Code do ingresso" />
            <small>Apresente este QR code na entrada</small>
          </div>
        )}

        <div className="sucesso-detalhes">
          <div className="detalhe-item">
            <span className="detalhe-icon"><Film size={24} /></span>
            <div>
              <strong>{dados.filme?.titulo}</strong>
              <p>{new Date(dados.sessao?.horario).toLocaleString('pt-BR')}</p>
              <p>{dados.sessao?.sala}</p>
            </div>
          </div>

          <div className="detalhe-item">
            <span className="detalhe-icon"><Ticket size={24} /></span>
            <div>
              <strong>Ingressos</strong>
              <p>Assentos: {dados.assentos?.join(', ')}</p>
            </div>
          </div>

          {dados.produtos?.length > 0 && (
            <div className="detalhe-item">
              <span className="detalhe-icon"><Popcorn size={24} /></span>
              <div>
                <strong>Bomboniere</strong>
                {dados.produtos.map((item, idx) => (
                  <p key={idx}>{item.quantidade}x {item.nome}</p>
                ))}
              </div>
            </div>
          )}

          <div className="detalhe-item total-item">
            <span className="detalhe-icon"><DollarSign size={24} /></span>
            <div>
              <strong>Total Pago</strong>
              <p className="valor-total">R$ {totalPago.toFixed(2)}</p>
            </div>
          </div>
        </div>

        <div className="sucesso-instrucoes">
          <h3><Smartphone size={20} style={{ marginRight: '8px', verticalAlign: 'middle' }} />Próximos Passos</h3>
          <ul>
            <li>Apresente este código na bilheteria ou balcão de retirada</li>
            <li>Chegue 15 minutos antes do horário da sessão</li>
            <li>Seus ingressos e produtos estarão reservados</li>
          </ul>
        </div>

        <div className="sucesso-acoes">
          <button className="btn-secundario" onClick={onAbrirIngressos}>
            <Ticket size={18} style={{ marginRight: '6px' }} />Ver Meus Ingressos
          </button>
          <button className="btn-voltar-inicio" onClick={onVoltarInicio}>
            <Home size={18} style={{ marginRight: '6px' }} />Voltar ao Início
          </button>
        </div>
      </div>
    </div>
  );
};

export default Sucesso;

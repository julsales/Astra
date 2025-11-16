import React from 'react';
import './PagesStyles.css';
import { gerarQrCodeDataUrl } from '../../../utils/qr';

const Checkout = ({ carrinho, onVoltar, onConfirmarPagamento }) => {
  const [metodoPagamento, setMetodoPagamento] = React.useState('');
  const [processando, setProcessando] = React.useState(false);

  const calcularSubtotalProdutos = () => {
    return carrinho.produtos.reduce((total, item) => total + (item.preco * item.quantidade), 0);
  };

  const calcularTotal = () => {
    return carrinho.totalIngressos + calcularSubtotalProdutos();
  };

  const handleConfirmar = async () => {
    if (!metodoPagamento) {
      alert('Por favor, selecione um método de pagamento');
      return;
    }

    setProcessando(true);
    try {
      const payloadQr = {
        usuario: carrinho.usuario?.nome,
        filme: carrinho.filme?.titulo,
        sessao: carrinho.sessao?.id,
        assentos: carrinho.ingressos,
        metodoPagamento,
        timestamp: Date.now(),
      };
      const qrCode = await gerarQrCodeDataUrl(JSON.stringify(payloadQr));

      onConfirmarPagamento({
        filme: carrinho.filme,
        sessao: carrinho.sessao,
        assentos: carrinho.ingressos,
        produtos: carrinho.produtos,
        total: calcularTotal(),
        metodoPagamento,
        usuario: carrinho.usuario,
        qrCode,
        status: 'CONFIRMADO',
      });
    } finally {
      setProcessando(false);
    }
  };

  return (
    <div className="page-container">
      <header className="page-header">
        <div>
          <h1>💳 Pagamento</h1>
          <p className="page-subtitle">Revise seu pedido e finalize a compra</p>
        </div>
        <button className="btn-voltar" onClick={onVoltar} disabled={processando}>
          ← Voltar
        </button>
      </header>

      <div className="checkout-container">
        <div className="checkout-resumo">
          <h2>📋 Resumo do Pedido</h2>
          
          <div className="resumo-sessao">
            <h3>🎬 {carrinho.filme.titulo}</h3>
            <p>{new Date(carrinho.sessao.horario).toLocaleString('pt-BR')} • {carrinho.sessao.sala}</p>
          </div>

          <div className="resumo-section">
            <p><strong>🎫 Ingressos ({carrinho.ingressos.length}):</strong></p>
            <p className="assentos-lista">Assentos: {carrinho.ingressos.join(', ')}</p>
            <p className="valor-linha">R$ {carrinho.totalIngressos.toFixed(2)}</p>
          </div>

          {carrinho.produtos.length > 0 && (
            <div className="resumo-section">
              <p><strong>🍿 Bomboniere:</strong></p>
              {carrinho.produtos.map((item, idx) => (
                <div key={idx} className="item-linha">
                  <span>{item.quantidade}x {item.nome}</span>
                  <span>R$ {(item.preco * item.quantidade).toFixed(2)}</span>
                </div>
              ))}
            </div>
          )}

          <div className="resumo-total-destaque">
            <span>TOTAL</span>
            <span>R$ {calcularTotal().toFixed(2)}</span>
          </div>
        </div>

        <div className="checkout-pagamento">
          <h2>💳 Método de Pagamento</h2>
          
          <div className="metodos-pagamento">
            <button
              className={`metodo-btn ${metodoPagamento === 'credito' ? 'selected' : ''}`}
              onClick={() => setMetodoPagamento('credito')}
              disabled={processando}
            >
              <span className="metodo-icon">💳</span>
              <div>
                <strong>Cartão de Crédito</strong>
                <small>Parcelamento disponível</small>
              </div>
            </button>

            <button
              className={`metodo-btn ${metodoPagamento === 'debito' ? 'selected' : ''}`}
              onClick={() => setMetodoPagamento('debito')}
              disabled={processando}
            >
              <span className="metodo-icon">💰</span>
              <div>
                <strong>Cartão de Débito</strong>
                <small>Aprovação instantânea</small>
              </div>
            </button>

            <button
              className={`metodo-btn ${metodoPagamento === 'pix' ? 'selected' : ''}`}
              onClick={() => setMetodoPagamento('pix')}
              disabled={processando}
            >
              <span className="metodo-icon">📱</span>
              <div>
                <strong>PIX</strong>
                <small>Pagamento instantâneo</small>
              </div>
            </button>
          </div>

          <button 
            className="btn-confirmar-pagamento"
            onClick={handleConfirmar}
            disabled={!metodoPagamento || processando}
          >
            {processando ? '⏳ Processando...' : '✓ Confirmar Pagamento'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default Checkout;

import React, { useEffect, useState } from 'react';
import './PagesStyles.css';

const Bomboniere = ({ carrinho, onAdicionarProduto, onRemoverProduto, onVoltar, onFinalizar }) => {
  const [produtos, setProdutos] = useState([]);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    carregarProdutos();
  }, []);

  const carregarProdutos = async () => {
    try {
      const res = await fetch('/api/produtos');
      const data = await res.json();
      setProdutos(data.filter(p => p.estoque > 0));
    } catch (e) {
      console.error('Erro ao carregar produtos:', e);
    } finally {
      setCarregando(false);
    }
  };

  const getQuantidadeNoCarrinho = (produtoId) => {
    const item = carrinho.produtos.find(p => p.id === produtoId);
    return item ? item.quantidade : 0;
  };

  const calcularSubtotalProdutos = () => {
    return carrinho.produtos.reduce((total, item) => total + (item.preco * item.quantidade), 0);
  };

  const calcularTotal = () => {
    return carrinho.totalIngressos + calcularSubtotalProdutos();
  };

  return (
    <div className="page-container">
      <header className="page-header">
        <div>
          <h1>🍿 Bomboniere</h1>
          <p className="page-subtitle">
            Aproveite para garantir seus lanches!
          </p>
        </div>
        <button className="btn-voltar" onClick={onVoltar}>
          ← Voltar
        </button>
      </header>

      {carregando ? (
        <div className="loading-card">Carregando produtos...</div>
      ) : (
        <>
          <div className="produtos-grid">
            {produtos.map((produto) => {
              const qtd = getQuantidadeNoCarrinho(produto.id);
              return (
                <div key={produto.id} className="produto-card">
                  <div className="produto-icon">
                    {produto.nome.toLowerCase().includes('pipoca') ? '🍿' :
                     produto.nome.toLowerCase().includes('refri') || produto.nome.toLowerCase().includes('refrigerante') ? '🥤' :
                     produto.nome.toLowerCase().includes('água') ? '💧' :
                     produto.nome.toLowerCase().includes('chocolate') ? '🍫' :
                     produto.nome.toLowerCase().includes('nachos') ? '🌮' :
                     produto.nome.toLowerCase().includes('hot') ? '🌭' :
                     produto.nome.toLowerCase().includes('combo') ? '🎉' : '🍭'}
                  </div>
                  <h3>{produto.nome}</h3>
                  <p className="produto-preco">R$ {produto.preco.toFixed(2)}</p>
                  <p className="produto-estoque">{produto.estoque} disponíveis</p>
                  
                  {qtd > 0 ? (
                    <div className="quantidade-controle">
                      <button 
                        className="btn-qty"
                        onClick={() => onRemoverProduto(produto)}
                      >
                        −
                      </button>
                      <span className="qty-display">{qtd}</span>
                      <button 
                        className="btn-qty"
                        onClick={() => onAdicionarProduto(produto)}
                        disabled={qtd >= produto.estoque}
                      >
                        +
                      </button>
                    </div>
                  ) : (
                    <button 
                      className="btn-adicionar"
                      onClick={() => onAdicionarProduto(produto)}
                    >
                      Adicionar
                    </button>
                  )}
                </div>
              );
            })}
          </div>

          <div className="resumo-compra">
            <h3>📋 Resumo da Compra</h3>
            
            <div className="resumo-section">
              <p><strong>🎫 Ingressos:</strong></p>
              <p>{carrinho.ingressos.length} ingresso(s) - R$ {carrinho.totalIngressos.toFixed(2)}</p>
            </div>

            {carrinho.produtos.length > 0 && (
              <div className="resumo-section">
                <p><strong>🍿 Bomboniere:</strong></p>
                {carrinho.produtos.map((item, idx) => (
                  <p key={idx}>
                    {item.quantidade}x {item.nome} - R$ {(item.preco * item.quantidade).toFixed(2)}
                  </p>
                ))}
                <p><strong>Subtotal:</strong> R$ {calcularSubtotalProdutos().toFixed(2)}</p>
              </div>
            )}

            <div className="resumo-total">
              <p><strong>TOTAL:</strong> R$ {calcularTotal().toFixed(2)}</p>
            </div>

            <div className="resumo-acoes">
              <button className="btn-pular" onClick={() => onFinalizar()}>
                Pular Bomboniere
              </button>
              <button className="btn-finalizar" onClick={() => onFinalizar()}>
                Finalizar Compra →
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default Bomboniere;

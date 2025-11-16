import React, { useMemo, useState } from 'react';
import './ClientePainel.css';
import Filmes from './pages/Filmes';
import Assentos from './pages/Assentos';
import Bomboniere from './pages/Bomboniere';
import Checkout from './pages/Checkout';
import Sucesso from './pages/Sucesso';
import MeusIngressos from './pages/MeusIngressos';
import { useMeusIngressos } from '../../hooks/useMeusIngressos';

const ClientePainel = ({ usuario, onLogout }) => {
  const [etapa, setEtapa] = useState('filmes'); // filmes, assentos, bomboniere, checkout, sucesso, meusIngressos
  const [sessaoSelecionada, setSessaoSelecionada] = useState(null);
  const [filmeSelecionado, setFilmeSelecionado] = useState(null);
  const [carrinho, setCarrinho] = useState({
    ingressos: [],
    produtos: [],
    totalIngressos: 0,
    sessao: null,
    filme: null
  });
  const [ultimaCompra, setUltimaCompra] = useState(null);

  const { ingressos, registrarCompra } = useMeusIngressos(usuario);
  const resumoCarrinho = useMemo(() => ({
    ...carrinho,
    usuario,
  }), [carrinho, usuario]);

  // Handlers
  const handleSelecionarSessao = (sessao, filme) => {
    setSessaoSelecionada(sessao);
    setFilmeSelecionado(filme);
    setEtapa('assentos');
  };

  const handleConfirmarAssentos = (assentos) => {
    const totalIngressos = assentos.length * 25.0;
    setCarrinho({
      ...carrinho,
      ingressos: assentos,
      totalIngressos,
      sessao: sessaoSelecionada,
      filme: filmeSelecionado
    });
    setEtapa('bomboniere');
  };

  const handleAdicionarProduto = (produto) => {
    const produtosAtualizados = [...carrinho.produtos];
    const index = produtosAtualizados.findIndex(p => p.id === produto.id);
    
    if (index >= 0) {
      produtosAtualizados[index].quantidade += 1;
    } else {
      produtosAtualizados.push({ ...produto, quantidade: 1 });
    }
    
    setCarrinho({ ...carrinho, produtos: produtosAtualizados });
  };

  const handleRemoverProduto = (produto) => {
    const produtosAtualizados = [...carrinho.produtos];
    const index = produtosAtualizados.findIndex(p => p.id === produto.id);
    
    if (index >= 0) {
      if (produtosAtualizados[index].quantidade > 1) {
        produtosAtualizados[index].quantidade -= 1;
      } else {
        produtosAtualizados.splice(index, 1);
      }
    }
    
    setCarrinho({ ...carrinho, produtos: produtosAtualizados });
  };

  const handleIrParaCheckout = () => {
    setEtapa('checkout');
  };

  const handleConfirmarPagamento = (detalhesCompra) => {
    const registro = registrarCompra(detalhesCompra);
    setUltimaCompra(registro);
    setEtapa('sucesso');
  };

  const handleVoltarInicio = () => {
    setEtapa('filmes');
    setSessaoSelecionada(null);
    setFilmeSelecionado(null);
    setCarrinho({
      ingressos: [],
      produtos: [],
      totalIngressos: 0,
      sessao: null,
      filme: null
    });
    setUltimaCompra(null);
  };

  return (
    <div className="cliente-container">
      {/* Header Global */}
      {etapa !== 'sucesso' && (
        <header className="cliente-header-global">
          <div className="header-info">
            <p className="bem-vindo">Olá, {usuario.nome} 👋</p>
            <div className="progress-steps">
              <span className={etapa === 'filmes' ? 'active' : etapa !== 'filmes' ? 'completed' : ''}>
                🎬 Filmes
              </span>
              <span className="step-separator">→</span>
              <span className={etapa === 'assentos' ? 'active' : ['bomboniere', 'checkout'].includes(etapa) ? 'completed' : ''}>
                🎫 Assentos
              </span>
              <span className="step-separator">→</span>
              <span className={etapa === 'bomboniere' ? 'active' : etapa === 'checkout' ? 'completed' : ''}>
                🍿 Bomboniere
              </span>
              <span className="step-separator">→</span>
              <span className={etapa === 'checkout' ? 'active' : ''}>
                💳 Pagamento
              </span>
            </div>
          </div>
          <div className="header-actions">
            <button
              type="button"
              className="meus-ingressos-btn"
              onClick={() => setEtapa('meusIngressos')}
            >
              🎟️ Meus ingressos
            </button>
            <span className="badge-cliente">{usuario.tipo}</span>
            <button type="button" className="logout-btn" onClick={onLogout}>
              Sair
            </button>
          </div>
        </header>
      )}

      {/* Renderiza a etapa atual */}
      {etapa === 'filmes' && (
        <Filmes
          usuario={usuario}
          onSelecionarSessao={handleSelecionarSessao}
          onAbrirIngressos={() => setEtapa('meusIngressos')}
        />
      )}

      {etapa === 'assentos' && (
        <Assentos
          sessao={sessaoSelecionada}
          filme={filmeSelecionado}
          onVoltar={() => setEtapa('filmes')}
          onConfirmar={handleConfirmarAssentos}
        />
      )}

      {etapa === 'bomboniere' && (
        <Bomboniere
          carrinho={carrinho}
          onAdicionarProduto={handleAdicionarProduto}
          onRemoverProduto={handleRemoverProduto}
          onVoltar={() => setEtapa('assentos')}
          onFinalizar={handleIrParaCheckout}
        />
      )}

      {etapa === 'checkout' && (
        <Checkout
          carrinho={resumoCarrinho}
          onVoltar={() => setEtapa('bomboniere')}
          onConfirmarPagamento={handleConfirmarPagamento}
        />
      )}

      {etapa === 'sucesso' && (
        <Sucesso
          compra={ultimaCompra}
          fallbackCarrinho={resumoCarrinho}
          onVoltarInicio={handleVoltarInicio}
          onAbrirIngressos={() => setEtapa('meusIngressos')}
        />
      )}

      {etapa === 'meusIngressos' && (
        <MeusIngressos
          ingressos={ingressos}
          onVoltar={() => setEtapa('filmes')}
        />
      )}
    </div>
  );
};

export default ClientePainel;

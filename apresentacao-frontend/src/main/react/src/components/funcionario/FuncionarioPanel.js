import React, { useState, useEffect } from 'react';
import { CheckCircle, XCircle, Clock, AlertCircle, QrCode as QrIcon, History, RefreshCw, ShoppingCart } from 'lucide-react';
import '../Dashboard.css';
import './FuncionarioPanel.css';

const FuncionarioPanel = () => {
  const [abaAtiva, setAbaAtiva] = useState('validar');
  const [qrCode, setQrCode] = useState('');
  const [resultado, setResultado] = useState(null);
  const [carregando, setCarregando] = useState(false);

  // Estado para histórico
  const [historico, setHistorico] = useState([]);

  // Estado para remarcar
  const [ingressosAtivos, setIngressosAtivos] = useState([]);

  // Estado para bomboniere
  const [carrinhoBomb, setCarrinhoBomb] = useState([]);
  const [produtos, setProdutos] = useState([]);

  // Carregar dados ao montar o componente
  useEffect(() => {
    carregarHistorico();
    carregarIngressosAtivos();
    carregarProdutos();
  }, []);

  const carregarHistorico = async () => {
    try {
      const response = await fetch('/api/funcionario/ingressos/historico');
      if (response.ok) {
        const data = await response.json();
        setHistorico(data);
      }
    } catch (error) {
      console.error('Erro ao carregar histórico:', error);
    }
  };

  const carregarIngressosAtivos = async () => {
    try {
      const response = await fetch('/api/funcionario/ingressos/ativos');
      if (response.ok) {
        const data = await response.json();
        setIngressosAtivos(data);
      }
    } catch (error) {
      console.error('Erro ao carregar ingressos ativos:', error);
    }
  };

  const carregarProdutos = async () => {
    try {
      const response = await fetch('/api/funcionario/bomboniere/produtos');
      if (response.ok) {
        const data = await response.json();
        setProdutos(data);
      }
    } catch (error) {
      console.error('Erro ao carregar produtos:', error);
    }
  };

  const validarIngresso = async () => {
    if (!qrCode.trim()) return;

    setCarregando(true);
    
    try {
      const response = await fetch('/api/funcionario/ingressos/validar', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ qrCode: qrCode.trim() })
      });

      const data = await response.json();
      
      if (response.ok) {
        setResultado({
          valido: data.valido,
          mensagem: data.mensagem,
          filme: data.sessao ? `Sessão ${data.sessao.id}` : '',
          sessao: data.sessao ? `${data.sessao.horario} - Sala ${data.sessao.sala}` : '',
          assento: data.ingresso ? data.ingresso.assento : '',
          cliente: 'Cliente'
        });
        carregarHistorico();
        carregarIngressosAtivos();
      } else {
        setResultado({
          valido: false,
          mensagem: data.erro || 'Erro ao validar ingresso',
          filme: '',
          sessao: ''
        });
      }
    } catch (error) {
      console.error('Erro ao validar ingresso:', error);
      setResultado({
        valido: false,
        mensagem: 'Erro de conexão com o servidor',
        filme: '',
        sessao: ''
      });
    } finally {
      setCarregando(false);
    }
  };

  const ativarScanner = () => {
    alert('Scanner de QR Code ativado! (Funcionalidade de câmera seria implementada aqui)');
  };

  const renderValidarEntrada = () => (
    <div className="validar-container">
      <div className="validar-header">
        <QrIcon size={32} />
        <div>
          <h2>Validar Entrada de Cliente</h2>
          <p>Escaneie o QR Code ou digite o código do ingresso</p>
        </div>
      </div>

      <div className="validar-input-section">
        <div className="input-with-button">
          <input
            type="text"
            value={qrCode}
            onChange={(e) => setQrCode(e.target.value)}
            placeholder="Digite o código (ex: ASTRA001)"
            className="qr-input"
            onKeyPress={(e) => e.key === 'Enter' && validarIngresso()}
          />
          <button 
            className="btn-scanner"
            onClick={ativarScanner}
            title="Ativar Scanner de QR Code"
          >
            <QrIcon size={20} />
            Ativar Scanner
          </button>
        </div>

        <button 
          className="btn-validar"
          onClick={validarIngresso}
          disabled={!qrCode.trim() || carregando}
        >
          {carregando ? 'Validando...' : 'Validar'}
        </button>
      </div>

      <p className="help-text">
        💡 Clique em "Ativar Scanner" para validar ingressos usando a câmera do dispositivo.
      </p>

      {resultado && (
        <div className={`resultado-validacao ${resultado.valido ? 'valido' : 'invalido'}`}>
          <div className="resultado-icon">
            {resultado.valido ? <CheckCircle size={48} /> : <XCircle size={48} />}
          </div>
          <h3>{resultado.mensagem}</h3>
          <div className="resultado-detalhes">
            <p><strong>Filme:</strong> {resultado.filme}</p>
            <p><strong>Sessão:</strong> {resultado.sessao}</p>
            {resultado.assento && <p><strong>Assento:</strong> {resultado.assento}</p>}
            {resultado.cliente && <p><strong>Cliente:</strong> {resultado.cliente}</p>}
          </div>
        </div>
      )}

      <div className="codigos-teste">
        <h3>Códigos de teste disponíveis:</h3>
        <div className="codigos-lista">
          <span className="codigo-badge">ASTRA001</span>
          <span className="codigo-badge">ASTRA002</span>
        </div>
      </div>
    </div>
  );

  const renderRemarcarIngressos = () => (
    <div className="remarcar-container">
      <div className="remarcar-header">
        <RefreshCw size={32} />
        <div>
          <h2>Remarcar Ingressos</h2>
          <p>Mover cliente para outra sessão em caso de problemas técnicos</p>
        </div>
      </div>

      <div className="ingressos-list">
        {ingressosAtivos.length === 0 ? (
          <p style={{ textAlign: 'center', padding: '20px' }}>Nenhum ingresso ativo no momento</p>
        ) : (
          ingressosAtivos.map((ingresso) => (
            <div key={ingresso.id} className="ingresso-card">
              <div className="ingresso-info">
                <h4>QR Code: {ingresso.qrCode}</h4>
                <p><strong>Sessão:</strong> {ingresso.sessaoId}</p>
                <p><strong>Assento:</strong> {ingresso.assento}</p>
                <p><strong>Tipo:</strong> {ingresso.tipo}</p>
              </div>
              <button className="btn-remarcar">Selecionar</button>
            </div>
          ))
        )}
      </div>
    </div>
  );

  const renderHistorico = () => (
    <div className="historico-container">
      <div className="historico-header">
        <History size={32} />
        <div>
          <h2>Histórico de Validações</h2>
          <p>Todos os ingressos processados</p>
        </div>
      </div>

      <div className="historico-table">
        <table>
          <thead>
            <tr>
              <th>QR Code</th>
              <th>Sessão</th>
              <th>Assento</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {historico.length === 0 ? (
              <tr>
                <td colSpan="4" style={{ textAlign: 'center' }}>Nenhum ingresso processado ainda</td>
              </tr>
            ) : (
              historico.map((item) => (
                <tr key={item.id}>
                  <td>{item.qrCode}</td>
                  <td>Sessão {item.sessaoId}</td>
                  <td>{item.assento}</td>
                  <td>
                    <span className={`status-badge ${item.status.toLowerCase()}`}>
                      {item.status === 'VALIDO' && '⏱ Válido'}
                      {item.status === 'UTILIZADO' && '✅ Utilizado'}
                      {item.status === 'CANCELADO' && '❌ Cancelado'}
                    </span>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );

  const adicionarAoCarrinho = (produto) => {
    const itemExistente = carrinhoBomb.find(item => item.produtoId === produto.id);
    
    if (itemExistente) {
      setCarrinhoBomb(carrinhoBomb.map(item =>
        item.produtoId === produto.id
          ? { ...item, quantidade: item.quantidade + 1 }
          : item
      ));
    } else {
      setCarrinhoBomb([...carrinhoBomb, {
        produtoId: produto.id,
        nome: produto.nome,
        preco: produto.preco,
        quantidade: 1
      }]);
    }
  };

  const removerDoCarrinho = (produtoId) => {
    setCarrinhoBomb(carrinhoBomb.filter(item => item.produtoId !== produtoId));
  };

  const alterarQuantidade = (produtoId, novaQuantidade) => {
    if (novaQuantidade <= 0) {
      removerDoCarrinho(produtoId);
      return;
    }
    
    setCarrinhoBomb(carrinhoBomb.map(item =>
      item.produtoId === produtoId
        ? { ...item, quantidade: novaQuantidade }
        : item
    ));
  };

  const calcularTotal = () => {
    return carrinhoBomb.reduce((total, item) => total + (item.preco * item.quantidade), 0);
  };

  const finalizarVenda = async () => {
    if (carrinhoBomb.length === 0) return;

    try {
      const response = await fetch('/api/funcionario/bomboniere/venda', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          itens: carrinhoBomb.map(item => ({
            produtoId: item.produtoId,
            quantidade: item.quantidade
          }))
        })
      });

      const data = await response.json();

      if (response.ok) {
        alert(`Venda finalizada com sucesso! Total: R$ ${data.valorTotal.toFixed(2)}`);
        setCarrinhoBomb([]);
      } else {
        alert(`Erro: ${data.erro}`);
      }
    } catch (error) {
      console.error('Erro ao finalizar venda:', error);
      alert('Erro de conexão com o servidor');
    }
  };

  const renderBomboniere = () => (
    <div className="bomboniere-container">
      <div className="bomboniere-header">
        <ShoppingCart size={32} />
        <div>
          <h2>Operar Bomboniere</h2>
          <p>Registrar vendas de produtos</p>
        </div>
      </div>

      <div className="bomboniere-content">
        <div className="produtos-grid">
          {produtos.map((produto) => (
            <div key={produto.id} className="produto-card">
              <h4>{produto.nome}</h4>
              <p className="produto-preco">R$ {produto.preco.toFixed(2)}</p>
              <button 
                className="btn-adicionar"
                onClick={() => adicionarAoCarrinho(produto)}
              >
                Adicionar
              </button>
            </div>
          ))}
        </div>

        <div className="carrinho-bomboniere">
          <h3>Carrinho</h3>
          {carrinhoBomb.length === 0 ? (
            <p className="carrinho-vazio">Nenhum item adicionado</p>
          ) : (
            <div className="carrinho-itens">
              {carrinhoBomb.map((item) => (
                <div key={item.produtoId} className="carrinho-item">
                  <div className="item-info">
                    <strong>{item.nome}</strong>
                    <span>R$ {item.preco.toFixed(2)}</span>
                  </div>
                  <div className="item-quantidade">
                    <button onClick={() => alterarQuantidade(item.produtoId, item.quantidade - 1)}>-</button>
                    <span>{item.quantidade}</span>
                    <button onClick={() => alterarQuantidade(item.produtoId, item.quantidade + 1)}>+</button>
                  </div>
                  <div className="item-subtotal">
                    R$ {(item.preco * item.quantidade).toFixed(2)}
                  </div>
                  <button 
                    className="btn-remover"
                    onClick={() => removerDoCarrinho(item.produtoId)}
                  >
                    ✕
                  </button>
                </div>
              ))}
            </div>
          )}
          <div className="carrinho-total">
            <strong>Total:</strong>
            <span>R$ {calcularTotal().toFixed(2)}</span>
          </div>
          <button 
            className="btn-finalizar" 
            disabled={carrinhoBomb.length === 0}
            onClick={finalizarVenda}
          >
            Finalizar Venda
          </button>
        </div>
      </div>
    </div>
  );

  return (
    <div className="funcionario-panel">
      {/* Header com Logo e Info do Usuário */}
      <header className="funcionario-header">
        <div className="header-left">
          <div className="logo-funcionario">
            <img src="/logo.png" alt="Astra Cinemas" className="logo-header-image" />
          </div>
          <div className="funcionario-badge">Funcionário</div>
        </div>

        <div className="header-right">
          <div className="user-info">
            <span>Conta Demo</span>
          </div>
          <button className="logout-btn" onClick={() => window.location.reload()}>
            Sair
          </button>
        </div>
      </header>

      <div className="panel-header">
        <h1>Painel do Funcionário</h1>
        <p>Validar ingressos e gerenciar sessões</p>
      </div>

      <div className="panel-tabs">
        <button 
          className={`tab ${abaAtiva === 'validar' ? 'active' : ''}`}
          onClick={() => setAbaAtiva('validar')}
        >
          <QrIcon size={20} />
          Validar Entrada
        </button>
        <button 
          className={`tab ${abaAtiva === 'remarcar' ? 'active' : ''}`}
          onClick={() => setAbaAtiva('remarcar')}
        >
          <RefreshCw size={20} />
          Remarcar Ingressos
        </button>
        <button 
          className={`tab ${abaAtiva === 'historico' ? 'active' : ''}`}
          onClick={() => setAbaAtiva('historico')}
        >
          <History size={20} />
          Histórico
        </button>
        <button 
          className={`tab ${abaAtiva === 'bomboniere' ? 'active' : ''}`}
          onClick={() => setAbaAtiva('bomboniere')}
        >
          <ShoppingCart size={20} />
          Operar Bomboniere
        </button>
      </div>

      <div className="panel-content">
        {abaAtiva === 'validar' && renderValidarEntrada()}
        {abaAtiva === 'remarcar' && renderRemarcarIngressos()}
        {abaAtiva === 'historico' && renderHistorico()}
        {abaAtiva === 'bomboniere' && renderBomboniere()}
      </div>
    </div>
  );
};

export default FuncionarioPanel;

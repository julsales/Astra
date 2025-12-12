import React, { useState, useEffect } from 'react';
import {
  CheckCircle,
  XCircle,
  QrCode as QrIcon,
  History,
  RefreshCw,
  ShoppingCart,
  Home,
  Clock,
  Users,
  TrendingUp,
  Film,
  AlertTriangle,
  Check,
  X,
  Calendar,
  Package,
  LogOut,
  Scan,
  Plus,
  Minus,
  Search,
  BarChart3,
  PieChart,
  Activity,
  DollarSign,
  Award
} from 'lucide-react';
import Modal from '../shared/Modal';
import StatusBadge from '../shared/StatusBadge';
import { formatarDataHora, formatarMoeda } from '../../utils/formatters';
import './FuncionarioNovo.css';
import RemarcarNovo from './RemarcarNovo';

const FuncionarioPanel = ({ onLogout }) => {
  // Estados principais
  const [telaAtiva, setTelaAtiva] = useState('home');
  const [carregando, setCarregando] = useState(false);

  // Estados para Dashboard/Home
  const [proximaSessao, setProximaSessao] = useState(null);
  const [estatisticasHoje, setEstatisticasHoje] = useState({
    validacoes: 0,
    totalValidacoes: 0,
    taxaSucesso: 0,
    ingressosPendentes: 0,
    vendas: 0,
    totalVendas: 0
  });

  // Estados para Validação
  const [qrCode, setQrCode] = useState('');
  const [resultadoValidacao, setResultadoValidacao] = useState(null);

  // Estados para Histórico
  const [historico, setHistorico] = useState([]);
  const [filtroHistorico, setFiltroHistorico] = useState('todos');

  // Estados para Remarcação (agora carregamos TODOS os ingressos para remarcação)
  const [ingressos, setIngressos] = useState([]);
  const [ingressoSelecionado, setIngressoSelecionado] = useState(null);
  const [sessoesDisponiveis, setSessoesDisponiveis] = useState([]);
  const [modalRemarcar, setModalRemarcar] = useState(false);
  const [motivoTecnico, setMotivoTecnico] = useState('');

  // Estados para Bomboniere
  const [produtos, setProdutos] = useState([]);
  const [carrinho, setCarrinho] = useState([]);
  const [filtroCategoria, setFiltroCategoria] = useState('todos');

  // Estados para Relatórios
  const [relatorios, setRelatorios] = useState({
    remarcacoes: [],
    estatisticasGerais: {
      totalRemarcacoes: 0,
      remarcacoesHoje: 0,
      remarcacoesSemana: 0,
      motivosMaisComuns: []
    },
    vendasPorPeriodo: [],
    filmesPopulares: [],
    ocupacaoSalas: []
  });

  // Carregar dados iniciais
  useEffect(() => {
    carregarDashboard();
    carregarHistorico();
    carregarIngressos();
    carregarProdutos();
    carregarRelatorios();
  }, []);

  // Carregar relatórios quando a aba é acessada
  useEffect(() => {
    if (telaAtiva === 'relatorios') {
      carregarRelatorios();
    }
  }, [telaAtiva]);

  // ==========================================
  // DASHBOARD - Próxima Sessão e Estatísticas
  // ==========================================
  const carregarDashboard = async () => {
    try {
      // Buscar próxima sessão
      const resSessoes = await fetch('/api/sessoes');
      if (resSessoes.ok) {
        const sessoes = await resSessoes.json();
        const agora = new Date();
        const proxima = sessoes
          .filter(s => new Date(s.horario) > agora)
          .sort((a, b) => new Date(a.horario) - new Date(b.horario))[0];

        if (proxima) {
          const resFilme = await fetch(`/api/filmes/${proxima.filmeId}`);
          const filme = await resFilme.json();
          setProximaSessao({ ...proxima, filmeTitulo: filme.titulo });
        }
      }

      // Buscar estatísticas reais do backend
      const resEstatisticas = await fetch('/api/funcionario/estatisticas');
      if (resEstatisticas.ok) {
        const stats = await resEstatisticas.json();
        setEstatisticasHoje({
          validacoes: stats.validacoesHoje || 0,
          totalValidacoes: stats.totalValidacoes || 0,
          taxaSucesso: stats.taxaSucesso || 0,
          ingressosPendentes: stats.ingressosPendentes || 0,
          vendas: stats.vendasHoje || 0,
          totalVendas: stats.totalVendas || 0
        });
      }
    } catch (error) {
      console.error('Erro ao carregar dashboard:', error);
    }
  };

  // ==========================================
  // VALIDAÇÃO DE INGRESSOS
  // ==========================================
  const validarIngresso = async () => {
    if (!qrCode.trim()) return;

    setCarregando(true);
    setResultadoValidacao(null);

    try {
      const response = await fetch('/api/funcionario/ingressos/validar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ qrCode: qrCode.trim() })
      });

      const data = await response.json();

      if (response.ok) {
        setResultadoValidacao({
          valido: data.valido,
          mensagem: data.mensagem,
          ingresso: data.ingresso,
          sessao: data.sessao
        });

        // Recarregar histórico
        carregarHistorico();

        // Limpar QR code após 3 segundos se válido
        if (data.valido) {
          setTimeout(() => {
            setQrCode('');
            setResultadoValidacao(null);
          }, 3000);
        }
      } else {
        setResultadoValidacao({
          valido: false,
          mensagem: data.erro || 'Erro ao validar ingresso',
          ingresso: null,
          sessao: null
        });
      }
    } catch (error) {
      console.error('Erro ao validar ingresso:', error);
      setResultadoValidacao({
        valido: false,
        mensagem: 'Erro de conexão com o servidor',
        ingresso: null,
        sessao: null
      });
    } finally {
      setCarregando(false);
    }
  };

  const ativarScanner = () => {
    alert('Scanner de QR Code ativado!\n\n(Funcionalidade de câmera seria implementada aqui com uma biblioteca como react-qr-scanner)');
  };

  // ==========================================
  // HISTÓRICO
  // ==========================================
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

  const agruparHistoricoPorCompra = () => {
    // Agrupar validações por compraId
    const grupos = {};
    historico.forEach(item => {
      const compraId = item.compraId || item.id; // fallback para item.id se compraId não existir
      if (!grupos[compraId]) {
        grupos[compraId] = {
          ...item,
          assentos: [item.assento]
        };
      } else {
        // Adicionar assento ao grupo se ainda não estiver lá
        if (!grupos[compraId].assentos.includes(item.assento)) {
          grupos[compraId].assentos.push(item.assento);
        }
        // Manter a data/hora mais recente
        if (new Date(item.dataHora) > new Date(grupos[compraId].dataHora)) {
          grupos[compraId].dataHora = item.dataHora;
        }
      }
    });

    return Object.values(grupos);
  };

  const filtrarHistorico = () => {
    const agrupado = agruparHistoricoPorCompra();
    if (filtroHistorico === 'todos') return agrupado;
    return agrupado.filter(item => {
      if (filtroHistorico === 'sucesso') return item.sucesso;
      if (filtroHistorico === 'falha') return !item.sucesso;
      return true;
    });
  };

  // ==========================================
  // REMARCAÇÃO
  // ==========================================
  // Carrega TODOS os ingressos (não apenas os validados) para permitir remarcação por titular
  const carregarIngressos = async () => {
    try {
      // Assumimos que há um endpoint público para listar ingressos: /api/ingressos
      // Se o seu backend usar outro path, ajuste aqui.
      const response = await fetch('/api/ingressos');
      if (response.ok) {
        const data = await response.json();
        setIngressos(data);
      }
    } catch (error) {
      console.error('Erro ao carregar ingressos:', error);
    }
  };

  const abrirModalRemarcar = async (ingresso) => {
    setIngressoSelecionado(ingresso);

    // Buscar sessões disponíveis
    try {
      const response = await fetch('/api/sessoes');
      if (response.ok) {
        const sessoes = await response.json();
        // Filtrar sessões futuras, excluindo a sessão atual do ingresso
        const agora = new Date();
        const disponiveis = sessoes.filter(s =>
          new Date(s.horario) > agora && s.id !== ingresso.sessaoId
        );
        setSessoesDisponiveis(disponiveis);
      }
    } catch (error) {
      console.error('Erro ao carregar sessões:', error);
    }

    setModalRemarcar(true);
  };

  const remarcarIngresso = async (novaSessaoId) => {
    if (!motivoTecnico.trim()) {
      alert('Por favor, informe o motivo técnico da remarcação');
      return;
    }

    try {
      const response = await fetch('/api/funcionario/ingressos/remarcar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ingressoId: ingressoSelecionado.id,
          novaSessaoId: novaSessaoId,
          novoAssentoId: ingressoSelecionado.assento, // Mantém o mesmo assento
          motivoTecnico: motivoTecnico
        })
      });

      const data = await response.json();

        if (response.ok) {
        alert('Ingresso remarcado com sucesso!');
        setModalRemarcar(false);
        setMotivoTecnico('');
        setIngressoSelecionado(null);
        carregarIngressos();
      } else {
        alert(`Erro: ${data.erro}`);
      }
    } catch (error) {
      console.error('Erro ao remarcar ingresso:', error);
      alert('Erro de conexão com o servidor');
    }
  };

  // ==========================================
  // BOMBONIERE (PDV)
  // ==========================================
  const carregarProdutos = async () => {
    try {
      const response = await fetch('/api/produtos');
      if (response.ok) {
        const data = await response.json();
        setProdutos(data);
      }
    } catch (error) {
      console.error('Erro ao carregar produtos:', error);
    }
  };

  const adicionarAoCarrinho = (produto) => {
    const itemExistente = carrinho.find(item => item.produtoId === produto.id);

    if (itemExistente) {
      setCarrinho(carrinho.map(item =>
        item.produtoId === produto.id
          ? { ...item, quantidade: item.quantidade + 1 }
          : item
      ));
    } else {
      setCarrinho([...carrinho, {
        produtoId: produto.id,
        nome: produto.nome,
        preco: produto.preco,
        quantidade: 1
      }]);
    }
  };

  const removerDoCarrinho = (produtoId) => {
    setCarrinho(carrinho.filter(item => item.produtoId !== produtoId));
  };

  const alterarQuantidade = (produtoId, novaQuantidade) => {
    if (novaQuantidade <= 0) {
      removerDoCarrinho(produtoId);
      return;
    }

    setCarrinho(carrinho.map(item =>
      item.produtoId === produtoId
        ? { ...item, quantidade: novaQuantidade }
        : item
    ));
  };

  const calcularTotal = () => {
    return carrinho.reduce((total, item) => total + (item.preco * item.quantidade), 0);
  };

  const finalizarVenda = async () => {
    if (carrinho.length === 0 || carregando) return;

    setCarregando(true);
    try {
      const response = await fetch('/api/funcionario/bomboniere/venda', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          itens: carrinho.map(item => ({
            produtoId: item.produtoId,
            quantidade: item.quantidade
          }))
        })
      });

      const data = await response.json();

      if (response.ok) {
        alert(`Venda finalizada com sucesso!\nTotal: ${formatarMoeda(data.valorTotal)}`);
        setCarrinho([]);
        carregarProdutos(); // Atualizar estoque dos produtos
        carregarDashboard(); // Atualizar estatísticas
      } else {
        alert(`Erro: ${data.erro}`);
      }
    } catch (error) {
      console.error('Erro ao finalizar venda:', error);
      alert('Erro de conexão com o servidor');
    } finally {
      setCarregando(false);
    }
  };

  const categoriasProdutos = () => {
    const cats = new Set(produtos.map(p => p.categoria).filter(Boolean));
    return ['todos', ...Array.from(cats)];
  };

  const produtosFiltrados = () => {
    if (filtroCategoria === 'todos') return produtos;
    return produtos.filter(p => p.categoria === filtroCategoria);
  };

  // ==========================================
  // RELATÓRIOS
  // ==========================================
  const carregarRelatorios = async () => {
    try {
      // Carregar remarcações recentes
      const resRemarcacoes = await fetch('/api/funcionario/relatorios/remarcacoes');
      if (resRemarcacoes.ok) {
        const remarcacoesData = await resRemarcacoes.json();
        
        // Calcular estatísticas
        const hoje = new Date();
        hoje.setHours(0, 0, 0, 0);
        const inicioSemana = new Date(hoje);
        inicioSemana.setDate(hoje.getDate() - 7);

        const remarcacoesHoje = remarcacoesData.filter(r => 
          new Date(r.dataRemarcacao) >= hoje
        ).length;

        const remarcacoesSemana = remarcacoesData.filter(r => 
          new Date(r.dataRemarcacao) >= inicioSemana
        ).length;

        // Contar motivos mais comuns
        const motivosCount = {};
        remarcacoesData.forEach(r => {
          if (r.motivoTecnico) {
            motivosCount[r.motivoTecnico] = (motivosCount[r.motivoTecnico] || 0) + 1;
          }
        });

        const motivosMaisComuns = Object.entries(motivosCount)
          .map(([motivo, count]) => ({ motivo, count }))
          .sort((a, b) => b.count - a.count)
          .slice(0, 5);

        setRelatorios(prev => ({
          ...prev,
          remarcacoes: remarcacoesData,
          estatisticasGerais: {
            totalRemarcacoes: remarcacoesData.length,
            remarcacoesHoje,
            remarcacoesSemana,
            motivosMaisComuns
          }
        }));
      }

      // Carregar estatísticas de vendas
      const resVendas = await fetch('/api/funcionario/relatorios/vendas');
      if (resVendas.ok) {
        const vendasData = await resVendas.json();
        setRelatorios(prev => ({
          ...prev,
          vendasPorPeriodo: vendasData
        }));
      }

      // Carregar filmes populares
      const resFilmes = await fetch('/api/funcionario/relatorios/filmes-populares');
      if (resFilmes.ok) {
        const filmesData = await resFilmes.json();
        setRelatorios(prev => ({
          ...prev,
          filmesPopulares: filmesData
        }));
      }

      // Carregar ocupação de salas
      const resSalas = await fetch('/api/funcionario/relatorios/ocupacao-salas');
      if (resSalas.ok) {
        const salasData = await resSalas.json();
        setRelatorios(prev => ({
          ...prev,
          ocupacaoSalas: salasData
        }));
      }
    } catch (error) {
      console.error('Erro ao carregar relatórios:', error);
    }
  };

  // ==========================================
  // RENDERIZAÇÃO DAS TELAS
  // ==========================================

  const renderHome = () => (
    <div className="func-home-container">
      {/* Hero Section */}
      <div className="func-hero">
        <div className="func-hero-content">
          <h1 className="func-hero-title">Painel do Funcionário</h1>
          <p className="func-hero-subtitle">Gerencie operações do cinema em tempo real</p>
        </div>
      </div>

      {/* Estatísticas */}
      <div className="func-stats-grid">
        <div className="func-stat-card purple">
          <div className="func-stat-icon">
            <CheckCircle size={32} />
          </div>
          <div className="func-stat-info">
            <h3>{estatisticasHoje.validacoes}</h3>
            <p>Validações Hoje</p>
          </div>
        </div>

        <div className="func-stat-card blue">
          <div className="func-stat-icon">
            <ShoppingCart size={32} />
          </div>
          <div className="func-stat-info">
            <h3>{estatisticasHoje.vendas}</h3>
            <p>Itens Vendidos Hoje</p>
          </div>
        </div>

        <div className="func-stat-card green">
          <div className="func-stat-icon">
            <TrendingUp size={32} />
          </div>
          <div className="func-stat-info">
            <h3>{formatarMoeda(estatisticasHoje.totalVendas)}</h3>
            <p>Total Vendas</p>
          </div>
        </div>
      </div>

      {/* Próxima Sessão */}
      {proximaSessao && (
        <div className="func-proxima-sessao">
          <div className="func-card-header">
            <Film size={24} />
            <h3>Próxima Sessão</h3>
          </div>
          <div className="func-sessao-content">
            <div className="func-sessao-info">
              <h2>{proximaSessao.filmeTitulo}</h2>
              <div className="func-sessao-detalhes">
                <div className="func-detalhe-item">
                  <Calendar size={18} />
                  <span>{formatarDataHora(proximaSessao.horario)}</span>
                </div>
                <div className="func-detalhe-item">
                  <Film size={18} />
                  <span>{proximaSessao.sala}</span>
                </div>
                <div className="func-detalhe-item">
                  <Users size={18} />
                  <span>{proximaSessao.assentosReservados || 0}/{proximaSessao.capacidade} ocupados</span>
                </div>
              </div>
              <div className="func-progress-bar">
                <div
                  className="func-progress-fill"
                  style={{ width: `${((proximaSessao.assentosReservados || 0) / proximaSessao.capacidade) * 100}%` }}
                />
              </div>
            </div>
            <button
              className="func-btn-primary"
              onClick={() => setTelaAtiva('validar')}
            >
              <QrIcon size={20} />
              Ir para Validação
            </button>
          </div>
        </div>
      )}

      {/* Atalhos Rápidos */}
      <div className="func-atalhos-section">
        <h3>Atalhos Rápidos</h3>
        <div className="func-atalhos-grid">
          <button className="func-atalho-card" onClick={() => setTelaAtiva('validar')}>
            <div className="func-atalho-icon purple">
              <QrIcon size={32} />
            </div>
            <span>Validar Ingresso</span>
          </button>
          <button className="func-atalho-card" onClick={() => setTelaAtiva('bomboniere')}>
            <div className="func-atalho-icon blue">
              <ShoppingCart size={32} />
            </div>
            <span>Abrir PDV</span>
          </button>
          <button className="func-atalho-card" onClick={() => setTelaAtiva('historico')}>
            <div className="func-atalho-icon amber">
              <History size={32} />
            </div>
            <span>Ver Histórico</span>
          </button>
          <button className="func-atalho-card" onClick={() => setTelaAtiva('remarcar')}>
            <div className="func-atalho-icon green">
              <RefreshCw size={32} />
            </div>
            <span>Remarcar Ingresso</span>
          </button>
        </div>
      </div>
    </div>
  );

  const renderValidar = () => (
    <div className="func-validar-container">
      <div className="func-section-header">
        <div className="func-header-icon purple">
          <QrIcon size={40} />
        </div>
        <div className="func-header-text">
          <h2>Validar Entrada de Cliente</h2>
          <p>Escaneie o QR Code ou digite o código do ingresso</p>
        </div>
      </div>

      <div className="func-validar-input-area">
        <div className="func-input-group">
          <div className="func-qr-input-wrapper">
            <input
              type="text"
              value={qrCode}
              onChange={(e) => setQrCode(e.target.value.toUpperCase())}
              placeholder="Digite o código (ex: ASTRA001)"
              className="func-qr-input"
              onKeyPress={(e) => e.key === 'Enter' && validarIngresso()}
              autoFocus
            />
            <button
              className="func-btn-scanner"
              onClick={ativarScanner}
              title="Ativar Scanner"
            >
              <Scan size={24} />
            </button>
          </div>

          <button
            className="func-btn-validar"
            onClick={validarIngresso}
            disabled={!qrCode.trim() || carregando}
          >
            {carregando ? (
              <>
                <div className="func-spinner"></div>
                Validando...
              </>
            ) : (
              <>
                <CheckCircle size={20} />
                Validar
              </>
            )}
          </button>
        </div>

        <p className="func-help-text">
          Clique no ícone de scanner para usar a câmera do dispositivo
        </p>
      </div>

      {resultadoValidacao && (
        <div className={`func-resultado-card ${resultadoValidacao.valido ? 'sucesso' : 'erro'}`}>
          <div className="func-resultado-icon">
            {resultadoValidacao.valido ? (
              <CheckCircle size={64} strokeWidth={2.5} />
            ) : (
              <XCircle size={64} strokeWidth={2.5} />
            )}
          </div>

          <h2 className="func-resultado-titulo">{resultadoValidacao.mensagem}</h2>

          {resultadoValidacao.ingresso && (
            <div className="func-resultado-detalhes">
              <div className="func-detalhe-row">
                <span className="label">Assento(s):</span>
                <span className="valor destaque">{resultadoValidacao.ingresso.assento}</span>
              </div>
              <div className="func-detalhe-row">
                <span className="label">Tipo:</span>
                <span className="valor">{resultadoValidacao.ingresso.tipo}</span>
              </div>
              <div className="func-detalhe-row">
                <span className="label">QR Code:</span>
                <span className="valor codigo">{resultadoValidacao.ingresso.qrCode}</span>
              </div>
            </div>
          )}

          {resultadoValidacao.sessao && (
            <div className="func-sessao-info-box">
              <h4>Informações da Sessão</h4>
              <p><strong>Sala:</strong> {resultadoValidacao.sessao.sala}</p>
              <p><strong>Horário:</strong> {formatarDataHora(resultadoValidacao.sessao.horario)}</p>
            </div>
          )}

          <button
            className="func-btn-secondary"
            onClick={() => {
              setQrCode('');
              setResultadoValidacao(null);
            }}
          >
            Nova Validação
          </button>
        </div>
      )}

      {/* Test codes removed: no longer show test QR codes in production UI */}
    </div>
  );

  const renderHistorico = () => (
    <div className="func-historico-container">
      <div className="func-section-header">
        <div className="func-header-icon amber">
          <History size={40} />
        </div>
        <div className="func-header-text">
          <h2>Histórico de Validações</h2>
          <p>Todos os ingressos processados</p>
        </div>
      </div>

      {/* Filtros */}
      <div className="func-filtros">
        <button
          className={`func-filtro-btn ${filtroHistorico === 'todos' ? 'active' : ''}`}
          onClick={() => setFiltroHistorico('todos')}
        >
          Todos
        </button>
        <button
          className={`func-filtro-btn ${filtroHistorico === 'sucesso' ? 'active' : ''}`}
          onClick={() => setFiltroHistorico('sucesso')}
        >
          Sucesso
        </button>
        <button
          className={`func-filtro-btn ${filtroHistorico === 'falha' ? 'active' : ''}`}
          onClick={() => setFiltroHistorico('falha')}
        >
          Falha
        </button>
      </div>

      {filtrarHistorico().length === 0 ? (
        <div className="func-empty-state">
          <History size={64} />
          <h3>Nenhum registro encontrado</h3>
          <p>Ainda não há validações no histórico</p>
        </div>
      ) : (
        <div className="func-historico-lista">
          {filtrarHistorico().map((item) => (
            <div key={item.id} className="func-historico-item">
              <div className="func-historico-info">
                <div className="func-historico-qr">
                  <QrIcon size={20} />
                  <span className="codigo">{item.qrCode}</span>
                </div>
                <div className="func-historico-detalhes">
                  <p><strong>Assento{item.assentos && item.assentos.length > 1 ? 's' : ''}:</strong> {item.assentos ? item.assentos.join(', ') : item.assento}</p>
                  <p><strong>Sessão:</strong> #{item.sessaoId}</p>
                  <p><strong>Data:</strong> {formatarDataHora(item.dataHora)}</p>
                </div>
              </div>
              <div className="func-historico-status">
                {item.sucesso ? (
                  <StatusBadge status="ATIVO" type="ingresso" customLabel="Sucesso" />
                ) : (
                  <StatusBadge status="CANCELADO" type="ingresso" customLabel="Falha" />
                )}
                <p className="func-historico-msg">{item.mensagem}</p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );

  const renderRemarcar = () => (
    <RemarcarNovo />
  );

  const renderBomboniere = () => (
    <div className="func-bomboniere-container">
      <div className="func-section-header">
        <div className="func-header-icon blue">
          <ShoppingCart size={40} />
        </div>
        <div className="func-header-text">
          <h2>Bomboniere - PDV</h2>
          <p>Registrar vendas de produtos</p>
        </div>
      </div>

      {/* Estatísticas de Vendas */}
      <div className="func-stats-grid" style={{ marginBottom: '24px', gridTemplateColumns: 'repeat(2, 1fr)' }}>
        <div className="func-stat-card blue">
          <div className="func-stat-icon">
            <ShoppingCart size={32} />
          </div>
          <div className="func-stat-info">
            <h3>{estatisticasHoje.vendas || 0}</h3>
            <p>Itens Vendidos Hoje</p>
          </div>
        </div>

        <div className="func-stat-card green">
          <div className="func-stat-icon">
            <TrendingUp size={32} />
          </div>
          <div className="func-stat-info">
            <h3>{formatarMoeda(estatisticasHoje.totalVendas || 0)}</h3>
            <p>Total Vendas</p>
          </div>
        </div>
      </div>

      <div className="func-pdv-layout">
        {/* Produtos */}
        <div className="func-produtos-section">
          {/* Filtros de Categoria */}
          <div className="func-categoria-filtros">
            {categoriasProdutos().map(cat => (
              <button
                key={cat}
                className={`func-categoria-btn ${filtroCategoria === cat ? 'active' : ''}`}
                onClick={() => setFiltroCategoria(cat)}
              >
                {typeof cat === 'string' && cat.length > 0
                  ? cat.charAt(0).toUpperCase() + cat.slice(1)
                  : String(cat)}
              </button>
            ))}
          </div>

          <div className="func-produtos-grid">
            {produtosFiltrados().map((produto) => (
              <div
                key={produto.id}
                className={`func-produto-card ${!produto.disponivel ? 'indisponivel' : ''}`}
              >
                <div className="func-produto-info">
                  <h4>{produto.nome}</h4>
                  <p className="func-produto-categoria">{produto.categoria}</p>
                  <p className="func-produto-preco">{formatarMoeda(produto.preco)}</p>
                  <p className="func-produto-estoque">
                    Estoque: <span className={produto.estoque < 10 ? 'estoque-baixo' : ''}>{produto.estoque}</span>
                  </p>
                </div>
                {produto.disponivel ? (
                  <button
                    className="func-btn-adicionar"
                    onClick={() => adicionarAoCarrinho(produto)}
                  >
                    <Plus size={18} />
                    Adicionar
                  </button>
                ) : (
                  <span className="func-badge-indisponivel">Indisponível</span>
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Carrinho */}
        <div className="func-carrinho-section">
          <div className="func-carrinho-header">
            <h3>Carrinho</h3>
            {carrinho.length > 0 && (
              <span className="func-carrinho-badge">{carrinho.length}</span>
            )}
          </div>

          {carrinho.length === 0 ? (
            <div className="func-carrinho-vazio">
              <ShoppingCart size={64} />
              <p>Carrinho vazio</p>
              <small>Adicione produtos para iniciar a venda</small>
            </div>
          ) : (
            <>
              <div className="func-carrinho-itens">
                {carrinho.map((item) => (
                  <div key={item.produtoId} className="func-carrinho-item">
                    <div className="func-item-info">
                      <h4>{item.nome}</h4>
                      <p>{formatarMoeda(item.preco)}</p>
                    </div>
                    <div className="func-item-controles">
                      <div className="func-qty-controls">
                        <button
                          className="func-qty-btn"
                          onClick={() => alterarQuantidade(item.produtoId, item.quantidade - 1)}
                        >
                          <Minus size={16} />
                        </button>
                        <span className="func-qty-valor">{item.quantidade}</span>
                        <button
                          className="func-qty-btn"
                          onClick={() => alterarQuantidade(item.produtoId, item.quantidade + 1)}
                        >
                          <Plus size={16} />
                        </button>
                      </div>
                      <p className="func-item-subtotal">{formatarMoeda(item.preco * item.quantidade)}</p>
                      <button
                        className="func-btn-remover"
                        onClick={() => removerDoCarrinho(item.produtoId)}
                        title="Remover item"
                      >
                        <X size={18} />
                      </button>
                    </div>
                  </div>
                ))}
              </div>

              <div className="func-carrinho-footer">
                <div className="func-total-section">
                  <span className="func-total-label">Total:</span>
                  <span className="func-total-valor">{formatarMoeda(calcularTotal())}</span>
                </div>
                <button
                  className="func-btn-finalizar"
                  onClick={finalizarVenda}
                  disabled={carregando}
                >
                  {carregando ? (
                    <>
                      <div className="func-spinner" style={{ width: '20px', height: '20px', borderWidth: '2px' }}></div>
                      Processando...
                    </>
                  ) : (
                    <>
                      <Check size={20} />
                      Finalizar Venda
                    </>
                  )}
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );

  const renderRelatorios = () => (
    <div className="func-relatorios-container">
      <div className="func-section-header">
        <div className="func-header-icon amber">
          <BarChart3 size={40} />
        </div>
        <div className="func-header-text">
          <h2>Relatórios e Estatísticas</h2>
          <p>Análise detalhada das operações do cinema</p>
        </div>
        <button className="func-btn-refresh-relatorios" onClick={carregarRelatorios}>
          <RefreshCw size={18} />
          Atualizar
        </button>
      </div>

      {/* Cards de Estatísticas de Remarcação */}
      <div className="func-stats-grid" style={{ marginBottom: '32px' }}>
        <div className="func-stat-card green">
          <div className="func-stat-icon">
            <RefreshCw size={32} />
          </div>
          <div className="func-stat-info">
            <h3>{relatorios.estatisticasGerais.totalRemarcacoes}</h3>
            <p>Total de Remarcações</p>
          </div>
        </div>

        <div className="func-stat-card blue">
          <div className="func-stat-icon">
            <Clock size={32} />
          </div>
          <div className="func-stat-info">
            <h3>{relatorios.estatisticasGerais.remarcacoesHoje}</h3>
            <p>Remarcações Hoje</p>
          </div>
        </div>

        <div className="func-stat-card purple">
          <div className="func-stat-icon">
            <Calendar size={32} />
          </div>
          <div className="func-stat-info">
            <h3>{relatorios.estatisticasGerais.remarcacoesSemana}</h3>
            <p>Remarcações (7 dias)</p>
          </div>
        </div>

        <div className="func-stat-card amber">
          <div className="func-stat-icon">
            <Activity size={32} />
          </div>
          <div className="func-stat-info">
            <h3>{((relatorios.estatisticasGerais.remarcacoesSemana / 7) || 0).toFixed(1)}</h3>
            <p>Média por Dia</p>
          </div>
        </div>
      </div>

      <div className="func-relatorios-grid">
        {/* Remarcações Recentes */}
        <div className="func-relatorio-card">
          <div className="func-relatorio-header">
            <div className="func-relatorio-titulo">
              <RefreshCw size={20} />
              <h3>Remarcações Recentes</h3>
            </div>
            <span className="func-badge-count">{relatorios.remarcacoes.length}</span>
          </div>

          <div className="func-relatorio-content">
            {relatorios.remarcacoes.length === 0 ? (
              <div className="func-empty-relatorio">
                <RefreshCw size={48} />
                <p>Nenhuma remarcação registrada</p>
              </div>
            ) : (
              <div className="func-remarcacoes-lista">
                {relatorios.remarcacoes.slice(0, 10).map((remarcacao, idx) => (
                  <div key={idx} className="func-remarcacao-item">
                    <div className="func-remarcacao-info">
                      <span className="func-remarcacao-qr">{remarcacao.qrCode || `REM${String(idx + 1).padStart(3, '0')}`}</span>
                      <span className="func-remarcacao-data">{formatarDataHora(remarcacao.dataRemarcacao)}</span>
                    </div>
                    <div className="func-remarcacao-detalhes">
                      <p className="func-remarcacao-motivo">{remarcacao.motivoTecnico}</p>
                      <span className="func-remarcacao-cliente">{remarcacao.clienteNome || 'Cliente'}</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Motivos Mais Comuns */}
        <div className="func-relatorio-card">
          <div className="func-relatorio-header">
            <div className="func-relatorio-titulo">
              <PieChart size={20} />
              <h3>Motivos de Remarcação</h3>
            </div>
          </div>

          <div className="func-relatorio-content">
            {relatorios.estatisticasGerais.motivosMaisComuns.length === 0 ? (
              <div className="func-empty-relatorio">
                <AlertTriangle size={48} />
                <p>Sem dados de motivos</p>
              </div>
            ) : (
              <div className="func-motivos-lista">
                {relatorios.estatisticasGerais.motivosMaisComuns.map((motivo, idx) => (
                  <div key={idx} className="func-motivo-item">
                    <div className="func-motivo-info">
                      <span className="func-motivo-rank">#{idx + 1}</span>
                      <p className="func-motivo-texto">{motivo.motivo}</p>
                    </div>
                    <div className="func-motivo-stats">
                      <span className="func-motivo-count">{motivo.count}x</span>
                      <div className="func-motivo-barra">
                        <div 
                          className="func-motivo-barra-fill" 
                          style={{ 
                            width: `${(motivo.count / Math.max(...relatorios.estatisticasGerais.motivosMaisComuns.map(m => m.count))) * 100}%` 
                          }}
                        />
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Filmes Populares */}
        <div className="func-relatorio-card">
          <div className="func-relatorio-header">
            <div className="func-relatorio-titulo">
              <Film size={20} />
              <h3>Filmes Mais Populares</h3>
            </div>
          </div>

          <div className="func-relatorio-content">
            {relatorios.filmesPopulares.length === 0 ? (
              <div className="func-empty-relatorio">
                <Film size={48} />
                <p>Sem dados de filmes</p>
              </div>
            ) : (
              <div className="func-filmes-lista">
                {relatorios.filmesPopulares.slice(0, 5).map((filme, idx) => (
                  <div key={idx} className="func-filme-item">
                    <div className="func-filme-rank">
                      <Award size={18} />
                      <span>#{idx + 1}</span>
                    </div>
                    <div className="func-filme-info">
                      <h4>{filme.titulo || `Filme ${idx + 1}`}</h4>
                      <p>{filme.totalIngressos || 0} ingressos vendidos</p>
                    </div>
                    <div className="func-filme-receita">
                      {formatarMoeda(filme.receitaTotal || 0)}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Ocupação de Salas */}
        <div className="func-relatorio-card">
          <div className="func-relatorio-header">
            <div className="func-relatorio-titulo">
              <Users size={20} />
              <h3>Ocupação de Salas</h3>
            </div>
          </div>

          <div className="func-relatorio-content">
            {relatorios.ocupacaoSalas.length === 0 ? (
              <div className="func-empty-relatorio">
                <Users size={48} />
                <p>Sem dados de ocupação</p>
              </div>
            ) : (
              <div className="func-salas-lista">
                {relatorios.ocupacaoSalas.map((sala, idx) => (
                  <div key={idx} className="func-sala-item">
                    <div className="func-sala-header">
                      <h4>{sala.nome || `Sala ${idx + 1}`}</h4>
                      <span className="func-sala-percentual">{sala.ocupacao || 0}%</span>
                    </div>
                    <div className="func-sala-barra">
                      <div 
                        className={`func-sala-barra-fill ${sala.ocupacao >= 80 ? 'alta' : sala.ocupacao >= 50 ? 'media' : 'baixa'}`}
                        style={{ width: `${sala.ocupacao || 0}%` }}
                      />
                    </div>
                    <div className="func-sala-stats">
                      <span>{sala.assentosOcupados || 0}/{sala.capacidade || 0} assentos</span>
                      <span>{sala.sessoesHoje || 0} sessões hoje</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );

  // ==========================================
  // RENDER PRINCIPAL
  // ==========================================
  return (
    <div className="func-panel">
      {/* Header */}
      <header className="func-header">
        <div className="func-header-left">
          <img src="/logo.png" alt="Astra Cinemas" className="func-logo" />
          <div className="func-badge">Funcionário</div>
        </div>

        <div className="func-header-right">
          <div className="func-user-info">
            <span>Conta Demo</span>
          </div>
          <button className="func-logout-btn" onClick={() => onLogout && onLogout()}>
            <LogOut size={20} />
            Sair
          </button>
        </div>
      </header>

      {/* Navigation Tabs */}
      <nav className="func-nav">
        <button
          className={`func-nav-tab ${telaAtiva === 'home' ? 'active' : ''}`}
          onClick={() => setTelaAtiva('home')}
        >
          <Home size={20} />
          <span>Home</span>
        </button>
        <button
          className={`func-nav-tab ${telaAtiva === 'validar' ? 'active' : ''}`}
          onClick={() => setTelaAtiva('validar')}
        >
          <QrIcon size={20} />
          <span>Validar</span>
        </button>
        <button
          className={`func-nav-tab ${telaAtiva === 'bomboniere' ? 'active' : ''}`}
          onClick={() => setTelaAtiva('bomboniere')}
        >
          <ShoppingCart size={20} />
          <span>Bomboniere</span>
        </button>
        <button
          className={`func-nav-tab ${telaAtiva === 'historico' ? 'active' : ''}`}
          onClick={() => setTelaAtiva('historico')}
        >
          <History size={20} />
          <span>Histórico</span>
        </button>
        <button
          className={`func-nav-tab ${telaAtiva === 'remarcar' ? 'active' : ''}`}
          onClick={() => setTelaAtiva('remarcar')}
        >
          <RefreshCw size={20} />
          <span>Remarcar</span>
        </button>
        <button
          className={`func-nav-tab ${telaAtiva === 'relatorios' ? 'active' : ''}`}
          onClick={() => setTelaAtiva('relatorios')}
        >
          <BarChart3 size={20} />
          <span>Relatórios</span>
        </button>
      </nav>

      {/* Content */}
      <main className="func-content">
        {telaAtiva === 'home' && renderHome()}
        {telaAtiva === 'validar' && renderValidar()}
        {telaAtiva === 'historico' && renderHistorico()}
        {telaAtiva === 'remarcar' && renderRemarcar()}
        {telaAtiva === 'bomboniere' && renderBomboniere()}
        {telaAtiva === 'relatorios' && renderRelatorios()}
      </main>
    </div>
  );
};

export default FuncionarioPanel;

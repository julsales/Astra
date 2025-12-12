import React, { useState, useEffect } from 'react';
import './PageStyles.css';

const Overview = ({ usuario }) => {
  const [estatisticas, setEstatisticas] = useState({
    vendasHoje: { valor: 0, variacao: '-', tipo: 'neutro', descricao: 'carregando...' },
    ingressosVendidos: { valor: 0, total: 0, descricao: 'carregando...' },
    ocupacaoMedia: { valor: 0, variacao: 'carregando...', tipo: 'neutro' },
    ticketMedio: { valor: 0, variacao: '-', tipo: 'neutro', descricao: 'carregando...' }
  });

  const [metricas, setMetricas] = useState([
    { label: 'Filmes em Cartaz', valor: 0, icone: '' },
    { label: 'Sessões Programadas', valor: 0, icone: '' },
    { label: 'Usuários Cadastrados', valor: 0, icone: '' }
  ]);

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregarDadosOverview();
  }, []);

  const carregarDadosOverview = async () => {
    try {
      setLoading(true);

      // Carregar filmes
      const resFilmes = await fetch('/api/filmes/em-cartaz');
      const filmes = await resFilmes.json();

      // Carregar todas as sessões
      let todasSessoes = [];
      for (const filme of filmes) {
        const resSessoes = await fetch(`/api/sessoes/filme/${filme.id}`);
        const sessoes = await resSessoes.json();
        todasSessoes.push(...sessoes);
      }

      // Carregar clientes (com tratamento de erro)
      let clientes = [];
      try {
        const resClientes = await fetch('/api/clientes');
        if (resClientes.ok) {
          clientes = await resClientes.json();
        }
      } catch (e) {
        console.warn('Endpoint /api/clientes não disponível');
      }

      // Carregar funcionários (com tratamento de erro)
      let funcionarios = [];
      try {
        const resFuncionarios = await fetch('/api/funcionarios');
        if (resFuncionarios.ok) {
          funcionarios = await resFuncionarios.json();
        }
      } catch (e) {
        console.warn('Endpoint /api/funcionarios não disponível');
      }

      // Carregar produtos
      const resProdutos = await fetch('/api/produtos');
      const produtos = await resProdutos.json();

      // Calcular total de assentos
      const totalAssentos = todasSessoes.reduce((acc, s) => 
        acc + (s.totalAssentos || 0), 0
      );

      // Calcular assentos disponíveis (livres)
      const assentosLivres = todasSessoes.reduce((acc, s) => 
        acc + (s.assentosDisponiveis || 0), 0
      );

      // Calcular assentos ocupados
      const assentosOcupados = totalAssentos - assentosLivres;

      // Calcular ocupação média
      const ocupacaoPercentual = totalAssentos > 0 ? Math.round((assentosOcupados / totalAssentos) * 100) : 0;

      // Calcular valor total em estoque de produtos
      const valorEstoque = produtos.reduce((acc, p) => acc + (p.preco * p.estoque), 0);

      // Calcular total de usuários (garantindo que sejam arrays)
      const totalClientes = Array.isArray(clientes) ? clientes.length : 0;
      const totalFuncionarios = Array.isArray(funcionarios) ? funcionarios.length : 0;
      const totalUsuarios = totalClientes + totalFuncionarios;

      // Buscar estatísticas de vendas do backend
      let totalVendas = 0;
      let vendasHoje = 0;
      try {
        const resEstatisticas = await fetch('/api/funcionario/estatisticas');
        if (resEstatisticas.ok) {
          const stats = await resEstatisticas.json();
          totalVendas = stats.totalVendas || 0;
          vendasHoje = stats.vendasHoje || 0;
        }
      } catch (e) {
        console.warn('Endpoint /api/funcionario/estatisticas não disponível');
      }

      // Atualizar métricas
      setMetricas([
        { label: 'Filmes em Cartaz', valor: filmes.length, icone: '' },
        { label: 'Sessões Programadas', valor: todasSessoes.length, icone: '' },
        { label: 'Usuários Cadastrados', valor: totalUsuarios, icone: '' }
      ]);

      // Calcular estatísticas REAIS e ÚTEIS
      setEstatisticas({
        vendasHoje: {
          valor: vendasHoje,
          variacao: '-',
          tipo: 'neutro',
          descricao: 'vendas bomboniere - hoje'
        },
        totalVendas: {
          valor: totalVendas,
          variacao: '-',
          tipo: 'neutro',
          descricao: 'receita total'
        },
        filmesAtivos: {
          valor: filmes.length,
          variacao: filmes.filter(f => f.status === 'EM_CARTAZ').length,
          tipo: 'neutro',
          descricao: 'filmes disponíveis'
        },
        sessoesAtivas: {
          valor: todasSessoes.length,
          total: totalAssentos,
          descricao: `${totalAssentos} assentos no total`
        },
        ocupacao: {
          valor: ocupacaoPercentual,
          variacao: `${assentosOcupados} ocupados / ${assentosLivres} livres`,
          tipo: ocupacaoPercentual > 70 ? 'bom' : ocupacaoPercentual > 40 ? 'medio' : 'baixo'
        },
        bomboniere: {
          valor: valorEstoque,
          variacao: produtos.length,
          tipo: 'neutro',
          descricao: `${produtos.length} produtos`
        }
      });

    } catch (error) {
      console.error('Erro ao carregar dados do overview:', error);
    } finally {
      setLoading(false);
    }
  };

  // Todas as tabs de navegação e ações rápidas removidas conforme solicitado

  if (loading) {
    return (
      <div className="page-container">
        <div style={{ textAlign: 'center', padding: '60px', color: 'rgba(255,255,255,0.7)' }}>
          <div className="spinner"></div>
          Carregando dados...
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      {/* Cabeçalho da Página */}
      <div className="page-header">
        <div className="page-title-section">
          <h1 className="page-title">Painel Administrativo</h1>
          <p className="page-subtitle">Gerencie o cinema, sessões, promoções e análise de vendas</p>
        </div>
      </div>

      {/* Grid de Estatísticas Principais */}
      <div className="stats-grid-main">
        <div className="stat-card vendas">
          <div className="stat-header">
            <span className="stat-label">Vendas Bomboniere - Hoje</span>
            <div className="stat-icon-circle purple"></div>
          </div>
          <div className="stat-value">{estatisticas.vendasHoje.valor}</div>
          <div className="stat-footer neutro">
            <span>{estatisticas.vendasHoje.descricao}</span>
          </div>
        </div>
        <div className="stat-card ingressos">
          <div className="stat-header">
            <span className="stat-label">Total Vendas</span>
            <div className="stat-icon-circle green"></div>
          </div>
          <div className="stat-value">R$ {estatisticas.totalVendas.valor.toFixed(2)}</div>
          <div className="stat-footer neutro">
            <span>{estatisticas.totalVendas.descricao}</span>
          </div>
        </div>
        <div className="stat-card ocupacao">
          <div className="stat-header">
            <span className="stat-label">Taxa de Ocupação - Hoje</span>
            <div className="stat-icon-circle orange"></div>
          </div>
          <div className="stat-value">{estatisticas.ocupacao.valor}%</div>
          <div className="stat-footer neutro">
            <span>{estatisticas.ocupacao.variacao}</span>
          </div>
        </div>
        <div className="stat-card ticket">
          <div className="stat-header">
            <span className="stat-label">Filmes em Cartaz</span>
            <div className="stat-icon-circle blue"></div>
          </div>
          <div className="stat-value">{estatisticas.filmesAtivos.valor}</div>
          <div className="stat-footer neutro">
            <span>{estatisticas.filmesAtivos.descricao}</span>
          </div>
        </div>
      </div>

      {/* Métricas Secundárias */}
      <div className="metrics-row">
        {metricas.map((metrica, index) => (
          <div key={index} className="metric-card">
            <div className="metric-icon">{metrica.icone}</div>
            <div className="metric-info">
              <div className="metric-label">{metrica.label}</div>
              <div className="metric-value">{metrica.valor}</div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Overview;

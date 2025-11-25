import React, { useState, useEffect } from 'react';
import './PageStyles.css';
import { SearchIcon, ViewIcon } from '../Icons';

const Relatorios = ({ usuario }) => {
  const [dados, setDados] = useState({
    filmes: [],
    sessoes: [],
    produtos: [],
    clientes: [],
    funcionarios: []
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregarDadosRelatorio();
  }, []);

  const carregarDadosRelatorio = async () => {
    try {
      setLoading(true);

      // Carregar todos os dados
      const [resFilmes, resProdutos, resClientes, resFuncionarios] = await Promise.all([
        fetch('/api/filmes/em-cartaz'),
        fetch('/api/produtos'),
        fetch('/api/clientes'),
        fetch('/api/funcionarios')
      ]);

      const filmes = await resFilmes.json();
      const produtos = await resProdutos.json();
      const clientes = await resClientes.json();
      const funcionarios = await resFuncionarios.json();

      // Carregar sessões de todos os filmes
      let todasSessoes = [];
      for (const filme of filmes) {
        const resSessoes = await fetch(`/api/sessoes/filme/${filme.id}`);
        const sessoes = await resSessoes.json();
        todasSessoes.push(...sessoes);
      }

      setDados({
        filmes,
        sessoes: todasSessoes,
        produtos,
        clientes,
        funcionarios
      });
    } catch (error) {
      console.error('Erro ao carregar dados do relatório:', error);
    } finally {
      setLoading(false);
    }
  };

  const calcularEstatisticas = () => {
    const totalAssentos = dados.sessoes.reduce((acc, s) => 
      acc + Object.keys(s.assentosDisponiveis || {}).length, 0
    );
    
    const valorEstoqueProdutos = dados.produtos.reduce((acc, p) => 
      acc + (p.preco * p.estoque), 0
    );

    return {
      totalFilmes: dados.filmes.length,
      totalSessoes: dados.sessoes.length,
      totalAssentos,
      totalProdutos: dados.produtos.length,
      valorEstoque: valorEstoqueProdutos,
      totalUsuarios: dados.clientes.length + dados.funcionarios.length,
      totalClientes: dados.clientes.length,
      totalFuncionarios: dados.funcionarios.length
    };
  };

  if (loading) {
    return (
      <div className="page-container">
        <div style={{ textAlign: 'center', padding: '60px', color: 'rgba(255,255,255,0.7)' }}>
          <div className="spinner"></div>
          Carregando relatórios...
        </div>
      </div>
    );
  }

  const stats = calcularEstatisticas();

  return (
    <div className="page-container">
      <div className="page-header">
        <div className="page-title-section">
          <h1 className="page-title">
            <span className="page-icon"></span>
            Relatórios e Analytics
          </h1>
          <p className="page-subtitle">
            Análises detalhadas de vendas e performance
          </p>
        </div>
        <button className="btn-primary" onClick={carregarDadosRelatorio}>
          <SearchIcon size={18} /> Atualizar
        </button>
      </div>

      {/* Estatísticas Gerais */}
      <div className="stats-grid-main">
        <div className="stat-card">
          <div className="stat-header">
            <span className="stat-label">Total de Filmes</span>
            <div className="stat-icon-circle purple"></div>
          </div>
          <div className="stat-value">{stats.totalFilmes}</div>
          <div className="stat-footer neutro">
            <span>em cartaz</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-header">
            <span className="stat-label">Sessões Programadas</span>
            <div className="stat-icon-circle blue"></div>
          </div>
          <div className="stat-value">{stats.totalSessoes}</div>
          <div className="stat-footer neutro">
            <span>{stats.totalAssentos} assentos</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-header">
            <span className="stat-label">Produtos Bomboniere</span>
            <div className="stat-icon-circle orange"></div>
          </div>
          <div className="stat-value">{stats.totalProdutos}</div>
          <div className="stat-footer neutro">
            <span>R$ {stats.valorEstoque.toFixed(2)} em estoque</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-header">
            <span className="stat-label">Usuários Totais</span>
            <div className="stat-icon-circle green"></div>
          </div>
          <div className="stat-value">{stats.totalUsuarios}</div>
          <div className="stat-footer neutro">
            <span>{stats.totalClientes} clientes • {stats.totalFuncionarios} funcionários</span>
          </div>
        </div>
      </div>

      {/* Detalhamento por Categoria */}
      <div className="section-container">
        <h2 className="section-title">Resumo por Categoria</h2>
        
        <div style={{ display: 'grid', gap: '20px', marginTop: '20px' }}>
          {/* Filmes */}
          <div style={{
            padding: '20px',
            background: 'rgba(30,20,60,0.4)',
            borderRadius: '12px',
            border: '1px solid rgba(139,92,246,0.2)'
          }}>
            <h3 style={{ color: 'white', marginBottom: '15px' }}> Filmes em Cartaz</h3>
            <div style={{ display: 'grid', gap: '10px' }}>
              {dados.filmes.map(filme => (
                <div key={filme.id} style={{
                  padding: '10px',
                  background: 'rgba(139,92,246,0.1)',
                  borderRadius: '8px',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center'
                }}>
                  <span style={{ color: 'white' }}>{filme.titulo}</span>
                  <div style={{ display: 'flex', gap: '10px', fontSize: '12px', color: 'rgba(255,255,255,0.6)' }}>
                    <span>{filme.duracao} min</span>
                    <span>•</span>
                    <span>{filme.classificacaoEtaria}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Produtos */}
          <div style={{
            padding: '20px',
            background: 'rgba(30,20,60,0.4)',
            borderRadius: '12px',
            border: '1px solid rgba(139,92,246,0.2)'
          }}>
            <h3 style={{ color: 'white', marginBottom: '15px' }}> Produtos Bomboniere</h3>
            <div style={{ display: 'grid', gap: '10px' }}>
              {dados.produtos.map(produto => (
                <div key={produto.id} style={{
                  padding: '10px',
                  background: 'rgba(139,92,246,0.1)',
                  borderRadius: '8px',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center'
                }}>
                  <span style={{ color: 'white' }}>{produto.nome}</span>
                  <div style={{ display: 'flex', gap: '15px', fontSize: '12px' }}>
                    <span style={{ color: '#34D399' }}>R$ {produto.preco.toFixed(2)}</span>
                    <span style={{ color: produto.estoque > 0 ? 'rgba(255,255,255,0.6)' : '#FF6B6B' }}>
                      {produto.estoque} un.
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Relatorios;

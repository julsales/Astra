import React, { useState, useEffect, useCallback, useMemo } from 'react';
import { gerarQrCodeDataUrl } from '../../../utils/qr';
import { useMeusIngressos } from '../../../hooks/useMeusIngressos';
import { useFetch } from '../../../hooks/useFetch';
import { formatarData, formatarHora, formatarMoeda } from '../../../utils/formatters';

const CompraIngresso = ({ sessao, filme, usuario, onVoltar, onConcluir }) => {
  const [etapa, setEtapa] = useState(1); // 1=assentos, 2=tipos, 3=bomboniere, 4=pagamento
  const [assentos, setAssentos] = useState([]);
  const [assentosSelecionados, setAssentosSelecionados] = useState([]);
  const [tipoIngresso, setTipoIngresso] = useState('inteira'); // 'inteira' ou 'meia'
  const [metodoPagamento, setMetodoPagamento] = useState('');
  const [processando, setProcessando] = useState(false);
  const [carregando, setCarregando] = useState(true);
  const [produtos, setProdutos] = useState([]);
  const [itensBomboniere, setItensBomboniere] = useState({});
  const [carregandoProdutos, setCarregandoProdutos] = useState(false);

  const { registrarCompra } = useMeusIngressos(usuario);

  // Buscar preços dinâmicos do backend
  const { data: precos } = useFetch('/api/precos');

  const carregarAssentos = useCallback(async () => {
    if (!sessao?.id) {
      setAssentos([]);
      setCarregando(false);
      return;
    }

    setCarregando(true);
    try {
      // REGRA: SEMPRE usar o backend para disponibilidade de assentos
      const res = await fetch(`/api/sessoes/${sessao.id}/assentos`);
      if (!res.ok) throw new Error('Falha ao buscar assentos do backend');
      
      const data = await res.json();
      
      // O backend retorna { sessaoId, capacidade, assentos: { "A01": true, "A02": false, ... } }
      // true = disponível, false = ocupado
      const mapaBackend = data.assentos;
      
      // Converter para formato do frontend
      const assentosArray = Object.entries(mapaBackend).map(([id, disponivel]) => ({
        id,
        disponivel,
        fila: id.charAt(0),
        numero: parseInt(id.substring(1))
      }));
      
      // Ordenar por fila e número
      assentosArray.sort((a, b) => {
        if (a.fila !== b.fila) return a.fila.localeCompare(b.fila);
        return a.numero - b.numero;
      });
      
      
      setAssentos(assentosArray);
    } catch (e) {
      console.error('Erro ao carregar assentos do backend:', e);
      alert('Erro ao carregar disponibilidade de assentos. Tente novamente.');
      setAssentos([]);
    } finally {
      setCarregando(false);
    }
  }, [sessao]);

  useEffect(() => {
    carregarAssentos();
  }, [carregarAssentos]);

  useEffect(() => {
    const carregarProdutos = async () => {
      setCarregandoProdutos(true);
      try {
        const response = await fetch('/api/produtos');
        if (!response.ok) {
          throw new Error('Falha ao carregar produtos da bomboniere');
        }
        const data = await response.json();
        setProdutos(Array.isArray(data) ? data : []);
      } catch (error) {
        console.error('Erro ao carregar produtos:', error);
      } finally {
        setCarregandoProdutos(false);
      }
    };

    carregarProdutos();
  }, []);

  const reservarAssentosSelecionados = async () => {
    if (!sessao?.id) {
      throw new Error('Sessão inválida. Volte e selecione novamente.');
    }

    if (!assentosSelecionados.length) {
      throw new Error('Nenhum assento foi selecionado.');
    }

    const response = await fetch(`/api/sessoes/${sessao.id}/assentos/reservar`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        assentos: assentosSelecionados,
        clienteId: usuario?.id ?? null,
        origem: 'CLIENTE_APP'
      })
    });

    let payload = {};
    try {
      payload = await response.json();
    } catch (error) {
      payload = {};
    }

    if (!response.ok) {
      throw new Error(payload?.mensagem || 'Falha ao reservar assentos. Atualize e tente novamente.');
    }

    return payload;
  };

  const toggleAssento = (assentoId) => {
    if (assentosSelecionados.includes(assentoId)) {
      setAssentosSelecionados(assentosSelecionados.filter(id => id !== assentoId));
    } else {
      setAssentosSelecionados([...assentosSelecionados, assentoId]);
    }
  };

  // Preço dos ingressos (otimizado com useMemo)
  const precoIngressos = useMemo(() => {
    if (!precos) return 0;
    const precoBase = tipoIngresso === 'inteira'
      ? (precos.ingressoInteiro || 35.0)
      : (precos.ingressoMeia || 17.5);
    return assentosSelecionados.length * precoBase;
  }, [assentosSelecionados.length, tipoIngresso, precos]);

  const atualizarQuantidadeProduto = (produtoId, delta, limite = Infinity) => {
    setItensBomboniere((prev) => {
      const atual = prev[produtoId] || 0;
      let novoValor = atual + delta;
      if (novoValor < 0) novoValor = 0;
      if (novoValor > limite) novoValor = limite;
      const atualizado = { ...prev };
      if (novoValor === 0) {
        delete atualizado[produtoId];
      } else {
        atualizado[produtoId] = novoValor;
      }
      return atualizado;
    });
  };

  // Total da bomboniere (otimizado com useMemo)
  const totalBomboniere = useMemo(() => {
    return Object.entries(itensBomboniere).reduce((total, [id, quantidade]) => {
      const produto = produtos.find((p) => String(p.id) === String(id));
      if (!produto) return total;
      return total + (produto.preco || 0) * quantidade;
    }, 0);
  }, [itensBomboniere, produtos]);

  // Total geral (otimizado com useMemo)
  const totalGeral = useMemo(() => {
    return precoIngressos + totalBomboniere;
  }, [precoIngressos, totalBomboniere]);

  const obterQuantidadeProduto = (produtoId) => itensBomboniere[produtoId] || 0;

  const itensBomboniereSelecionados = produtos
    .filter((produto) => itensBomboniere[produto.id])
    .map((produto) => {
      const quantidade = itensBomboniere[produto.id];
      return {
        ...produto,
        quantidade,
        subtotal: (produto.preco || 0) * quantidade
      };
    });

  const finalizarCompra = async () => {
    if (!metodoPagamento) {
      alert('Selecione um método de pagamento');
      return;
    }

    if (!sessao?.id) {
      alert('Sessão inválida. Volte e selecione novamente.');
      return;
    }

    if (assentosSelecionados.length === 0) {
      alert('Selecione ao menos um assento.');
      return;
    }

    if (!usuario?.id) {
      alert('Usuário não identificado. Faça login novamente.');
      return;
    }

    setProcessando(true);
    try {
      await reservarAssentosSelecionados();
      await carregarAssentos();

      // Chama o backend para criar a compra (gera QR Codes automaticamente)
      // Converter produtos da bomboniere para o formato do backend
      const produtosParaBackend = Object.entries(itensBomboniere).map(([produtoId, quantidade]) => ({
        produtoId: parseInt(produtoId),
        quantidade: quantidade
      }));

      const payload = {
        clienteId: usuario.id,
        sessaoId: sessao.id,
        assentos: assentosSelecionados,
        tipoIngresso: tipoIngresso.toUpperCase(), // 'INTEIRA' ou 'MEIA'
        produtos: produtosParaBackend // Adiciona produtos da bomboniere
      };


      const response = await fetch('/api/compras', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        // Tenta ler o corpo como JSON, se falhar lê como texto para mensagens mais claras
        let errBody;
        try {
          errBody = await response.json();
        } catch (e) {
          try {
            errBody = await response.text();
          } catch (e2) {
            errBody = '<não foi possível ler corpo de erro>';
          }
        }
        const mensagem = errBody && errBody.erro ? errBody.erro : (typeof errBody === 'string' ? errBody : 'Erro ao criar compra');
        throw new Error(mensagem);
      }

      const compraBackend = await response.json();
      
      // Gera QR Code visual para cada ingresso
      const ingressosComQrCode = await Promise.all(
        compraBackend.ingressos.map(async (ingresso) => {
          const qrCodeDataUrl = await gerarQrCodeDataUrl(ingresso.qrCode);
          return {
            ...ingresso,
            qrCodeDataUrl
          };
        })
      );

      const totalBomboniereCompra = totalBomboniere;
      const totalIngressosCompra = precoIngressos;
      
      // Usa o primeiro QR Code para compatibilidade (ou pode mostrar todos)
      const primeiroIngresso = ingressosComQrCode[0];
      
      // NÃO registra localmente - deixa a sincronização automática do HomeCliente fazer isso
      // Isso evita duplicatas (registro local + sincronização backend)

      setAssentosSelecionados([]);
      setItensBomboniere({});
      setEtapa(1);

      setTimeout(async () => {
        setProcessando(false);
        // Força sincronização imediata antes de voltar
        try {
          await new Promise(resolve => setTimeout(resolve, 500)); // Aguarda backend processar
        } catch (e) {
          console.error('Erro ao aguardar sincronização', e);
        }
        onConcluir(); // Volta para HomeCliente que vai sincronizar automaticamente
      }, 1500);
    } catch (error) {
      console.error('Erro ao finalizar compra:', error);
      setProcessando(false);
      alert(error?.message || 'Erro ao processar pagamento');
    }
  };

  return (
    <div className="compra-ingresso-wrapper">
      {/* Header com breadcrumb */}
      <header className="compra-header">
        <button className="btn-voltar-compra" onClick={onVoltar}>
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M19 12H5M12 19l-7-7 7-7" />
          </svg>
          Voltar
        </button>
        <div className="compra-breadcrumb">
          <span className={etapa >= 1 ? 'active' : ''}>1. Assentos</span>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M9 18l6-6-6-6" />
          </svg>
          <span className={etapa >= 2 ? 'active' : ''}>2. Tipos</span>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M9 18l6-6-6-6" />
          </svg>
          <span className={etapa >= 3 ? 'active' : ''}>3. Bomboniere</span>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M9 18l6-6-6-6" />
          </svg>
          <span className={etapa >= 4 ? 'active' : ''}>4. Pagamento</span>
        </div>
      </header>

      <div className="compra-content">
        {/* Sidebar com resumo */}
        <aside className="compra-sidebar">
          <h3>Comprar Ingresso</h3>
          <div className="resumo-filme">
            <h4>{filme.titulo}</h4>
            <p className="resumo-codigo">Código: {filme.codigo || `ASTRA${String(filme.id).padStart(3, '0')}`}</p>
          </div>

          <div className="resumo-detalhes">
            <div className="resumo-item">
              <span className="label">Data:</span>
              <span className="valor">{formatarData(sessao.horario)}</span>
            </div>
            <div className="resumo-item">
              <span className="label">Horário:</span>
              <span className="valor">{formatarHora(sessao.horario)}</span>
            </div>
            <div className="resumo-item">
              <span className="label">Sala:</span>
              <span className="valor">{sessao.sala}</span>
            </div>
          </div>

          {assentosSelecionados.length > 0 && (
            <>
              <div className="resumo-assentos">
                <h5>Assentos Selecionados:</h5>
                <p>{assentosSelecionados.join(', ')}</p>
              </div>

              <div className="resumo-tipo">
                <h5>Tipo de Ingresso:</h5>
                <p>{tipoIngresso === 'inteira' ? 'Inteira' : 'Meia-entrada'}</p>
              </div>

              <div className="resumo-total">
                <span>Total ingressos:</span>
                <span className="preco-destaque">{formatarMoeda(precoIngressos)}</span>
              </div>
            </>
          )}

          {itensBomboniereSelecionados.length > 0 && (
            <div className="resumo-bomboniere">
              <h5>Bomboniere</h5>
              <ul>
                {itensBomboniereSelecionados.map((item) => (
                  <li key={item.id}>
                    <span>{item.quantidade}x {item.nome}</span>
                    <span>R$ {item.subtotal.toFixed(2)}</span>
                  </li>
                ))}
              </ul>
              <div className="resumo-total">
                <span>Total combos:</span>
                <span className="preco-destaque">{formatarMoeda(totalBomboniere)}</span>
              </div>
            </div>
          )}

          <div className="resumo-geral">
            <div className="resumo-geral-linha">
              <span>Ingressos</span>
              <span>{formatarMoeda(precoIngressos)}</span>
            </div>
            <div className="resumo-geral-linha">
              <span>Bomboniere</span>
              <span>{formatarMoeda(totalBomboniere)}</span>
            </div>
            <div className="resumo-geral-total">
              <span>Total geral</span>
              <strong>{formatarMoeda(totalGeral)}</strong>
            </div>
          </div>
        </aside>

        {/* Área principal - muda conforme a etapa */}
        <main className="compra-main">
          {etapa === 1 && (
            <div className="etapa-assentos">
              <h2>Escolha seus assentos</h2>
              <p className="etapa-subtitulo">Clique nos assentos desejados para selecioná-los</p>

              {carregando ? (
                <div className="loading-state">
                  <div className="spinner"></div>
                  <p>Carregando assentos...</p>
                </div>
              ) : (
                <>
                  <div className="legenda-assentos-nova">
                    <div className="legenda-item-nova">
                      <span className="assento-demo disponivel"></span>
                      <span>Disponível</span>
                    </div>
                    <div className="legenda-item-nova">
                      <span className="assento-demo selecionado"></span>
                      <span>Selecionado</span>
                    </div>
                    <div className="legenda-item-nova">
                      <span className="assento-demo ocupado"></span>
                      <span>Ocupado</span>
                    </div>
                  </div>

                  <div className="tela-cinema-nova">
                    <div className="tela-label-nova">TELA</div>
                  </div>

                  <div className="grid-assentos-nova">
                    {assentos.map((assento) => (
                      <button
                        key={assento.id}
                        className={`assento-btn ${!assento.disponivel ? 'ocupado' : ''} ${assentosSelecionados.includes(assento.id) ? 'selecionado' : ''}`}
                        onClick={() => assento.disponivel && toggleAssento(assento.id)}
                        disabled={!assento.disponivel}
                      >
                        {assento.id}
                      </button>
                    ))}
                  </div>

                  <div className="compra-acoes">
                    <button 
                      className="btn-primary-largo"
                      onClick={() => setEtapa(2)}
                      disabled={assentosSelecionados.length === 0}
                    >
                      Continuar
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M5 12h14M12 5l7 7-7 7" />
                      </svg>
                    </button>
                  </div>
                </>
              )}
            </div>
          )}

          {etapa === 2 && (
            <div className="etapa-tipos">
              <h2>Tipo de Ingresso</h2>
              <p className="etapa-subtitulo">Selecione o tipo de ingresso</p>

              <div className="tipos-grid">
                <button 
                  className={`tipo-card ${tipoIngresso === 'inteira' ? 'selecionado' : ''}`}
                  onClick={() => setTipoIngresso('inteira')}
                >
                  <div className="tipo-icon">
                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
                      <circle cx="12" cy="7" r="4" />
                    </svg>
                  </div>
                  <h3>Inteira</h3>
                  <p className="tipo-preco">{formatarMoeda(precos?.ingressoInteiro || 35.0)}</p>
                  <p className="tipo-desc">Por assento</p>
                </button>

                <button
                  className={`tipo-card ${tipoIngresso === 'meia' ? 'selecionado' : ''}`}
                  onClick={() => setTipoIngresso('meia')}
                >
                  <div className="tipo-icon">
                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M16 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" />
                      <circle cx="8.5" cy="7" r="4" />
                      <path d="M20 8v6M23 11h-6" />
                    </svg>
                  </div>
                  <h3>Meia-entrada</h3>
                  <p className="tipo-preco">{formatarMoeda(precos?.ingressoMeia || 17.5)}</p>
                  <p className="tipo-desc">Por assento</p>
                  <span className="tipo-badge">50% OFF</span>
                </button>
              </div>

              <div className="meia-entrada-info">
                <h4>Quem tem direito à meia-entrada?</h4>
                <ul>
                  <li>Estudantes com carteirinha válida</li>
                  <li>Idosos (acima de 60 anos)</li>
                  <li>Pessoas com deficiência</li>
                  <li>Professores da rede pública</li>
                </ul>
                <p className="meia-aviso">*Apresente documento comprobatório na entrada</p>
              </div>

              <div className="compra-acoes">
                <button className="btn-secondary-largo" onClick={() => setEtapa(1)}>
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M19 12H5M12 19l-7-7 7-7" />
                  </svg>
                  Voltar
                </button>
                <button className="btn-primary-largo" onClick={() => setEtapa(3)}>
                  Ir para bomboniere
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M5 12h14M12 5l7 7-7 7" />
                  </svg>
                </button>
              </div>
            </div>
          )}

          {etapa === 3 && (
            <div className="etapa-bomboniere">
              <div className="bomboniere-hero">
                <div>
                  <h2>Bomboniere futurista</h2>
                  <p className="etapa-subtitulo">
                    Combine pipocas, bebidas e doces reais do nosso estoque digital.
                  </p>
                </div>
                <div className="bomboniere-total-hero">
                  <span>Total combos</span>
                  <strong>{formatarMoeda(totalBomboniere)}</strong>
                </div>
              </div>

              {carregandoProdutos ? (
                <div className="loading-state">
                  <div className="spinner"></div>
                  <p>Carregando delícias da bomboniere...</p>
                </div>
              ) : produtos.length === 0 ? (
                <div className="empty-state">
                  <p>Nenhum produto disponível agora. Nossa equipe já está reabastecendo.</p>
                </div>
              ) : (
                <div className="bomboniere-grid">
                  {produtos.map((produto) => {
                    const quantidade = obterQuantidadeProduto(produto.id);
                    const limite = typeof produto.estoque === 'number' ? produto.estoque : Infinity;
                    const semEstoque = Number.isFinite(limite) && limite === 0;
                    return (
                      <article key={produto.id} className={`bomboniere-card ${quantidade ? 'selecionado' : ''}`}>
                        <div className="bomboniere-card-body">
                          <h3>{produto.nome}</h3>
                          <p className="bomboniere-preco">R$ {produto.preco?.toFixed(2)}</p>
                          <p className="bomboniere-estoque">
                            {produto.estoque != null ? `${produto.estoque} em estoque` : 'Estoque em atualização'}
                          </p>
                          {semEstoque && <span className="bomboniere-tag">Esgotado</span>}
                        </div>
                        <div className="bomboniere-controles">
                          <button
                            className="btn-quantidade"
                            onClick={() => atualizarQuantidadeProduto(produto.id, -1, limite)}
                            disabled={quantidade === 0}
                          >
                            –
                          </button>
                          <span className="quantidade-valor">{quantidade}</span>
                          <button
                            className="btn-quantidade"
                            onClick={() => atualizarQuantidadeProduto(produto.id, 1, limite)}
                            disabled={semEstoque || quantidade >= limite}
                          >
                            +
                          </button>
                        </div>
                      </article>
                    );
                  })}
                </div>
              )}

              <div className="bomboniere-carrinho">
                <div className="bomboniere-carrinho-header">
                  <h4>Seu carrinho</h4>
                  <span>{itensBomboniereSelecionados.length} itens</span>
                </div>
                {itensBomboniereSelecionados.length === 0 ? (
                  <p className="bomboniere-carrinho-vazio">
                    Comece adicionando combos — você retira tudo pronto ao chegar.
                  </p>
                ) : (
                  <ul>
                    {itensBomboniereSelecionados.map((item) => (
                      <li key={item.id}>
                        <div>
                          <strong>{item.nome}</strong>
                          <span>{item.quantidade}x</span>
                        </div>
                        <span>R$ {item.subtotal.toFixed(2)}</span>
                      </li>
                    ))}
                  </ul>
                )}

                <div className="bomboniere-carrinho-total">
                  <span>Total combos</span>
                  <strong>{formatarMoeda(totalBomboniere)}</strong>
                </div>
              </div>

              <div className="compra-acoes">
                <button className="btn-secondary-largo" onClick={() => setEtapa(2)}>
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M19 12H5M12 19l-7-7 7-7" />
                  </svg>
                  Voltar
                </button>
                <button className="btn-primary-largo" onClick={() => setEtapa(4)}>
                  Ir para pagamento
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M5 12h14M12 5l7 7-7 7" />
                  </svg>
                </button>
              </div>
            </div>
          )}

          {etapa === 4 && (
            <div className="etapa-pagamento">
              <h2>Pagamento</h2>
              <p className="etapa-subtitulo">Escolha a forma de pagamento</p>

              <div className="pagamento-grid">
                <button 
                  className={`pagamento-card ${metodoPagamento === 'PIX' ? 'selecionado' : ''}`}
                  onClick={() => setMetodoPagamento('PIX')}
                >
                  <div className="pagamento-icon">
                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <rect x="3" y="3" width="18" height="18" rx="2" />
                      <path d="M9 9h.01M9 15h.01M15 9h.01M15 15h.01" />
                    </svg>
                  </div>
                  <h3>PIX</h3>
                  <p>Aprovação instantânea</p>
                </button>

                <button 
                  className={`pagamento-card ${metodoPagamento === 'CREDITO' ? 'selecionado' : ''}`}
                  onClick={() => setMetodoPagamento('CREDITO')}
                >
                  <div className="pagamento-icon">
                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <rect x="1" y="4" width="22" height="16" rx="2" />
                      <path d="M1 10h22" />
                    </svg>
                  </div>
                  <h3>Crédito</h3>
                  <p>Até 3x sem juros</p>
                </button>

                <button 
                  className={`pagamento-card ${metodoPagamento === 'DEBITO' ? 'selecionado' : ''}`}
                  onClick={() => setMetodoPagamento('DEBITO')}
                >
                  <div className="pagamento-icon">
                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <rect x="1" y="4" width="22" height="16" rx="2" />
                      <path d="M1 10h22M7 15h.01" />
                    </svg>
                  </div>
                  <h3>Débito</h3>
                  <p>Desconto à vista</p>
                </button>
              </div>

              <div className="pagamento-resumo">
                <div>
                  <span>Ingressos</span>
                  <strong>{formatarMoeda(precoIngressos)}</strong>
                </div>
                <div>
                  <span>Bomboniere</span>
                  <strong>{formatarMoeda(totalBomboniere)}</strong>
                </div>
                <div className="pagamento-total-geral">
                  <span>Total a pagar</span>
                  <strong>{formatarMoeda(totalGeral)}</strong>
                </div>
              </div>

              <div className="compra-acoes">
                <button className="btn-secondary-largo" onClick={() => setEtapa(3)}>
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M19 12H5M12 19l-7-7 7-7" />
                  </svg>
                  Voltar
                </button>
                <button 
                  className="btn-primary-largo btn-finalizar"
                  onClick={finalizarCompra}
                  disabled={!metodoPagamento || processando}
                >
                  {processando ? (
                    <>
                      <div className="spinner-pequeno"></div>
                      Processando...
                    </>
                  ) : (
                    <>
                      Finalizar Compra
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M9 11l3 3L22 4" />
                        <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11" />
                      </svg>
                    </>
                  )}
                </button>
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default CompraIngresso;

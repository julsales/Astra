import { useCallback, useEffect, useMemo, useState } from 'react';
import { gerarQrCodeDataUrl } from '../utils/qr';

const formatarSessao = (sessao = {}) => ({
  id: sessao.id ?? null,
  horario: sessao.horario ?? new Date().toISOString(),
  sala: sessao.sala ?? 'Sala 1',
});

const gerarCodigoAleatorio = () =>
  Math.random().toString(36).substring(2, 10).toUpperCase();

const gerarIdCompra = () => {
  if (typeof crypto !== 'undefined' && crypto.randomUUID) {
    return crypto.randomUUID();
  }
  return `compra-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;
};

const normalizarCompra = (compra) => ({
  id: compra.id ?? gerarIdCompra(),
  codigo: compra.codigo ?? gerarCodigoAleatorio(),
  dataCompra: compra.dataCompra ?? new Date().toISOString(),
  filme: compra.filme,
  sessao: formatarSessao(compra.sessao),
  assentos: Array.isArray(compra.assentos) ? compra.assentos : [],
  produtos: Array.isArray(compra.produtos) ? compra.produtos : [],
  total: compra.total ?? 0,
  metodoPagamento: compra.metodoPagamento ?? 'NAO_INFORMADO',
  status: compra.status ?? 'CONFIRMADO',
  qrCode: compra.qrCode ?? '',
});

export const useMeusIngressos = (usuario) => {
  const storageKey = useMemo(
    () => `meus-ingressos-${usuario?.clienteId ?? usuario?.id ?? 'anonimo'}`,
    [usuario?.clienteId, usuario?.id]
  );

  const [ingressos, setIngressos] = useState([]);

  useEffect(() => {
    try {
      const salvo = typeof window !== 'undefined' ? window.localStorage.getItem(storageKey) : null;
      let ingressosCarregados = salvo ? JSON.parse(salvo) : [];

      // Remove duplicatas ao carregar (baseado em código QR, ID ou sessão+assentos)
      const semDuplicatas = [];
      const codigosVistos = new Set();
      const idsVistos = new Set();
      const chavesAssentosVistas = new Set();

      for (const ingresso of ingressosCarregados) {
        const codigo = ingresso.codigo || ingresso.qrCode;
        const id = ingresso.id;

        // Cria chave única por sessão+assentos
        let chaveAssento = null;
        if (ingresso.sessao && ingresso.assentos && Array.isArray(ingresso.assentos)) {
          const assentosOrdenados = [...ingresso.assentos].sort().join(',');
          chaveAssento = `${ingresso.sessao.id}-${assentosOrdenados}`;
        }

        // Se já vimos este código, ID ou chave de assento, pula
        const jaDuplicado =
          (codigo && codigosVistos.has(codigo)) ||
          (id && idsVistos.has(id)) ||
          (chaveAssento && chavesAssentosVistas.has(chaveAssento));

        if (jaDuplicado) {
          continue;
        }

        if (codigo) codigosVistos.add(codigo);
        if (id) idsVistos.add(id);
        if (chaveAssento) chavesAssentosVistas.add(chaveAssento);
        semDuplicatas.push(ingresso);
      }

      // Se removeu duplicatas, salva a versão limpa
      if (semDuplicatas.length < ingressosCarregados.length) {
        console.log(`Removidas ${ingressosCarregados.length - semDuplicatas.length} duplicatas do localStorage`);
        if (typeof window !== 'undefined') {
          window.localStorage.setItem(storageKey, JSON.stringify(semDuplicatas));
        }
      }

      setIngressos(semDuplicatas);
    } catch (error) {
      console.error('Erro ao recuperar ingressos:', error);
      setIngressos([]);
    }
  }, [storageKey]);

  const persistir = useCallback(
    (atualizador) => {
      setIngressos((estadoAnterior) => {
        const proximoEstado = typeof atualizador === 'function' ? atualizador(estadoAnterior) : atualizador;
        if (typeof window !== 'undefined') {
          window.localStorage.setItem(storageKey, JSON.stringify(proximoEstado));
        }
        return proximoEstado;
      });
    },
    [storageKey]
  );

  const registrarCompra = useCallback(
    (compra) => {
      const novaCompra = normalizarCompra(compra);
      persistir((listaAtual) => {
        // Remove duplicatas baseando-se em id OU código
        const semDuplicados = listaAtual.filter((c) =>
          c.id !== novaCompra.id &&
          (!novaCompra.codigo || c.codigo !== novaCompra.codigo)
        );
        return [novaCompra, ...semDuplicados];
      });
      return novaCompra;
    },
    [persistir]
  );

    // Sincronizar com backend (quando houver um usuário autenticado)
    const sincronizarComBackend = useCallback(async () => {
      if (!usuario || !usuario.clienteId) return;
      try {
        console.log('Sincronizando ingressos com backend...');
        // Busca TODOS os ingressos (ativos E cancelados) do usuário logado
        const res = await fetch(`/api/ingressos?clienteId=${usuario.clienteId}&_t=${Date.now()}`);
        if (!res.ok) return;
        const dados = await res.json();
        console.log('Dados recebidos do backend:', dados);

        // AGRUPAR ingressos por compra (mesmo qrCode = mesma compra)
        // Backend retorna 1 ingresso por assento, mas frontend trata como 1 compra com múltiplos assentos
        const ingressosPorQrCode = new Map();

        dados.forEach((i) => {
          const qrCode = i.qrCode;
          if (!qrCode) return;

          if (!ingressosPorQrCode.has(qrCode)) {
            // Primeira vez vendo este qrCode - criar entrada
            ingressosPorQrCode.set(qrCode, i);
          }
          // Se já existe, ignora (porque todos os tickets da mesma compra têm o mesmo qrCode)
        });

        // Mapear para o formato interno usado pelo frontend
        const compras = await (async () => {
          const promises = Array.from(ingressosPorQrCode.values()).map((i) => {
            // Converter string "A1, A2, A3" em array ["A1", "A2", "A3"]
            let assentos = [];
            if (i.assento) {
              if (typeof i.assento === 'string') {
                assentos = i.assento.split(',').map(a => a.trim()).filter(Boolean);
              } else if (Array.isArray(i.assento)) {
                assentos = i.assento;
              }
            } else if (i.assentoIndividual) {
              assentos = [i.assentoIndividual];
            }

            const qrPromise = i.qrCode
              ? gerarQrCodeDataUrl(i.qrCode).catch((err) => {
                  console.error('Erro ao gerar QR data URL durante sincronização:', err);
                  return '';
                })
              : Promise.resolve('');

            return qrPromise.then((qrDataUrl) => ({
              id: i.id || `ing-${i.qrCode}`,
              codigo: i.qrCode || '',
              dataCompra: new Date().toISOString(),
              filme: { titulo: (i.filme && i.filme.titulo) || '' },
              sessao: { id: i.sessaoId, horario: i.horario || new Date().toISOString(), sala: i.sala || 'Sala 1' },
              assentos: assentos,
              produtos: Array.isArray(i.produtos) ? i.produtos : [],
              total: i.total ?? 0,
              metodoPagamento: 'NAO_INFORMADO',
              status: i.status || 'ATIVO',
              qrCode: qrDataUrl,
              ingressosDetalhados: i.ingressosDetalhados || [],  // ← Adiciona detalhes dos ingressos
            }));
          });

          return Promise.all(promises);
        })();

        console.log('Compras processadas do backend:', compras);

        persistir((listaAtual) => {
          // Remove todos os ingressos que vieram do backend (evita duplicação)
          // Mantém apenas compras locais que ainda não foram sincronizadas
          const idsBackend = new Set(compras.map(c => c.id));
          const codigosBackend = new Set(compras.map(c => c.codigo).filter(Boolean));

          // Cria um Set de chaves únicas para identificar ingressos duplicados
          // Formato: "sessaoId-assento1,assento2-tipoIngresso"
          const chavesDuplicacao = new Set();
          compras.forEach(c => {
            if (c.sessao && c.assentos && Array.isArray(c.assentos)) {
              const assentosOrdenados = [...c.assentos].sort().join(',');
              const chave = `${c.sessao.id}-${assentosOrdenados}`;
              chavesDuplicacao.add(chave);
            }
          });

          const apenasLocais = listaAtual.filter((c) => {
            // Mantém se não está no backend (por ID ou código)
            const naoEstaNoBackendPorId = !idsBackend.has(c.id) && !codigosBackend.has(c.codigo);

            // Também verifica se não é duplicata por sessão+assentos
            let naoDuplicadoPorAssento = true;
            if (c.sessao && c.assentos && Array.isArray(c.assentos)) {
              const assentosOrdenados = [...c.assentos].sort().join(',');
              const chave = `${c.sessao.id}-${assentosOrdenados}`;
              naoDuplicadoPorAssento = !chavesDuplicacao.has(chave);
            }

            return naoEstaNoBackendPorId && naoDuplicadoPorAssento;
          });

          return [...compras, ...apenasLocais];
        });
      } catch (err) {
        console.error('Falha ao sincronizar ingressos com backend:', err);
      }
    }, [usuario, persistir]);

  const removerCompra = useCallback(
    (id) => {
      persistir((listaAtual) => listaAtual.filter((compra) => compra.id !== id));
    },
    [persistir]
  );

  const cancelarCompra = useCallback(
    async (compraId) => {
      try {
        // Chama o backend para cancelar
        const response = await fetch(`/api/compras/${compraId}`, {
          method: 'DELETE',
        });

        // Se a compra não existe no backend (404 ou 500), marca como cancelada no localStorage
        if (response.status === 404 || response.status === 500) {
          console.warn(`Compra ${compraId} não encontrada no backend, marcando como cancelada no localStorage`);
          persistir((listaAtual) =>
            listaAtual.map((compra) =>
              compra.id === compraId || String(compra.id) === String(compraId)
                ? { ...compra, status: 'CANCELADO' }
                : compra
            )
          );
          return { sucesso: true, cancelada: true };
        }

        if (!response.ok) {
          const erro = await response.json();
          throw new Error(erro.erro || 'Erro ao cancelar compra');
        }

        // Atualiza status para CANCELADO no localStorage (mantém visível)
        persistir((listaAtual) =>
          listaAtual.map((compra) =>
            compra.id === compraId || String(compra.id) === String(compraId)
              ? { ...compra, status: 'CANCELADO' }
              : compra
          )
        );

        // Sincronizar com backend para atualizar a lista
        await sincronizarComBackend();

        return { sucesso: true };
      } catch (error) {
        console.error('Erro ao cancelar compra:', error);
        return { sucesso: false, erro: error.message };
      }
    },
    [persistir, sincronizarComBackend]
  );

  const limparHistorico = useCallback(() => {
    persistir([]);
  }, [persistir]);

  return {
    ingressos,
    registrarCompra,
    removerCompra,
    cancelarCompra,
    limparHistorico,
    sincronizarComBackend,
  };
};

import { useCallback, useEffect, useMemo, useState } from 'react';

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
    () => `meus-ingressos-${usuario?.id ?? 'anonimo'}`,
    [usuario?.id]
  );

  const [ingressos, setIngressos] = useState([]);

  useEffect(() => {
    try {
      const salvo = typeof window !== 'undefined' ? window.localStorage.getItem(storageKey) : null;
      setIngressos(salvo ? JSON.parse(salvo) : []);
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
        const semDuplicados = listaAtual.filter((c) => c.id !== novaCompra.id);
        return [novaCompra, ...semDuplicados];
      });
      return novaCompra;
    },
    [persistir]
  );

    // Sincronizar com backend (quando houver um usuário autenticado)
    const sincronizarComBackend = useCallback(async () => {
      if (!usuario || !usuario.id) return;
      try {
        const res = await fetch('/api/ingressos/ativos');
        if (!res.ok) return;
        const dados = await res.json();
        // Mapear para o formato interno usado pelo frontend
        const compras = dados.map((i) => {
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

          return {
            id: i.id || `ing-${i.qrCode}`,
            codigo: i.qrCode || '',
            dataCompra: new Date().toISOString(),
            filme: { titulo: i.filme?.titulo || '' },
            sessao: { id: i.sessaoId, horario: i.horario || new Date().toISOString(), sala: i.sala || 'Sala 1' },
            assentos: assentos,
            produtos: [],
            total: i.total ?? 0,
            metodoPagamento: 'NAO_INFORMADO',
            status: i.status === 'UTILIZADO' ? 'VALIDADO' : (i.status ?? 'PENDENTE'),
            qrCode: i.qrCode || '',
          };
        });

        persistir((listaAtual) => {
          // Substitui ingressos que tenham mesmo qrCode/id
          const restantes = listaAtual.filter((c) => !compras.some((n) => n.id === c.id || n.codigo === c.codigo));
          return [...compras, ...restantes];
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

  const limparHistorico = useCallback(() => {
    persistir([]);
  }, [persistir]);

  return {
    ingressos,
    registrarCompra,
    removerCompra,
    limparHistorico,
    sincronizarComBackend,
  };
};

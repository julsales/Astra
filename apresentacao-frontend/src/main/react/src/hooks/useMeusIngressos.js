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
  };
};

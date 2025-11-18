import { useState, useEffect, useCallback } from 'react';

/**
 * Hook customizado para requisições HTTP
 * Centraliza lógica de loading, error e data
 *
 * @param {string} url - URL da API
 * @param {object} options - Opções do fetch (método, headers, body, etc.)
 * @param {boolean} autoFetch - Se deve fazer fetch automaticamente (padrão: true)
 * @returns {object} { data, loading, error, refetch }
 */
export const useFetch = (url, options = {}, autoFetch = true) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(autoFetch);
  const [error, setError] = useState(null);

  const fetchData = useCallback(async () => {
    if (!url) {
      setError('URL não fornecida');
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const response = await fetch(url, {
        headers: {
          'Content-Type': 'application/json',
          ...options.headers
        },
        ...options
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(
          errorData.mensagem || errorData.message || `Erro ${response.status}`
        );
      }

      const json = await response.json();
      setData(json);
      return json;
    } catch (err) {
      setError(err.message || 'Erro ao carregar dados');
      console.error('Erro no useFetch:', err);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [url, JSON.stringify(options)]);

  useEffect(() => {
    if (autoFetch) {
      fetchData();
    }
  }, [autoFetch, fetchData]);

  const refetch = useCallback(() => {
    return fetchData();
  }, [fetchData]);

  return { data, loading, error, refetch };
};

/**
 * Hook para requisições POST/PUT/DELETE
 * Não faz fetch automático, apenas quando chamado
 *
 * @param {string} url - URL da API
 * @param {object} options - Opções do fetch
 * @returns {object} { mutate, data, loading, error }
 */
export const useMutation = (url, options = {}) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const mutate = useCallback(
    async (body) => {
      try {
        setLoading(true);
        setError(null);

        const response = await fetch(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            ...options.headers
          },
          body: JSON.stringify(body),
          ...options
        });

        if (!response.ok) {
          const errorData = await response.json().catch(() => ({}));
          throw new Error(
            errorData.mensagem ||
              errorData.message ||
              `Erro ${response.status}`
          );
        }

        const json = await response.json();
        setData(json);
        return json;
      } catch (err) {
        setError(err.message || 'Erro na operação');
        console.error('Erro no useMutation:', err);
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [url, JSON.stringify(options)]
  );

  return { mutate, data, loading, error };
};

export default useFetch;

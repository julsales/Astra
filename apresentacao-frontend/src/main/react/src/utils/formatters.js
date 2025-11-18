/**
 * Utilitários de formatação centralizados
 * Evita duplicação de código em múltiplos componentes
 */

/**
 * Formata uma data ISO para formato brasileiro (DD/MM/YYYY)
 * @param {string} iso - Data em formato ISO
 * @returns {string} Data formatada
 */
export const formatarData = (iso) => {
  if (!iso) return '-';
  const data = new Date(iso);
  return data.toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
};

/**
 * Formata uma hora ISO para formato brasileiro (HH:MM)
 * @param {string} iso - Data/hora em formato ISO
 * @returns {string} Hora formatada
 */
export const formatarHora = (iso) => {
  if (!iso) return '-';
  const data = new Date(iso);
  return data.toLocaleTimeString('pt-BR', {
    hour: '2-digit',
    minute: '2-digit'
  });
};

/**
 * Formata uma data/hora ISO completa (DD/MM/YYYY HH:MM)
 * @param {string} iso - Data/hora em formato ISO
 * @returns {string} Data e hora formatadas
 */
export const formatarDataHora = (iso) => {
  if (!iso) return '-';
  const data = new Date(iso);
  return data.toLocaleString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

/**
 * Formata um valor monetário para formato brasileiro (R$ 0,00)
 * @param {number} valor - Valor numérico
 * @returns {string} Valor formatado
 */
export const formatarMoeda = (valor) => {
  if (valor === null || valor === undefined) return 'R$ 0,00';
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(valor);
};

/**
 * Formata duração em minutos para formato legível (Xh Ymin)
 * @param {number} minutos - Duração em minutos
 * @returns {string} Duração formatada
 */
export const formatarDuracao = (minutos) => {
  if (!minutos) return '-';
  const horas = Math.floor(minutos / 60);
  const mins = minutos % 60;

  if (horas === 0) return `${mins}min`;
  if (mins === 0) return `${horas}h`;
  return `${horas}h ${mins}min`;
};

/**
 * Trunca um texto longo adicionando reticências
 * @param {string} texto - Texto a truncar
 * @param {number} maxLength - Comprimento máximo
 * @returns {string} Texto truncado
 */
export const truncarTexto = (texto, maxLength = 100) => {
  if (!texto) return '';
  if (texto.length <= maxLength) return texto;
  return texto.substring(0, maxLength) + '...';
};

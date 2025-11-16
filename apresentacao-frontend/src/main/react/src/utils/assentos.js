const COLUNAS_PADRAO = 10;

const gerarIdAssento = (linhaIndex, colunaIndex) => {
  const letra = String.fromCharCode(65 + linhaIndex);
  const numero = String(colunaIndex + 1).padStart(2, '0');
  return `${letra}${numero}`;
};

const pseudoRandom = (seed) => {
  let h = 0;
  const texto = seed.toString();
  for (let i = 0; i < texto.length; i += 1) {
    h = Math.imul(31, h) + texto.charCodeAt(i) | 0;
  }
  return () => {
    h = Math.imul(48271, h) % 0x7fffffff;
    return (h & 0x7fffffff) / 0x7fffffff;
  };
};

export const generateSeatMap = ({
  capacidade = 60,
  assentosDisponiveis = capacidade,
  sessaoId = 'generica',
  colunas = COLUNAS_PADRAO,
} = {}) => {
  const total = Math.max(capacidade, 30);
  const linhas = Math.ceil(total / colunas);
  const indisponiveis = Math.max(0, Math.min(capacidade, capacidade - assentosDisponiveis));
  const random = pseudoRandom(`${sessaoId}-${total}`);

  const assentos = [];
  for (let linha = 0; linha < linhas; linha += 1) {
    for (let coluna = 0; coluna < colunas; coluna += 1) {
      if (assentos.length >= total) break;
      assentos.push({ id: gerarIdAssento(linha, coluna), disponivel: true });
    }
  }

  let bloqueados = 0;
  while (bloqueados < indisponiveis && bloqueados < assentos.length) {
    const indice = Math.floor(random() * assentos.length);
    if (assentos[indice].disponivel) {
      assentos[indice].disponivel = false;
      bloqueados += 1;
    }
  }

  return assentos;
};

export const marcarAssentosIndisponiveis = (assentos, ids) => {
  const idsSet = new Set(ids);
  return assentos.map((assento) => ({
    ...assento,
    disponivel: idsSet.has(assento.id) ? false : assento.disponivel,
  }));
};

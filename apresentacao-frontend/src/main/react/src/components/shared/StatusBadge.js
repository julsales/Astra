import React from 'react';
import './StatusBadge.css';

/**
 * Componente reutilizável para exibição de badges de status
 * Padroniza a visualização de status em todo o sistema
 *
 * @param {string} status - O status a ser exibido
 * @param {string} type - Tipo de entidade (filme, sessao, produto, ingresso, compra, venda)
 * @param {string} customLabel - Label customizado (opcional)
 */
export const StatusBadge = ({ status, type, customLabel }) => {
  if (!status) return null;

  // Mapeamento de classes CSS por tipo e status
  const classMap = {
    filme: {
      EM_CARTAZ: 'badge-success',
      EM_BREVE: 'badge-warning',
      RETIRADO: 'badge-danger'
    },
    sessao: {
      DISPONIVEL: 'badge-success',
      ESGOTADA: 'badge-danger',
      CANCELADA: 'badge-neutral'
    },
    produto: {
      DISPONIVEL: 'badge-success',
      ESGOTADO: 'badge-danger'
    },
    ingresso: {
      ATIVO: 'badge-warning',
      VALIDADO: 'badge-success',
      CONFIRMADO: 'badge-success',
      CANCELADO: 'badge-danger'
    },
    compra: {
      PENDENTE: 'badge-warning',
      CONFIRMADA: 'badge-success',
      CANCELADA: 'badge-danger'
    },
    venda: {
      PENDENTE: 'badge-warning',
      CONFIRMADA: 'badge-success',
      CANCELADA: 'badge-danger'
    }
  };

  // Mapeamento de labels amigáveis (opcional)
  const labelMap = {
    filme: {
      EM_CARTAZ: 'Em Cartaz',
      EM_BREVE: 'Em Breve',
      RETIRADO: 'Retirado'
    },
    sessao: {
      DISPONIVEL: 'Disponível',
      ESGOTADA: 'Esgotada',
      CANCELADA: 'Cancelada'
    },
    produto: {
      DISPONIVEL: 'Disponível',
      ESGOTADO: 'Esgotado'
    },
    ingresso: {
      ATIVO: 'Ativo',
      VALIDADO: 'Usado',
      CONFIRMADO: 'Confirmado',
      CANCELADO: 'Cancelado'
    },
    compra: {
      PENDENTE: 'Pendente',
      CONFIRMADA: 'Confirmada',
      CANCELADA: 'Cancelada'
    },
    venda: {
      PENDENTE: 'Pendente',
      CONFIRMADA: 'Confirmada',
      CANCELADA: 'Cancelada'
    }
  };

  // Determina a classe CSS baseada no tipo e status
  const badgeClass = classMap[type]?.[status] || 'badge-neutral';

  // Determina o label a exibir
  const label = customLabel || labelMap[type]?.[status] || status;

  return <span className={`status-badge ${badgeClass}`}>{label}</span>;
};

export default StatusBadge;

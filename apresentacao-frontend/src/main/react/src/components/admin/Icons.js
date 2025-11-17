import React from 'react';

// Componente base para ícones com gradiente roxo
const IconBase = ({ children, size = 24, ...props }) => (
  <svg
    width={size}
    height={size}
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    {...props}
  >
    <defs>
      <linearGradient id="purpleGradient" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="#8B5CF6" />
        <stop offset="100%" stopColor="#7C3AED" />
      </linearGradient>
      <linearGradient id="purpleOrangeGradient" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="#8B5CF6" />
        <stop offset="50%" stopColor="#A78BFA" />
        <stop offset="100%" stopColor="#FFA500" />
      </linearGradient>
    </defs>
    {children}
  </svg>
);

// Ícone de Dashboard/Overview
export const OverviewIcon = ({ size = 24 }) => (
  <IconBase size={size}>
    <path
      d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"
      fill="url(#purpleGradient)"
    />
    <path
      d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"
      stroke="url(#purpleOrangeGradient)"
      strokeWidth="0.5"
      opacity="0.6"
    />
  </IconBase>
);

// Ícone de Filme
export const FilmeIcon = ({ size = 24 }) => (
  <IconBase size={size}>
    <path
      d="M18 4l2 4h-3l-2-4h-2l2 4h-3l-2-4H8l2 4H7L5 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V4h-4z"
      fill="url(#purpleGradient)"
    />
    <circle cx="12" cy="13" r="4" fill="rgba(255,255,255,0.2)" />
    <path
      d="M18 4l2 4h-3l-2-4h-2l2 4h-3l-2-4H8l2 4H7L5 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V4h-4z"
      stroke="url(#purpleOrangeGradient)"
      strokeWidth="0.5"
      opacity="0.6"
    />
  </IconBase>
);

// Ícone de Sessão
export const SessaoIcon = ({ size = 24 }) => (
  <IconBase size={size}>
    <path
      d="M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2z"
      fill="url(#purpleGradient)"
    />
    <path
      d="M10 16.5l6-4.5-6-4.5v9z"
      fill="rgba(255,255,255,0.9)"
    />
    <circle cx="20" cy="18" r="3" fill="#FFA500" opacity="0.8" />
    <path
      d="M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2z"
      stroke="url(#purpleOrangeGradient)"
      strokeWidth="0.5"
      opacity="0.6"
    />
  </IconBase>
);

// Ícone de Bomboniere/Promoções
export const BomboniereIcon = ({ size = 24 }) => (
  <IconBase size={size}>
    <path
      d="M18 6h-2c0-2.21-1.79-4-4-4S8 3.79 8 6H6c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm-6-2c1.1 0 2 .9 2 2h-4c0-1.1.9-2 2-2zm0 10c-2.76 0-5-2.24-5-5h2c0 1.66 1.34 3 3 3s3-1.34 3-3h2c0 2.76-2.24 5-5 5z"
      fill="url(#purpleGradient)"
    />
    <circle cx="8" cy="9" r="1.5" fill="#FFA500" />
    <circle cx="16" cy="9" r="1.5" fill="#FFA500" />
    <path
      d="M18 6h-2c0-2.21-1.79-4-4-4S8 3.79 8 6H6c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2z"
      stroke="url(#purpleOrangeGradient)"
      strokeWidth="0.5"
      opacity="0.6"
    />
  </IconBase>
);

// Ícone de Relatórios
export const RelatorioIcon = ({ size = 24 }) => (
  <IconBase size={size}>
    <path
      d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"
      fill="url(#purpleGradient)"
    />
    <path
      d="M7 10l4-4 4 4 4-4"
      stroke="#FFA500"
      strokeWidth="1.5"
      strokeLinecap="round"
      fill="none"
      opacity="0.7"
    />
    <path
      d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2z"
      stroke="url(#purpleOrangeGradient)"
      strokeWidth="0.5"
      opacity="0.6"
    />
  </IconBase>
);

// Ícone de Usuários
export const UsuarioIcon = ({ size = 24 }) => (
  <IconBase size={size}>
    <path
      d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"
      fill="url(#purpleGradient)"
    />
    <circle cx="8" cy="8" r="2" fill="rgba(255,255,255,0.3)" />
    <circle cx="16" cy="8" r="2" fill="rgba(255,165,0,0.3)" />
    <path
      d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3z"
      stroke="url(#purpleOrangeGradient)"
      strokeWidth="0.5"
      opacity="0.6"
    />
  </IconBase>
);

// Ícones extras para ações CRUD
export const AddIcon = ({ size = 24 }) => (
  <IconBase size={size}>
    <circle cx="12" cy="12" r="10" fill="url(#purpleGradient)" />
    <path
      d="M12 7v10M7 12h10"
      stroke="white"
      strokeWidth="2"
      strokeLinecap="round"
    />
  </IconBase>
);

export const EditIcon = ({ size = 24 }) => (
  <IconBase size={size}>
    <path
      d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"
      fill="url(#purpleGradient)"
    />
    <path
      d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25z"
      stroke="#FFA500"
      strokeWidth="0.5"
      opacity="0.6"
    />
  </IconBase>
);

export const DeleteIcon = ({ size = 24 }) => (
  <IconBase size={size}>
    <path
      d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"
      fill="url(#purpleGradient)"
    />
    <path
      d="M9 9h2v8H9zm4 0h2v8h-2z"
      fill="rgba(255,255,255,0.3)"
    />
  </IconBase>
);

export const ViewIcon = ({ size = 24 }) => (
  <IconBase size={size}>
    <path
      d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"
      fill="url(#purpleGradient)"
    />
    <circle cx="12" cy="12" r="2" fill="#FFA500" opacity="0.8" />
  </IconBase>
);

export const SearchIcon = ({ size = 24 }) => (
  <IconBase size={size}>
    <path
      d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"
      fill="url(#purpleGradient)"
    />
    <circle cx="9.5" cy="9.5" r="3" fill="rgba(255,165,0,0.2)" />
  </IconBase>
);

export const SaveIcon = ({ size = 24 }) => (
  <IconBase size={size}>
    <path
      d="M17 3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V7l-4-4zm-5 16c-1.66 0-3-1.34-3-3s1.34-3 3-3 3 1.34 3 3-1.34 3-3 3zm3-10H5V5h10v4z"
      fill="url(#purpleGradient)"
    />
    <circle cx="12" cy="16" r="2" fill="rgba(255,255,255,0.3)" />
    <path
      d="M17 3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V7l-4-4z"
      stroke="#FFA500"
      strokeWidth="0.5"
      opacity="0.5"
    />
  </IconBase>
);

export const CancelIcon = ({ size = 24 }) => (
  <IconBase size={size}>
    <circle cx="12" cy="12" r="10" fill="url(#purpleGradient)" />
    <path
      d="M15 9l-6 6M9 9l6 6"
      stroke="white"
      strokeWidth="2"
      strokeLinecap="round"
    />
  </IconBase>
);

export default {
  OverviewIcon,
  FilmeIcon,
  SessaoIcon,
  BomboniereIcon,
  RelatorioIcon,
  UsuarioIcon,
  AddIcon,
  EditIcon,
  DeleteIcon,
  ViewIcon,
  SearchIcon,
  SaveIcon,
  CancelIcon,
};

import React from 'react';
import { LogOut } from 'lucide-react';

const LogoutButton = ({ onLogout }) => {
  const style = {
    position: 'fixed',
    top: 12,
    right: 12,
    zIndex: 2000,
    background: 'rgba(0,0,0,0.4)',
    color: '#fff',
    border: '1px solid rgba(255,255,255,0.08)',
    padding: '8px 12px',
    borderRadius: 8,
    display: 'flex',
    alignItems: 'center',
    gap: 8,
    cursor: 'pointer',
    backdropFilter: 'blur(6px)'
  };

  return (
    <button style={style} onClick={() => onLogout && onLogout()} title="Sair">
      <LogOut size={16} />
      <span style={{fontSize: 14}}>Sair</span>
    </button>
  );
};

export default LogoutButton;

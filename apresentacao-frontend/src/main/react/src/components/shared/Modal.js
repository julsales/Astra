import React, { useEffect } from 'react';
import './Modal.css';

/**
 * Componente Modal reutilizável
 * Padroniza a exibição de modais em todo o sistema
 *
 * @param {boolean} isOpen - Controla a visibilidade do modal
 * @param {function} onClose - Callback ao fechar o modal
 * @param {string} title - Título do modal
 * @param {React.ReactNode} children - Conteúdo do modal
 * @param {string} size - Tamanho do modal (sm, md, lg, xl)
 * @param {boolean} closeOnOverlay - Fechar ao clicar no overlay (padrão: true)
 * @param {boolean} showCloseButton - Exibir botão X de fechar (padrão: true)
 */
export const Modal = ({
  isOpen,
  onClose,
  title,
  children,
  size = 'md',
  closeOnOverlay = true,
  showCloseButton = true
}) => {
  // Fecha modal com ESC
  useEffect(() => {
    const handleEscape = (e) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };

    document.addEventListener('keydown', handleEscape);
    return () => document.removeEventListener('keydown', handleEscape);
  }, [isOpen, onClose]);

  // Previne scroll do body quando modal está aberto
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }

    return () => {
      document.body.style.overflow = 'unset';
    };
  }, [isOpen]);

  if (!isOpen) return null;

  const handleOverlayClick = () => {
    if (closeOnOverlay) {
      onClose();
    }
  };

  return (
    <div className="modal-overlay" onClick={handleOverlayClick}>
      <div
        className={`modal-content modal-${size}`}
        onClick={(e) => e.stopPropagation()}
      >
        {(title || showCloseButton) && (
          <div className="modal-header">
            {title && <h2 className="modal-title">{title}</h2>}
            {showCloseButton && (
              <button
                className="modal-close-btn"
                onClick={onClose}
                aria-label="Fechar modal"
              >
                ×
              </button>
            )}
          </div>
        )}

        <div className="modal-body">{children}</div>
      </div>
    </div>
  );
};

/**
 * Componente ModalFooter para ações do modal
 * Usa dentro do children do Modal
 */
export const ModalFooter = ({ children, align = 'right' }) => {
  return (
    <div className={`modal-footer modal-footer-${align}`}>{children}</div>
  );
};

export default Modal;

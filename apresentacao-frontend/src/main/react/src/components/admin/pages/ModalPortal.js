import { useEffect } from 'react';
import { createPortal } from 'react-dom';

const ModalPortal = ({ children, isOpen }) => {
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

  return createPortal(
    children,
    document.body
  );
};

export default ModalPortal;

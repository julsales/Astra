import { render, screen } from '@testing-library/react';
import App from './App';

test('renderiza tela de login com CTA Entrar', () => {
  render(<App />);
  const botaoEntrar = screen.getByRole('button', { name: /entrar/i });
  expect(botaoEntrar).toBeInTheDocument();
  expect(screen.getByText(/Bem-vindo/i)).toBeInTheDocument();
});

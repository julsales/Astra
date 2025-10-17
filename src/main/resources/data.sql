-- ========================================
-- DADOS INICIAIS - ASTRA CINEMAS
-- ========================================
-- Senhas criptografadas com BCrypt (senha original: "123456")
-- BCrypt hash: $2a$10$XQx8VxPZJKPZXJxw6N2.xuJYE7HbWqPYhZmXvJ5Q9ZqE5oNYzJ0vC

-- Inserindo Clientes
INSERT INTO usuarios (tipo_usuario, email, senha, nome, telefone, ativo, cpf, pontos_fidelidade) VALUES
('CLIENTE', 'cliente@teste.com', '$2a$10$XQx8VxPZJKPZXJxw6N2.xuJYE7HbWqPYhZmXvJ5Q9ZqE5oNYzJ0vC', 'João Silva', '11999999999', true, '12345678901', 100),
('CLIENTE', 'maria@teste.com', '$2a$10$XQx8VxPZJKPZXJxw6N2.xuJYE7HbWqPYhZmXvJ5Q9ZqE5oNYzJ0vC', 'Maria Santos', '11988888888', true, '98765432100', 50),
('CLIENTE', 'pedro@teste.com', '$2a$10$XQx8VxPZJKPZXJxw6N2.xuJYE7HbWqPYhZmXvJ5Q9ZqE5oNYzJ0vC', 'Pedro Oliveira', '11977777777', true, '11122233344', 200);

-- Inserindo Funcionários
INSERT INTO usuarios (tipo_usuario, email, senha, nome, telefone, ativo, matricula, cargo, setor, salario) VALUES
('FUNCIONARIO', 'funcionario@teste.com', '$2a$10$XQx8VxPZJKPZXJxw6N2.xuJYE7HbWqPYhZmXvJ5Q9ZqE5oNYzJ0vC', 'Ana Costa', '11966666666', true, 'FUNC001', 'Atendente', 'Bilheteria', 2500.00),
('FUNCIONARIO', 'operador@teste.com', '$2a$10$XQx8VxPZJKPZXJxw6N2.xuJYE7HbWqPYhZmXvJ5Q9ZqE5oNYzJ0vC', 'Carlos Mendes', '11955555555', true, 'FUNC002', 'Operador de Projeção', 'Projeção', 3000.00);

-- Inserindo Administradores
INSERT INTO usuarios (tipo_usuario, email, senha, nome, telefone, ativo, nivel_acesso, departamento) VALUES
('ADMINISTRADOR', 'admin@teste.com', '$2a$10$XQx8VxPZJKPZXJxw6N2.xuJYE7HbWqPYhZmXvJ5Q9ZqE5oNYzJ0vC', 'Roberto Diretor', '11944444444', true, 3, 'Diretoria'),
('ADMINISTRADOR', 'gerente@teste.com', '$2a$10$XQx8VxPZJKPZXJxw6N2.xuJYE7HbWqPYhZmXvJ5Q9ZqE5oNYzJ0vC', 'Juliana Gerente', '11933333333', true, 2, 'Gerência');

-- ========================================
-- DADOS INICIAIS - ASTRA CINEMAS
-- ========================================

-- Inserindo Clientes
INSERT INTO usuarios (tipo_usuario, email, senha, nome, telefone, ativo, cpf, pontos_fidelidade) VALUES
('CLIENTE', 'cliente@teste.com', '123456', 'João Silva', '11999999999', true, '12345678901', 100),
('CLIENTE', 'maria@teste.com', '123456', 'Maria Santos', '11988888888', true, '98765432100', 50),
('CLIENTE', 'pedro@teste.com', '123456', 'Pedro Oliveira', '11977777777', true, '11122233344', 200);

-- Inserindo Funcionários
INSERT INTO usuarios (tipo_usuario, email, senha, nome, telefone, ativo, matricula, cargo, setor, salario) VALUES
('FUNCIONARIO', 'funcionario@teste.com', '123456', 'Ana Costa', '11966666666', true, 'FUNC001', 'Atendente', 'Bilheteria', 2500.00),
('FUNCIONARIO', 'operador@teste.com', '123456', 'Carlos Mendes', '11955555555', true, 'FUNC002', 'Operador de Projeção', 'Projeção', 3000.00);

-- Inserindo Administradores
INSERT INTO usuarios (tipo_usuario, email, senha, nome, telefone, ativo, nivel_acesso, departamento) VALUES
('ADMINISTRADOR', 'admin@teste.com', '123456', 'Roberto Diretor', '11944444444', true, 3, 'Diretoria'),
('ADMINISTRADOR', 'gerente@teste.com', '123456', 'Juliana Gerente', '11933333333', true, 2, 'Gerência');

-- ===============================================
-- ASTRA CINEMA - DADOS INICIAIS
-- ===============================================

-- Inserindo filmes (sem ID explícito para usar auto-incremento)
INSERT INTO FILME (titulo, sinopse, classificacao_etaria, duracao, status, imagem_url) VALUES
('Duna 2', 'Paul Atreides se une aos Fremen em uma jornada de vingança contra aqueles que destruíram sua família.', '14 anos', 166, 'EM_CARTAZ', 'https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=900&q=80'),
('Matrix', 'Um hacker descobre a verdade sobre sua realidade e seu papel na guerra contra seus controladores.', '14 anos', 136, 'EM_CARTAZ', 'https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=900&q=80'),
('Avatar 3', 'Jake Sully e Neytiri enfrentam novos desafios em Pandora.', '12 anos', 195, 'RETIRADO', 'https://images.unsplash.com/photo-1502139214982-d0ad755818d8?auto=format&fit=crop&w=900&q=80'),
('Oppenheimer', 'A história de J. Robert Oppenheimer e seu papel no desenvolvimento da bomba atômica.', '14 anos', 180, 'EM_CARTAZ', 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=900&q=80'),
('Barbie', 'Barbie e Ken vivem uma aventura no mundo real.', 'Livre', 114, 'EM_BREVE', 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=900&q=80');

-- Inserindo funcionários (sem ID explícito para usar auto-incremento)
INSERT INTO FUNCIONARIO (nome, cargo) VALUES ('João Silva', 'GERENTE');
INSERT INTO FUNCIONARIO (nome, cargo) VALUES ('Maria Santos', 'ATENDENTE');
INSERT INTO FUNCIONARIO (nome, cargo) VALUES ('Pedro Oliveira', 'GERENTE');
INSERT INTO FUNCIONARIO (nome, cargo) VALUES ('Ana Costa', 'ATENDENTE');

-- Inserindo sessões para filmes em cartaz
-- Sessão 1: Duna 2 - hoje às 14h
INSERT INTO SESSAO (filme_id, horario, status) VALUES
(1, DATEADD('HOUR', 2, CURRENT_TIMESTAMP), 'DISPONIVEL');

-- Criando assentos para a sessão 1 (100 assentos: A1-A10, B1-B10, ..., J1-J10)
INSERT INTO SESSAO_ASSENTO (sessao_id, assento_id, disponivel) VALUES
-- Fileira A
(1, 'A1', true), (1, 'A2', true), (1, 'A3', true), (1, 'A4', true), (1, 'A5', true),
(1, 'A6', true), (1, 'A7', true), (1, 'A8', true), (1, 'A9', true), (1, 'A10', true),
-- Fileira B
(1, 'B1', true), (1, 'B2', true), (1, 'B3', true), (1, 'B4', true), (1, 'B5', true),
(1, 'B6', true), (1, 'B7', true), (1, 'B8', true), (1, 'B9', true), (1, 'B10', true),
-- Fileira C
(1, 'C1', true), (1, 'C2', true), (1, 'C3', true), (1, 'C4', true), (1, 'C5', true),
(1, 'C6', true), (1, 'C7', true), (1, 'C8', true), (1, 'C9', true), (1, 'C10', true),
-- Fileira D
(1, 'D1', true), (1, 'D2', true), (1, 'D3', true), (1, 'D4', true), (1, 'D5', true),
(1, 'D6', true), (1, 'D7', true), (1, 'D8', true), (1, 'D9', true), (1, 'D10', true),
-- Fileira E
(1, 'E1', true), (1, 'E2', true), (1, 'E3', true), (1, 'E4', true), (1, 'E5', true),
(1, 'E6', true), (1, 'E7', true), (1, 'E8', true), (1, 'E9', true), (1, 'E10', true);

-- Sessão 2: Matrix - amanhã às 19h
INSERT INTO SESSAO (filme_id, horario, status) VALUES
(2, DATEADD('DAY', 1, DATEADD('HOUR', 19, CURRENT_DATE)), 'DISPONIVEL');

-- Criando assentos para a sessão 2 (mesma estrutura)
INSERT INTO SESSAO_ASSENTO (sessao_id, assento_id, disponivel)
SELECT 2, assento_id, disponivel FROM SESSAO_ASSENTO WHERE sessao_id = 1;

-- Sessão 3: Oppenheimer - amanhã às 15h
INSERT INTO SESSAO (filme_id, horario, status) VALUES
(4, DATEADD('DAY', 1, DATEADD('HOUR', 15, CURRENT_DATE)), 'DISPONIVEL');

-- Criando assentos para a sessão 3
INSERT INTO SESSAO_ASSENTO (sessao_id, assento_id, disponivel)
SELECT 3, assento_id, disponivel FROM SESSAO_ASSENTO WHERE sessao_id = 1;

-- Inserindo produtos da bomboniere (sem ID explícito para usar auto-incremento)
INSERT INTO PRODUTO (nome, preco, estoque) VALUES ('Pipoca Grande', 18.00, 50);
INSERT INTO PRODUTO (nome, preco, estoque) VALUES ('Pipoca Média', 14.00, 80);
INSERT INTO PRODUTO (nome, preco, estoque) VALUES ('Pipoca Pequena', 10.00, 100);
INSERT INTO PRODUTO (nome, preco, estoque) VALUES ('Refrigerante 500ml', 8.00, 120);
INSERT INTO PRODUTO (nome, preco, estoque) VALUES ('Refrigerante 1L', 12.00, 60);
INSERT INTO PRODUTO (nome, preco, estoque) VALUES ('Água Mineral', 5.00, 150);
INSERT INTO PRODUTO (nome, preco, estoque) VALUES ('Chocolate', 6.50, 80);
INSERT INTO PRODUTO (nome, preco, estoque) VALUES ('Nachos', 15.00, 40);
INSERT INTO PRODUTO (nome, preco, estoque) VALUES ('Hot Dog', 12.00, 35);
INSERT INTO PRODUTO (nome, preco, estoque) VALUES ('Combo Pipoca + Refri', 25.00, 45);

-- Inserindo usuários (sem ID explícito para usar auto-incremento)
INSERT INTO USUARIO (email, senha, nome, tipo) VALUES ('admin@astra.com', 'demo123', 'Administrador', 'ADMIN');
INSERT INTO USUARIO (email, senha, nome, tipo) VALUES ('gerente@astra.com', 'gerente123', 'Gerente Cinema', 'FUNCIONARIO');
INSERT INTO USUARIO (email, senha, nome, tipo) VALUES ('atendente@astra.com', 'atendente123', 'Atendente', 'FUNCIONARIO');
INSERT INTO USUARIO (email, senha, nome, tipo) VALUES ('tempzinxd@gmail.com', 'thiago123', 'Thiago', 'CLIENTE');

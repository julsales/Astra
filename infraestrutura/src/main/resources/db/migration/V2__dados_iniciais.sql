-- ===============================================
-- ASTRA CINEMA - DADOS INICIAIS
-- ===============================================

-- Inserindo filmes
INSERT INTO filme (titulo, sinopse, classificacao_etaria, duracao, status) VALUES 
('Duna 2', 'Paul Atreides se une aos Fremen em uma jornada de vingança contra aqueles que destruíram sua família.', '14 anos', 166, 'EM_CARTAZ'),
('Matrix', 'Um hacker descobre a verdade sobre sua realidade e seu papel na guerra contra seus controladores.', '14 anos', 136, 'EM_CARTAZ'),
('Avatar 3', 'Jake Sully e Neytiri enfrentam novos desafios em Pandora.', '12 anos', 195, 'RETIRADO'),
('Oppenheimer', 'A história de J. Robert Oppenheimer e seu papel no desenvolvimento da bomba atômica.', '14 anos', 180, 'EM_CARTAZ'),
('Barbie', 'Barbie e Ken vivem uma aventura no mundo real.', 'Livre', 114, 'EM_BREVE');

-- Inserindo funcionários
INSERT INTO funcionario (nome, cargo) VALUES 
('João Silva', 'GERENTE'),
('Maria Santos', 'ATENDENTE'),
('Pedro Oliveira', 'GERENTE'),
('Ana Costa', 'ATENDENTE');

-- Inserindo sessões para filmes em cartaz
INSERT INTO sessao (filme_id, horario, status, capacidade) VALUES
(1, NOW() + INTERVAL '2 hours', 'DISPONIVEL', 50),
(2, NOW() + INTERVAL '1 day 19 hours', 'DISPONIVEL', 50),
(4, NOW() + INTERVAL '1 day 15 hours', 'DISPONIVEL', 50);

-- Criando assentos para as sessões (50 assentos: A1-A10, B1-B10, ..., E1-E10)
DO $$
DECLARE
    sessao_id_var INTEGER;
    fileira CHAR;
    numero INTEGER;
BEGIN
    FOR sessao_id_var IN 1..3 LOOP
        FOR fileira IN SELECT unnest(ARRAY['A','B','C','D','E']) LOOP
            FOR numero IN 1..10 LOOP
                INSERT INTO sessao_assento (sessao_id, assento_id, disponivel)
                VALUES (sessao_id_var, fileira || numero, true);
            END LOOP;
        END LOOP;
    END LOOP;
END $$;

-- Inserindo produtos da bomboniere
INSERT INTO produto (nome, preco, estoque) VALUES 
('Pipoca Grande', 18.00, 50),
('Pipoca Média', 14.00, 80),
('Pipoca Pequena', 10.00, 100),
('Refrigerante 500ml', 8.00, 120),
('Refrigerante 1L', 12.00, 60),
('Água Mineral', 5.00, 150),
('Chocolate', 6.50, 80),
('Nachos', 15.00, 40),
('Hot Dog', 12.00, 35),
('Combo Pipoca + Refri', 25.00, 45);

-- Inserindo usuários (IMPORTANTE: senhas em texto plano apenas para demonstração!)
INSERT INTO usuario (email, senha, nome, tipo) VALUES 
('admin@astra.com', 'demo123', 'Administrador', 'ADMIN'),
('gerente@astra.com', 'gerente123', 'Gerente Cinema', 'FUNCIONARIO'),
('atendente@astra.com', 'atendente123', 'Atendente', 'FUNCIONARIO'),
('tempzinxd@gmail.com', 'thiago123', 'Thiago', 'CLIENTE');

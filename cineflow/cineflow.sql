-- CineFlow base de dados inicial
-- Observação: valores são ilustrativos. Ajuste conforme dados reais do cinema.

DROP TABLE IF EXISTS indicadores_diarios;
DROP TABLE IF EXISTS bomboniere_vendas;
DROP TABLE IF EXISTS ingressos;
DROP TABLE IF EXISTS sessoes;
DROP TABLE IF EXISTS salas;
DROP TABLE IF EXISTS filmes;

CREATE TABLE filmes (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(120) NOT NULL,
    duracao_min INTEGER NOT NULL,
    classificacao VARCHAR(8) NOT NULL,
    status VARCHAR(20) DEFAULT 'EM_CARTAZ'
);

CREATE TABLE salas (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(40) NOT NULL,
    capacidade INTEGER NOT NULL
);

CREATE TABLE sessoes (
    id SERIAL PRIMARY KEY,
    filme_id INTEGER NOT NULL REFERENCES filmes(id),
    sala_id INTEGER NOT NULL REFERENCES salas(id),
    horario TIMESTAMP NOT NULL,
    preco_inteira NUMERIC(10,2) NOT NULL,
    preco_meia NUMERIC(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'ABERTA'
);

CREATE TABLE ingressos (
    id SERIAL PRIMARY KEY,
    sessao_id INTEGER NOT NULL REFERENCES sessoes(id),
    canal_venda VARCHAR(20) NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    status VARCHAR(20) DEFAULT 'CONFIRMADO',
    valor_pago NUMERIC(10,2) NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bomboniere_vendas (
    id SERIAL PRIMARY KEY,
    sessao_id INTEGER REFERENCES sessoes(id),
    item VARCHAR(60) NOT NULL,
    quantidade INTEGER NOT NULL,
    valor_total NUMERIC(10,2) NOT NULL,
    canal_venda VARCHAR(20) DEFAULT 'BALCAO'
);

CREATE TABLE indicadores_diarios (
    data_referencia DATE NOT NULL,
    indicador VARCHAR(60) NOT NULL,
    valor NUMERIC(12,2) NOT NULL,
    meta NUMERIC(12,2),
    PRIMARY KEY (data_referencia, indicador)
);

-- Dados de exemplo (13 de janeiro, plausíveis para testes manuais)
INSERT INTO filmes (titulo, duracao_min, classificacao) VALUES
    ('Neblina Quântica', 128, '14'),
    ('Horizontes de Vênus', 101, '12'),
    ('Circuito Fantasma', 115, '16');

INSERT INTO salas (nome, capacidade) VALUES
    ('IMAX Azul', 180),
    ('Sala Atmos', 140),
    ('Sala Vip', 90);

INSERT INTO sessoes (filme_id, sala_id, horario, preco_inteira, preco_meia, status) VALUES
    (1, 1, '2025-01-13 18:30:00', 42.00, 21.00, 'LOTANDO'),
    (2, 2, '2025-01-13 20:00:00', 36.00, 18.00, 'ABERTA'),
    (3, 3, '2025-01-13 22:15:00', 45.00, 22.50, 'ABERTA');

INSERT INTO ingressos (sessao_id, canal_venda, tipo, status, valor_pago, criado_em) VALUES
    (1, 'APP', 'INTEIRA', 'CONFIRMADO', 42.00, '2025-01-13 10:15:00'),
    (1, 'APP', 'MEIA', 'CONFIRMADO', 21.00, '2025-01-13 10:16:00'),
    (1, 'BILHETERIA', 'INTEIRA', 'CONFIRMADO', 42.00, '2025-01-13 12:45:00'),
    (1, 'APP', 'MEIA', 'CONFIRMADO', 21.00, '2025-01-13 14:10:00'),
    (1, 'APP', 'INTEIRA', 'CANCELADO', 42.00, '2025-01-13 15:50:00'),
    (2, 'APP', 'INTEIRA', 'CONFIRMADO', 36.00, '2025-01-13 09:05:00'),
    (2, 'APP', 'MEIA', 'CONFIRMADO', 18.00, '2025-01-13 09:06:00'),
    (2, 'BILHETERIA', 'INTEIRA', 'CONFIRMADO', 36.00, '2025-01-13 17:40:00'),
    (2, 'APP', 'INTEIRA', 'CONFIRMADO', 36.00, '2025-01-13 19:10:00'),
    (3, 'APP', 'INTEIRA', 'CONFIRMADO', 45.00, '2025-01-13 11:40:00'),
    (3, 'APP', 'MEIA', 'CONFIRMADO', 22.50, '2025-01-13 11:42:00');

INSERT INTO bomboniere_vendas (sessao_id, item, quantidade, valor_total, canal_venda) VALUES
    (1, 'Combo Max (pipoca+refri)', 32, 1024.00, 'APP'),
    (1, 'Chocolate Nitro', 15, 337.50, 'BALCAO'),
    (2, 'Pipoca Média', 24, 360.00, 'BALCAO'),
    (2, 'Refri 700ml', 20, 240.00, 'BALCAO'),
    (3, 'Combo Doce', 18, 486.00, 'APP');

INSERT INTO indicadores_diarios (data_referencia, indicador, valor, meta) VALUES
    ('2025-01-13', 'Ingressos vendidos', 38, 45),
    ('2025-01-13', 'Taxa média de ocupação (%)', 71.5, 75),
    ('2025-01-13', 'Receita bilheteria (R$)', 1287.50, 1500),
    ('2025-01-13', 'Receita bomboniere (R$)', 2447.50, 2500);

-- View rápida para os analistas
CREATE OR REPLACE VIEW vw_resumo_financeiro AS
SELECT
    s.id AS sessao_id,
    f.titulo,
    sa.nome AS sala,
    s.horario,
    SUM(CASE WHEN i.status = 'CONFIRMADO' THEN i.valor_pago ELSE 0 END) AS receita_bilheteria,
    COALESCE(SUM(b.valor_total), 0) AS receita_bomboniere
FROM sessoes s
JOIN filmes f ON f.id = s.filme_id
JOIN salas sa ON sa.id = s.sala_id
LEFT JOIN ingressos i ON i.sessao_id = s.id
LEFT JOIN bomboniere_vendas b ON b.sessao_id = s.id
GROUP BY s.id, f.titulo, sa.nome, s.horario;

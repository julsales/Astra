-- Create SALA table
CREATE TABLE IF NOT EXISTS sala (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    capacidade INTEGER NOT NULL,
    tipo VARCHAR(50) NOT NULL
);

-- Insert default salas
INSERT INTO sala (id, nome, capacidade, tipo) VALUES
(1, 'Sala 1', 100, 'PADRAO'),
(2, 'Sala 2', 80, 'PADRAO'),
(3, 'Sala VIP', 50, 'VIP'),
(4, 'Sala IMAX', 150, 'IMAX')
ON CONFLICT (id) DO NOTHING;

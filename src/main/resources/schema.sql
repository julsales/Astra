-- ===============================================
-- ASTRA CINEMA - SCHEMA DO BANCO DE DADOS
-- ===============================================

-- Tabela FILME
CREATE TABLE IF NOT EXISTS FILME (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    sinopse VARCHAR(1000),
    classificacao_etaria VARCHAR(50),
    duracao INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL
);

-- Tabela SESSAO
CREATE TABLE IF NOT EXISTS SESSAO (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    filme_id INTEGER NOT NULL,
    horario TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (filme_id) REFERENCES FILME(id)
);

-- Tabela SESSAO_ASSENTO (ElementCollection)
CREATE TABLE IF NOT EXISTS SESSAO_ASSENTO (
    sessao_id INTEGER NOT NULL,
    assento_id VARCHAR(10) NOT NULL,
    disponivel BOOLEAN NOT NULL,
    PRIMARY KEY (sessao_id, assento_id),
    FOREIGN KEY (sessao_id) REFERENCES SESSAO(id) ON DELETE CASCADE
);

-- Tabela FUNCIONARIO
CREATE TABLE IF NOT EXISTS FUNCIONARIO (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cargo VARCHAR(20) NOT NULL
);

-- Tabela PRODUTO (Bomboniere)
CREATE TABLE IF NOT EXISTS PRODUTO (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    preco DOUBLE NOT NULL,
    estoque INTEGER NOT NULL
);

-- Índices para otimização
CREATE INDEX IF NOT EXISTS idx_sessao_filme ON SESSAO(filme_id);
CREATE INDEX IF NOT EXISTS idx_sessao_horario ON SESSAO(horario);
CREATE INDEX IF NOT EXISTS idx_filme_status ON FILME(status);

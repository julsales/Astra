-- Cria tabela de programação
CREATE TABLE IF NOT EXISTS PROGRAMACAO (
    id              SERIAL PRIMARY KEY,
    filme_id        INTEGER NOT NULL REFERENCES filme(id),
    data_inicio     TIMESTAMP WITH TIME ZONE NOT NULL,
    data_fim        TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Cria tabela de associação entre programação e sessões
CREATE TABLE IF NOT EXISTS PROGRAMACAO_SESSAO (
    programacao_id  INTEGER NOT NULL REFERENCES PROGRAMACAO(id) ON DELETE CASCADE,
    sessao_id       INTEGER NOT NULL,
    PRIMARY KEY (programacao_id, sessao_id)
);

-- Índices para melhorar performance
CREATE INDEX idx_programacao_filme_id ON PROGRAMACAO(filme_id);
CREATE INDEX idx_programacao_data ON PROGRAMACAO(data_inicio, data_fim);

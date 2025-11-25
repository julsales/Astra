-- Cria tabela de programação
CREATE TABLE IF NOT EXISTS programacao (
    id              SERIAL PRIMARY KEY,
    periodo_inicio  DATE NOT NULL,
    periodo_fim     DATE NOT NULL,
    criado_em       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Cria tabela de associação entre programação e sessões
CREATE TABLE IF NOT EXISTS programacao_sessao (
    programacao_id  INTEGER NOT NULL REFERENCES programacao(id) ON DELETE CASCADE,
    sessao_id       INTEGER NOT NULL,
    PRIMARY KEY (programacao_id, sessao_id)
);

-- Índices para melhorar performance
CREATE INDEX idx_programacao_periodo ON programacao(periodo_inicio, periodo_fim);
CREATE INDEX idx_programacao_sessao_sessao_id ON programacao_sessao(sessao_id);

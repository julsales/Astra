-- Cria tabela de validação de ingresso
CREATE TABLE IF NOT EXISTS VALIDACAO_INGRESSO (
    id                      SERIAL PRIMARY KEY,
    ingresso_id             INTEGER NOT NULL REFERENCES ingresso(id),
    funcionario_id          INTEGER NOT NULL REFERENCES funcionario(id),
    data_hora_validacao     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    sucesso                 BOOLEAN NOT NULL DEFAULT TRUE,
    mensagem                TEXT
);

-- Índices para melhorar consultas
CREATE INDEX idx_validacao_ingresso_id ON VALIDACAO_INGRESSO(ingresso_id);
CREATE INDEX idx_validacao_funcionario_id ON VALIDACAO_INGRESSO(funcionario_id);
CREATE INDEX idx_validacao_data_hora ON VALIDACAO_INGRESSO(data_hora_validacao DESC);

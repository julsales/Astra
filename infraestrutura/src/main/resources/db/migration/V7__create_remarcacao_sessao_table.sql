-- Cria tabela de remarcação de sessão
CREATE TABLE IF NOT EXISTS remarcacao_sessao (
    id                      SERIAL PRIMARY KEY,
    ingresso_id             INTEGER NOT NULL REFERENCES ingresso(id),
    sessao_original_id      INTEGER NOT NULL REFERENCES sessao(id),
    sessao_nova_id          INTEGER NOT NULL REFERENCES sessao(id),
    assento_original_id     VARCHAR(10) NOT NULL,
    assento_novo_id         VARCHAR(10) NOT NULL,
    funcionario_id          INTEGER NOT NULL REFERENCES funcionario(id),
    data_hora_remarcacao    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    motivo_tecnico          TEXT
);

-- Índices para melhorar consultas
CREATE INDEX idx_remarcacao_ingresso_id ON remarcacao_sessao(ingresso_id);
CREATE INDEX idx_remarcacao_funcionario_id ON remarcacao_sessao(funcionario_id);
CREATE INDEX idx_remarcacao_data_hora ON remarcacao_sessao(data_hora_remarcacao DESC);

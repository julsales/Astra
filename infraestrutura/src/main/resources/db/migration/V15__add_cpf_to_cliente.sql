-- Adiciona campo CPF na tabela cliente com constraint de unicidade
ALTER TABLE cliente ADD COLUMN cpf VARCHAR(14);

-- Cria índice único para garantir que não haja CPFs duplicados
CREATE UNIQUE INDEX idx_cliente_cpf_unique ON cliente(cpf) WHERE cpf IS NOT NULL;

-- Adiciona comentário explicativo
COMMENT ON COLUMN cliente.cpf IS 'CPF do cliente (formato: XXX.XXX.XXX-XX). Único por cliente.';

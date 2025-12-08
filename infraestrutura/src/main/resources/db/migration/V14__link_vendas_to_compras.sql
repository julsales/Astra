-- Adiciona coluna compra_id na tabela venda para vincular vendas de bomboniere às compras
ALTER TABLE venda ADD COLUMN IF NOT EXISTS compra_id INTEGER;

-- Adiciona constraint de chave estrangeira
ALTER TABLE venda ADD CONSTRAINT fk_venda_compra FOREIGN KEY (compra_id) REFERENCES compra(id) ON DELETE CASCADE;

-- Cria índice para melhor performance
CREATE INDEX IF NOT EXISTS idx_venda_compra_id ON venda(compra_id);

COMMENT ON COLUMN venda.compra_id IS 'Referência à compra (quando o produto foi vendido junto com ingressos)';

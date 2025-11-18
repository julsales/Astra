-- Adiciona coluna qr_code na tabela ingresso para armazenar o código QR gerado pelo backend
ALTER TABLE ingresso ADD COLUMN IF NOT EXISTS qr_code VARCHAR(100) UNIQUE;

-- Cria índice para busca rápida por QR Code
CREATE INDEX IF NOT EXISTS idx_ingresso_qr_code ON ingresso(qr_code);


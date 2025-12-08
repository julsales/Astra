-- Remove a coluna capacidade da tabela sessao
-- A capacidade agora vem da tabela sala (relacionamento via sala_id)
ALTER TABLE sessao DROP COLUMN IF EXISTS capacidade;

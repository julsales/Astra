-- ===============================================
-- ASTRA CINEMA - DADOS INICIAIS DE FUNCIONÁRIOS
-- ===============================================
-- Esta migração adiciona os funcionários correspondentes aos usuários
-- criados na V2, corrigindo a referência para validacao_ingresso

-- Inserindo funcionários correspondentes aos usuários do tipo FUNCIONARIO
INSERT INTO funcionario (nome, cargo) VALUES
('Gerente Cinema', 'GERENTE'),
('Atendente', 'ATENDENTE');

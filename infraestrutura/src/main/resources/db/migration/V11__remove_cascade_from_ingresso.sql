-- Remove o ON DELETE CASCADE da foreign key de ingresso para compra
-- Isso previne que ingressos sejam deletados automaticamente quando uma compra é deletada

-- 1. Primeiro, remove a constraint existente
ALTER TABLE ingresso DROP CONSTRAINT IF EXISTS ingresso_compra_id_fkey;

-- 2. Recria a constraint SEM o ON DELETE CASCADE
ALTER TABLE ingresso 
    ADD CONSTRAINT ingresso_compra_id_fkey 
    FOREIGN KEY (compra_id) 
    REFERENCES compra(id);

-- Agora os ingressos não serão mais deletados automaticamente quando uma compra for deletada
-- Será necessário deletar os ingressos manualmente antes de deletar uma compra

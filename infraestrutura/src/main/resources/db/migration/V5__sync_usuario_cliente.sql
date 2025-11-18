-- Migration V5: Sincronizar tabelas usuario e cliente
-- Garante que usuários do tipo CLIENTE tenham registros correspondentes na tabela cliente

-- 1. Criar registros de clientes para todos os usuários do tipo CLIENTE existentes
INSERT INTO cliente (id, nome, email, criado_em)
SELECT id, nome, email, NOW()
FROM usuario
WHERE tipo = 'CLIENTE'
ON CONFLICT (id) DO UPDATE
SET nome = EXCLUDED.nome,
    email = EXCLUDED.email;

-- 2. Criar função que sincroniza automaticamente usuario -> cliente
CREATE OR REPLACE FUNCTION sync_usuario_to_cliente()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.tipo = 'CLIENTE' THEN
        INSERT INTO cliente (id, nome, email, criado_em)
        VALUES (NEW.id, NEW.nome, NEW.email, NOW())
        ON CONFLICT (id) DO UPDATE
        SET nome = NEW.nome,
            email = NEW.email;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 3. Criar trigger que executa a função após INSERT ou UPDATE na tabela usuario
DROP TRIGGER IF EXISTS trigger_sync_usuario_to_cliente ON usuario;
CREATE TRIGGER trigger_sync_usuario_to_cliente
AFTER INSERT OR UPDATE ON usuario
FOR EACH ROW
EXECUTE FUNCTION sync_usuario_to_cliente();

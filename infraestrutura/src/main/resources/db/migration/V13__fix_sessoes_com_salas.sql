-- ===============================================
-- CORRIGE SESSÕES PARA USAR CAPACIDADE DAS SALAS
-- ===============================================

-- Remove todas as sessões antigas (criadas sem sala_id na V2)
-- IMPORTANTE: Deletar na ordem correta para respeitar constraints de FK
DELETE FROM ingresso;
DELETE FROM compra;
DELETE FROM sessao_assento;
DELETE FROM sessao;

-- Agora cria sessões CORRETAMENTE com sala_id
-- Sala 1 (100 lugares), Sala 2 (80 lugares), Sala IMAX (150 lugares)

INSERT INTO sessao (filme_id, horario, status, sala_id) VALUES
-- Duna 2 (filme_id = 1) - 3 sessões
(1, NOW() + INTERVAL '1 day 10 hours', 'DISPONIVEL', 1),  -- Sala 1 (100 lugares)
(1, NOW() + INTERVAL '1 day 14 hours', 'DISPONIVEL', 1),  -- Sala 1 (100 lugares)
(1, NOW() + INTERVAL '1 day 19 hours', 'DISPONIVEL', 1),  -- Sala 1 (100 lugares)

-- Matrix (filme_id = 2) - 3 sessões
(2, NOW() + INTERVAL '1 day 11 hours', 'DISPONIVEL', 1),  -- Sala 1 (100 lugares)
(2, NOW() + INTERVAL '1 day 16 hours', 'DISPONIVEL', 1),  -- Sala 1 (100 lugares)
(2, NOW() + INTERVAL '1 day 21 hours', 'DISPONIVEL', 1),  -- Sala 1 (100 lugares)

-- Oppenheimer (filme_id = 3) - 2 sessões
(3, NOW() + INTERVAL '1 day 13 hours', 'DISPONIVEL', 1),  -- Sala 1 (100 lugares)
(3, NOW() + INTERVAL '1 day 18 hours', 'DISPONIVEL', 1);  -- Sala 1 (100 lugares)

-- Criar assentos para cada sessão baseado na capacidade da SALA
DO $$
DECLARE
    sessao_record RECORD;
    sala_capacidade INTEGER;
    fileira CHAR;
    numero INTEGER;
    assentos_por_fileira INTEGER := 10;
    num_fileiras INTEGER;
    assento_count INTEGER;
BEGIN
    -- Para cada sessão criada
    FOR sessao_record IN SELECT id, sala_id FROM sessao LOOP
        -- Busca a capacidade da sala
        SELECT capacidade INTO sala_capacidade FROM sala WHERE id = sessao_record.sala_id;

        -- Calcula número de fileiras necessárias
        num_fileiras := CEIL(sala_capacidade::FLOAT / assentos_por_fileira);

        -- Cria assentos
        assento_count := 0;
        FOR i IN 0..(num_fileiras - 1) LOOP
            fileira := CHR(65 + i); -- A=65, B=66, etc.

            FOR numero IN 1..assentos_por_fileira LOOP
                EXIT WHEN assento_count >= sala_capacidade;

                INSERT INTO sessao_assento (sessao_id, assento_id, disponivel)
                VALUES (sessao_record.id, fileira || numero, true);

                assento_count := assento_count + 1;
            END LOOP;

            EXIT WHEN assento_count >= sala_capacidade;
        END LOOP;
    END LOOP;
END $$;

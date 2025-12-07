-- Add sala_id column to sessao table
-- Step 1: Add column as nullable
ALTER TABLE sessao ADD COLUMN IF NOT EXISTS sala_id INTEGER;

-- Step 2: Update existing records with default value (sala with id=1 must exist from V09_5)
UPDATE sessao SET sala_id = 1 WHERE sala_id IS NULL;

-- Step 3: Make column NOT NULL
ALTER TABLE sessao ALTER COLUMN sala_id SET NOT NULL;

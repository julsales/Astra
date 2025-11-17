-- Adiciona campo de imagem aos filmes e popula exemplos iniciais
ALTER TABLE filme ADD COLUMN IF NOT EXISTS imagem_url VARCHAR(1000);

-- Atualiza registros existentes com imagens de referência (placeholders livres de copyright)
UPDATE filme SET imagem_url = 'https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=900&q=80'
WHERE titulo = 'Duna 2' AND (imagem_url IS NULL OR imagem_url = '');

UPDATE filme SET imagem_url = 'https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=900&q=80'
WHERE titulo = 'Matrix' AND (imagem_url IS NULL OR imagem_url = '');

UPDATE filme SET imagem_url = 'https://images.unsplash.com/photo-1502139214982-d0ad755818d8?auto=format&fit=crop&w=900&q=80'
WHERE titulo = 'Avatar 3' AND (imagem_url IS NULL OR imagem_url = '');

UPDATE filme SET imagem_url = 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=900&q=80'
WHERE titulo = 'Oppenheimer' AND (imagem_url IS NULL OR imagem_url = '');

UPDATE filme SET imagem_url = 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=900&q=80'
WHERE titulo = 'Barbie' AND (imagem_url IS NULL OR imagem_url = '');

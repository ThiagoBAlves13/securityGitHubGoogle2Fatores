-- Drop the existing autor column
ALTER TABLE respostas DROP COLUMN autor;

-- Add the new autor_id column
ALTER TABLE respostas ADD COLUMN autor_id BIGINT NOT NULL DEFAULT 1;

-- Add the foreign key constraint
ALTER TABLE respostas ADD CONSTRAINT fk_autor_resposta FOREIGN KEY (autor_id) REFERENCES usuarios(id) ON DELETE CASCADE;

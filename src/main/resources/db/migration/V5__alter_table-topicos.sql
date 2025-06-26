-- Drop the existing autor column
ALTER TABLE topicos DROP COLUMN autor;

-- Add the new autor_id column
ALTER TABLE topicos ADD COLUMN autor_id BIGINT NOT NULL DEFAULT 1;

-- Add the foreign key constraint
ALTER TABLE topicos ADD CONSTRAINT fk_autor_topico FOREIGN KEY (autor_id) REFERENCES usuarios(id) ON DELETE CASCADE;

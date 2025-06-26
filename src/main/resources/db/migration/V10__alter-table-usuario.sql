-- Add the verificado column
ALTER TABLE usuarios
ADD COLUMN secret VARCHAR(64);

-- Add the token column
ALTER TABLE usuarios
ADD COLUMN a2f_ativa BOOLEAN NOT NULL DEFAULT 0;
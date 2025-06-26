-- Add the verificado column
ALTER TABLE usuarios ADD COLUMN verificado BOOLEAN NOT NULL DEFAULT TRUE;

-- Add the token column
ALTER TABLE usuarios ADD COLUMN token VARCHAR(64);

-- Add the expiracao_token column
ALTER TABLE usuarios ADD COLUMN expiracao_token TIMESTAMP;

CREATE TABLE IF NOT EXISTS accounts (
    id VARCHAR(40) PRIMARY KEY,
    balance NUMERIC(15, 2) NOT NULL CHECK (balance >= 0),
    version BIGINT NOT NULL,
    CHECK (id ~ '^[a-z0-9_-]+$')
);
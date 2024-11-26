CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY,
    amount NUMERIC(15,2) NOT NULL CHECK (amount > 0),
    from_acc VARCHAR(40) REFERENCES accounts(id) ON DELETE RESTRICT NOT NULL,
    to_acc VARCHAR(40) REFERENCES accounts(id) ON DELETE RESTRICT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_transaction_created_at ON transactions (created_at);
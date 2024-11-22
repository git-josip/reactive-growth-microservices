INSERT INTO accounts (id, balance, version) VALUES
('validacc1', 100000::NUMERIC, 0),
('validacc2', 200000::NUMERIC, 0),
('validacc3', 300000::NUMERIC, 0),
('validacc4', 400000::NUMERIC, 0),
('validacc5', 500000::NUMERIC, 0)
ON CONFLICT DO NOTHING;
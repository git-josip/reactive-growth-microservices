INSERT INTO transactions (id, from_acc, to_acc, amount, created_at) VALUES
('01930c90-dba6-7162-a689-ed96d038ef75', 'validacc1', 'validacc2', 10000::NUMERIC, CURRENT_TIMESTAMP),
('c7987aa1-f7bc-46e1-ad19-01d7c91eaa8d', 'validacc3', 'validacc4', 20000::NUMERIC, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;
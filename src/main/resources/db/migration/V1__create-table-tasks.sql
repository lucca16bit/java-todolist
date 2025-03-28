CREATE TABLE tb_tasks (
    id BINARY(16) PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    description TEXT,
    start_at TIMESTAMP,
    end_at TIMESTAMP,
    priority VARCHAR(10) CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH'))
);
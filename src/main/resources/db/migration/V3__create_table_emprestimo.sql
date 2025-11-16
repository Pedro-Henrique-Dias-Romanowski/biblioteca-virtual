CREATE TABLE emprestimo (
                            id BIGSERIAL PRIMARY KEY,
                            cliente_id BIGINT NOT NULL,
                            data_emprestimo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            data_devolucao TIMESTAMP,
                            ativo BOOLEAN DEFAULT FALSE,
                            FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);
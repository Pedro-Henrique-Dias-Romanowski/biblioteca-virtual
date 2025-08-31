CREATE TABLE emprestimo (
                            id SERIAL PRIMARY KEY,
                            cliente_id INT NOT NULL,
                            livro_id INT NOT NULL,
                            data_emprestimo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            data_devolucao TIMESTAMP,
                            devolvido BOOLEAN DEFAULT FALSE,
                            FOREIGN KEY (cliente_id) REFERENCES cliente(id),
                            FOREIGN KEY (livro_id) REFERENCES livro(id)
);
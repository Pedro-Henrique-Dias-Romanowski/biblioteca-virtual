CREATE TABLE livro_emprestimo (
                                  emprestimo_id BIGINT,
                                  livro_id BIGINT,
                                  PRIMARY KEY (emprestimo_id, livro_id),
                                  FOREIGN KEY (emprestimo_id) REFERENCES emprestimo(id),
                                  FOREIGN KEY (livro_id) REFERENCES livro(id)
);
CREATE TABLE livro_emprestimo (
                                  emprestimo_id INT,
                                  livro_id INT,
                                  PRIMARY KEY (emprestimo_id, livro_id),
                                  FOREIGN KEY (emprestimo_id) REFERENCES emprestimo(id),
                                  FOREIGN KEY (livro_id) REFERENCES livro(id)
);
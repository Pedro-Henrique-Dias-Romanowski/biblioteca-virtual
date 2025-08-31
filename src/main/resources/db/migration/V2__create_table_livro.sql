CREATE TABLE livro (
                       id SERIAL PRIMARY KEY,
                       titulo VARCHAR(200) NOT NULL,
                       autor VARCHAR(150) NOT NULL,
                       ano_publicacao INT,
                       disponivel BOOLEAN DEFAULT TRUE
);
CREATE TABLE administrador (
                               id SERIAL PRIMARY KEY,
                               nome VARCHAR(100) NOT NULL,
                               email VARCHAR(150) UNIQUE NOT NULL,
                               senha VARCHAR(255) NOT NULL
);
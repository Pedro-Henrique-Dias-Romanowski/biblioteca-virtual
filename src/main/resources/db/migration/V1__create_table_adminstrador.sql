CREATE TABLE administrador (
                               id BIGSERIAL PRIMARY KEY,
                               nome VARCHAR(100) NOT NULL,
                               email VARCHAR(150) UNIQUE NOT NULL,
                               senha VARCHAR(255) NOT NULL,
                               perfil VARCHAR(30) NOT NULL
);
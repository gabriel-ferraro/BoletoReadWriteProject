drop table if exists clientes;

CREATE TABLE IF NOT EXISTS clientes(
    Nome VARCHAR(50),
    Valor DECIMAL(10,2),
    CPF CHAR(14) UNIQUE PRIMARY KEY,
    Matrícula INTEGER,
    Inclusão DATE
);

--select * from "myDB".public.clientes;

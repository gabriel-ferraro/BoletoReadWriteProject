import fs from "fs";
import readline from "readline";
import pkg from 'pg';
const { Pool } = pkg;

// Cria a conexão com o banco de dados
const pool = new Pool({
    user: "admin",
    database: "postgresDBReader",
    port: 5433,
    host: "localhost",
    password: "123456",
});

// Abre o arquivo para leitura
const rl = readline.createInterface({
    input: fs.createReadStream('../hotFolder/remessa.cnab240')
});

// Define a função para processar cada linha
const processLine = async (line) => {
    const registro = line.substring(7, 8);

    if (registro === '0') {
        // Registro Header
        const codigoBanco = line.substring(0, 3);
        const tipoRegistro = line.substring(7, 8);
        const numeroRemessa = line.substring(8, 13);
        const dataGeracao = line.substring(143, 151);
        const horaGeracao = line.substring(151, 157);
        const densidadeGravacao = line.substring(157, 160);
        const reservadoBanco = line.substring(160, 392);
        const reservadoEmpresa = line.substring(392);

        // Insere os dados no banco de dados
        await pool.query(
            `INSERT INTO remessa_header 
            (codigo_banco, tipo_registro, numero_remessa, data_geracao, hora_geracao, densidade_gravacao, reservado_banco, reservado_empresa)
            VALUES ($1, $2, $3, $4, $5, $6, $7, $8)`,
            [codigoBanco, tipoRegistro, numeroRemessa, dataGeracao, horaGeracao, densidadeGravacao, reservadoBanco, reservadoEmpresa]
        );
    } else if (registro === '1') {
        // Registro Detalhe
        const codigoBanco = line.substring(0, 3);
        const tipoRegistro = line.substring(7, 8);
        const numeroDocumento = line.substring(38, 62);
        const valor = line.substring(77, 92);
        const vencimento = line.substring(146, 154);

        // Insere os dados no banco de dados
        await pool.query(
            `INSERT INTO remessa_detalhe (codigo_banco, tipo_registro, numero_documento, valor, vencimento) VALUES ($1, $2, $3, $4, $5)`,
            [codigoBanco, tipoRegistro, numeroDocumento, valor, vencimento]
        );
    } else if (registro === '9') {
        // Registro Trailer
        const codigoBanco = line.substring(0, 3);
        const tipoRegistro = line.substring(7, 8);
        const quantidadeRegistros = line.substring(17, 25);
        const valorTotal = line.substring(253, 266);

        // Insere os dados no banco de dados
        await pool.query(
            `INSERT INTO remessa_trailer (codigo_banco, tipo_registro, quantidade_registros, valor_total) VALUES ($1, $2, $3, $4)`,
            [codigoBanco, tipoRegistro, quantidadeRegistros, valorTotal]
        );
    }
};

// Lê cada linha do arquivo e processa
rl.on('line', (line) => {
    processLine(line);
});
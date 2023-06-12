import * as pg from 'pg'
const { Client } = pg;
import { connect } from 'amqplib';
import fs from 'fs';
import path from 'path';

async function main() {
    try {
        const connection = await connect("amqp://localhost");
        const channel = await connection.createChannel();
        const queue = "generate_remittance_queue";
        await channel.assertQueue(queue, { durable: false });

        console.log("Aguardando mensagens...");
        
        channel.consume(queue, msg => {
            if (msg.content) {
                let remittanceId = msg.content.toString()
                console.log("Mensagem recebida: ", remittanceId);
                remittanceId = remittanceId.split(" ").pop();
                console.log("merda: " + remittanceId);
                // Chama métodos para fazer a baixa da remessa.
                updateRemmitanceState(remittanceId);
                removeCnabFromHotFolder(remittanceId);
            }
        }, { noAck: true });

    } catch (error) {
        console.error('Erro:', error);
    }
}

async function updateRemmitanceState(id) {
    try {
        const client = new pg.Client({
            user: "admin",
            host: "localhost",
            database: "postgres",
            password: "123456",
            port: 5432
        });

        await client.connect();

        const query = 'UPDATE remittance SET isCompensated = TRUE WHERE id = $1';
        const values = [id];
        // Faz alteração no registro.
        await client.query(query, values);
        await client.end();
    } catch (error) {
        console.error('Erro:', error);
    }
}

async function removeCnabFromHotFolder(remittanceId) {
    const id = Number(remittanceId);
    //hotFolder path.
    const HOTFOLDER_PATH = "../hotFolder/";
    try {
        const files = fs.readdirSync(HOTFOLDER_PATH);
        const filename = `remessa(${id}).cnab240`;
        const filePath = path.join(HOTFOLDER_PATH, filename);

        if (files.includes(filename)) {
            fs.unlinkSync(filePath);
            console.log(`Arquivo ${filename} removido com sucesso.`);
        } else {
            console.log(`Arquivo ${filename} não encontrado.`);
        }

    } catch (error) {
        console.error('Erro:', error);
    }
}

// Chamar a função para iniciar o subscriber
main();

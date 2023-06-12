const pg = require('pg');
const { Pool } = pg;
const { connect } = require('amqplib');
const fs = require('fs');
const path = require('path');

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
        const client = new Pool({
            user: "admin",
            host: "localhost",
            database: "postgres",
            password: "123456",
            port: 5432
        });

        await client.connect();

        const query = 'UPDATE remittance SET is_compensated = TRUE WHERE id = $1';
        const values = [id];
        // Faz alteração no registro.
        await client.query(query, values);
        
        console.log("Remessa de indice " + id + "compensada\n");
        
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

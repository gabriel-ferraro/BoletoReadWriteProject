import { connect } from 'amqplib';
import readline from 'readline';

function questionPromise(rl, question) {
    return new Promise((resolve) => {
        rl.question(question, (answer) => {
            resolve(answer);
        });
    });
}

/**
 * Valida se números do input são válidos para processamento.
 * @param {*} values Inputs das remessas que devem ser processadas.
 */
function validateNumbers(values) {
    isNumber(values);
    isInitialLesserThanFinal(values);
}

function isNumber(values) {
    for (const valor of Object.values(values)) {
        if (isNaN(valor)) {
            throw new Error('Os valores fornecidos não são numéricos.');
        }
    }
}

function isInitialLesserThanFinal(values) {
    let { initial, final } = values;
    if (final < initial) {
        throw new Error('O valor inicial deve ser maior que o final.');
    }
}

/**
 * Processa o input de número de e número até enviados pelo usuário.
 * @returns O input para número de remessas que devem ser processadas.
 */
async function recieveInput() {
    const rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout
    });

    try {
        const initial = await questionPromise(rl, 'Digite o número inicial da remessa: ');
        const final = await questionPromise(rl, 'Digite o número final da remessa: ');
        const valores = { initial: parseInt(initial), final: parseInt(final) };
        validateNumbers(valores);
        return valores;
    } finally {
        rl.close();
    }
}

/**
 * Processa as remessas no canal especificado.
 * @param {*} channel Canal de comunicação do RabbitMQ.
 * @param {*} queue Nome da fila utilizada entre o publisher e o subscriber.
 * @param {*} initialValue Número inicial da remessa.
 * @param {*} finalValue - O número final da remessa.
 */
async function processRemittance(channel, queue, initialValue, finalValue) {
    // Valor da mensagem deve ser String.
    const message = String(initialValue + ',' + finalValue);
    // Abre a fila utilizada entre pub e sub.
    await channel.assertQueue(queue, { durable: false });
    // Envia requsição para subscriber.
    channel.sendToQueue(queue, Buffer.from(message));
}

async function main() {
    const connection = await connect("amqp://localhost");
    const channel = await connection.createChannel();
    // Nome da fila utilizado entre pub e sub.
    const queue = "remessas_message";
    // Recebe o input dos valores inicial e final.
    const { initial, final } = await recieveInput();
    // Envia requisição para aplicação subscriber processar as remessa desde o valor inicial até final.
    await processRemittance(channel, queue, initial, final);
    console.log(`Pedido para processar remessa ${initial} até ${final}.`);
    // Fecha conexão.
    await channel.close();
    await connection.close();
}

main();

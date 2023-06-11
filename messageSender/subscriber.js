import { connect } from 'amqplib';

const connection = await connect("amqp://localhost");

const channel = await connection.createChannel();

const queue = "remessas_message";

await channel.assertQueue(queue, { durable: false });

/**
 * Consumer
 */
channel.consume(queue, msg => {
    console.log(`Processar remessa: ${msg.content.toString()}`);
});
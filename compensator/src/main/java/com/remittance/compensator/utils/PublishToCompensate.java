package com.remittance.compensator.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

class PublishToCompensate {

    private static final String HOST = "localhost";
    private static final int RABBIT_PORT = 32790;
    // Fila utilizada entre aplicação que processa remessas (B) e que faz a baixa das remessas (C).
    private static final String QUEUE_NAME = "process_remittance_queue";

    public static void compensate(Integer remittanceId) throws IOException, TimeoutException {
        // Faz conexao com rabbitMQ.
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(RABBIT_PORT);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // Declara fila entre pub e sub.
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // Mensagem a ser enviada para o subscriber.
        String message = "Fazer baixa da remessa com ID: " + remittanceId;
        channel.basicPublish("",
                QUEUE_NAME, null,
                message.getBytes("UTF-8"));

        System.out.println("Mensagem enviada para aplicacao C: \t" + message);
    }
}

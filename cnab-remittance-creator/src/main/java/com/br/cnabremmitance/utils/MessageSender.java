package com.br.cnabremmitance.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {

    // Dados para conexao rabbitMQ
    private static final String HOST = "localhost";
    private static final int RABBIT_PORT = 5672;
    // Fila utilizada entre aplicação geradora de remessas (publisher) e aplicação que compensa remessas (subscriber).
    private static final String QUEUE_NAME = "generate_remittance_queue";

    /**
     * Notifica subscriber do processamento da remessa.
     *
     * @param remittanceId Numero de identificacao da remessa.
     * @param queueName Nome da fila usada entre pub e sub.
     * @throws java.io.IOException
     * @throws java.util.concurrent.TimeoutException
     */
    public void requestToCompensateRemittance(Integer remittanceId, String queueName) throws IOException, TimeoutException {
        // Determinando nome da fila
        String queue = queueName == null ? QUEUE_NAME : queueName;
        // Faz conexao com rabbitMQ.
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(RABBIT_PORT);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // Declara fila entre pub e sub.
        channel.queueDeclare(queue, false, false, false, null);
        // Mensagem a ser enviada para o subscriber.
        String message = "Compensar remessa: " + remittanceId;
        channel.basicPublish("",
                QUEUE_NAME, null,
                message.getBytes("UTF-8"));

        System.out.println("Mensagem enviada para aplicacao B: \t" + message);
    }
}

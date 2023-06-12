package com.remittance.compensator.utils;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.nio.charset.StandardCharsets;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.remittance.compensator.models.Remittance;
import com.remittance.compensator.repositories.RemittanceRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class Subscriber {

    // Dados para conexao rabbitMQ
    private static final String HOST = "localhost";
    private static final int RABBIT_PORT = 5672;
    // Fila utilizada entre aplicação geradora de remessas (publisher) e aplicação que compensa remessas (subscriber).
    private static final String QUEUE_NAME = "generate_remittance_queue";
    // Repository para checar dados da remessa.
    private static RemittanceRepository remittanceRepository;

    @Autowired
    public Subscriber(RemittanceRepository remittanceRepository) {
        this.remittanceRepository = remittanceRepository;
    }

    public static void initializeChannel() throws IOException, TimeoutException, InterruptedException {
        // Cria uma conexão com o servidor RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(RABBIT_PORT);
        Connection connection = factory.newConnection();
        // Cria um canal para se comunicar com o servidor RabbitMQ
        Channel channel = connection.createChannel();
        // Declara a fila a ser usada pelo subscriber
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // Cria um objeto DefaultConsumer para consumir as mensagens
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                // Processa a mensagem recebida
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println("Mensagem recebida: " + message);

                //Faz split para obter ID da remessa que sera compensada.
                String[] processMessage = message.split(" ");
                String remittanceId = processMessage[processMessage.length - 1];

                try {
                    // Realiza a compensacao da remessa
                    compensateRemittance(Integer.valueOf(remittanceId));
                } catch (TimeoutException ex) {
                    Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, "Excecao em initializeChannel", ex);
                }
            }
        };

        // Inicia o consumo das mensagens da fila
        channel.basicConsume(QUEUE_NAME, true, consumer);

        // Mantém o subscriber em execução indefinidamente, aguardando novas mensagens
        System.out.println("Aguardando mensagens...");
        while (true) {
            Thread.sleep(1000);
        }
    }

    public static void compensateRemittance(Integer remittanceId) throws IOException, TimeoutException {
        // Adquire a remessa do BD.
        Remittance remittance = remittanceRepository.findById(remittanceId)
                .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Remittance not found"));
        // Se a data da remessa enviada nao esta vencida (antes de 18/06/2019).
        if (remittance.getEmissionDate().isBefore(LocalDate.of(2019, 6, 18))) {
            //Envia mensagem para aplicacao C fazer a baixa da remssa.
            PublishToCompensate.compensate(remittanceId);
        } else {
            // Logando que a mensagem nao pode ser compensada fora da data
            System.out.println(
                    "Remessa vencida! nao pode ser compensada - ID: "
                    + remittance.getId()
                    + " - Emitida em: "
                    + remittance.getEmissionDate()
            );
        }
    }

}

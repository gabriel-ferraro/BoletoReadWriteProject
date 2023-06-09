package XLSMtoPostgresDB;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class App {

    private static final String QUEUE_NAME = "remessa_queue";
    
    public static void main(String[] args) throws IOException, TimeoutException {
        System.out.println("Iniciando");

        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "admin";
        String password = "123456";

        String filePath = "../../Clientes.xlsm"; //caminho absoluto no meu PC: "C:\\boletoReadWrite\\Clientes.xlsm"
        String tableName = "remessas";
        
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            // Configurar o consumidor para receber mensagens do publisher
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("Mensagem recebida: " + message);

                    // Recebe valores e processa dados da planilha para BD.
                    try {
                        int initialValue = Integer.parseInt(message.split(",")[0]);
                        int finalValue = Integer.parseInt(message.split(",")[1]);

                        XLSMtoPostgres converter = new XLSMtoPostgres(url, user, password);
                        System.out.println("Processando remessa " + initialValue + " até " + finalValue);
                        converter.convert(filePath, tableName, initialValue, finalValue);
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }
                }
            };

            // Iniciar o consumo das mensagens
            channel.basicConsume(QUEUE_NAME, true, consumer);

            System.out.println("Aguardando mensagens. Pressione Ctrl+C para encerrar.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Finalizando");
        }
    }
}
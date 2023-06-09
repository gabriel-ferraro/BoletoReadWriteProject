package XLSMtoPostgresDB;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class MessagePublisher {

    private static final String QUEUE_NAME = "remessas_message";

    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setUsername("guest");
            factory.setPassword("guest");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            InputValues inputValues = receiveInput();
            validateNumbers(inputValues);
            processRemittance(channel, QUEUE_NAME, inputValues.getInitial(), inputValues.getFinal());

            System.out.println("Pedido para processar remessa " + inputValues.getInitial() + " ate " + inputValues.getFinal());
            
            channel.close();
            connection.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private static InputValues receiveInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Digite o numero inicial da remessa: ");
        int initial = Integer.parseInt(reader.readLine());
        System.out.print("Digite o numero final da remessa: ");
        int finalValue = Integer.parseInt(reader.readLine());
        return new InputValues(initial, finalValue);
    }

    private static void validateNumbers(InputValues values) {
        isNumber(values.getInitial());
        isNumber(values.getFinal());
        isInitialLesserThanFinal(values);
    }

    private static void isNumber(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Os valores fornecidos não sao numericos.");
        }
    }

    private static void isInitialLesserThanFinal(InputValues values) {
        int initial = values.getInitial();
        int finalValue = values.getFinal();
        if (finalValue < initial) {
            throw new IllegalArgumentException("O valor inicial deve ser maior que o final.");
        }
    }

    private static void processRemittance(Channel channel, String queue, int initialValue, int finalValue) throws IOException {
        for (int i = initialValue; i <= finalValue; i++) {
            String message = String.valueOf(i);
            channel.queueDeclare(queue, false, false, false, null);
            channel.basicPublish("", queue, null, message.getBytes());
        }
    }

    private static class InputValues {
        private int initial;
        private int finalValue;

        public InputValues(int initial, int finalValue) {
            this.initial = initial;
            this.finalValue = finalValue;
        }

        public int getInitial() {
            return initial;
        }

        public int getFinal() {
            return finalValue;
        }
    }
}

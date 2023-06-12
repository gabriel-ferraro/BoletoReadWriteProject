package com.remittance.compensator;

import com.remittance.compensator.utils.Subscriber;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CompensatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(CompensatorApplication.class, args);

        // Inicializa fila para comunicacao entre api A e B.
        try {
            Subscriber.initializeChannel();
        } catch (IOException | TimeoutException | InterruptedException ex) {
            Logger.getLogger(CompensatorApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

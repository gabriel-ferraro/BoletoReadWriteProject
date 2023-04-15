package boletoWriterJAVA;

import java.io.IOException;

public class App {

    public static void main(String[] args) {
        System.out.println("Iniciando");
        try{
            RemessaGenerator.generateRemessa();
        } catch(IOException e){
            System.out.println("Error: " + e);
        } finally {
            System.out.println("Finalizando");
        }
        
    }
}

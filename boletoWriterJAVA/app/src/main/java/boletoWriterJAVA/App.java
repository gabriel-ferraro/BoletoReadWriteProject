package boletoWriterJAVA;

import java.io.IOException;

public class App {

    public static void main(String[] args) {
        System.out.println("Iniciando");

//        // Registra Benficiário
//        Beneficiario beneficiario = new Beneficiario("EMPRESA XYZ", "1087", 1, "123456", "86479743520");
//        // Registra Pagador
//        Pagador pagador = new Pagador("Pagador PF", "65453512300", "Rua ABC, 123", "Centro", "12345678", "SAO PAULO", "SP");
//        // Registra data de emissão
//        LocalDate emissao = LocalDate.of(2023, 10, 02);
//        // Registra data de vencimento
//        LocalDate vencimento = LocalDate.of(2017, 9, 29);
//        Titulo titulo = new Titulo(777.00d, emissao, vencimento, 3, 3, pagador);
//        List<Titulo> titulos = ImmutableList.of(titulo);
//        // Registra data de geração da remessa
//        LocalDateTime dataHoraGeracao = LocalDateTime.of(2017, 10, 2, 8, 9, 44);
//        // Registra data de gravação da remessa
//        LocalDate dataGravacao = LocalDate.of(2017, 10, 2);
//        int numeroRemessa = 1;
//        // Gera remessa
//        Remessa remessa = new Remessa(numeroRemessa, beneficiario, titulos, dataHoraGeracao, dataGravacao);
//        String remessaGerada = remessa.gerarArquivo();

        try{
            RemessaGenerator.generateremessa();
        } catch(IOException e){
            System.out.println("Error: " + e);
        } finally {
            System.out.println("Finalizando");
        }
        
    }
}

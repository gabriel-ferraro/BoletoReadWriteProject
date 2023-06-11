package com.br.cnabremmitance.utils;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class CnabWriter {

    private final static String QUEUE_NAME = "EnviarRemessa";

    public static void EnviarMensagemDaRemessa(String nRemessa) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(32790);

        Connection connection = null;
        Channel channel = null;
        try {
            // Cria uma conexão e um canal
            connection = factory.newConnection();
            channel = connection.createChannel();

            // Declara a fila
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            String mensagem = "[x] Remessa recebida para importação: " + nRemessa + "!";
            for (int i = 0; i < 1; i++) {
                String mensagemCompleta = mensagem;

                channel.basicPublish("", QUEUE_NAME, null, mensagemCompleta.getBytes("UTF-8"));
                System.out.println("Mensagem enviada: " + mensagemCompleta);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        } finally {
            // Fecha o canal e a conexão
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static void main() {
        String caminhoPlanilha = "C:/Users/Pichau/Desktop/BoletosNaoPagos/Clientes.xlsm";

        try (FileInputStream fis = new FileInputStream(caminhoPlanilha)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0); // Obtém a primeira planilha

            // Itera pelas linhas da planilha, começando da segunda linha (índice 1)
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                // Extrai os dados da planilha
                String nome = row.getCell(0).getStringCellValue();
                Double valor = row.getCell(1).getNumericCellValue();
                String cpf = row.getCell(2).getStringCellValue();
                Integer matricula = (int) row.getCell(3).getNumericCellValue();
                Date dataEmissao = row.getCell(4).getDateCellValue();

                RemessaArquivo remessa = new RemessaArquivo(LayoutsSuportados.LAYOUT_BB_CNAB240_COBRANCA_REMESSA);
                remessa.addNovoCabecalho()
                        .sequencialArquivo(1)
                        .dataGeracao(new Date())
                        .setVal("horaGeracao", new Date())
                        .banco("0", "Banco")
                        .cedente("ACME S.A LTDA.", "1")
                        .convenio("1", "1", "1", "1")
                        .carteira("00");

                // Gera a remessa com os dados extraídos
                remessa.addNovoDetalheSegmentoP()
                        .valor(valor)
                        .dataGeracao(new Date())
                        .dataVencimento(dataEmissao)
                        .numeroDocumento(rowIndex)
                        .nossoNumero(rowIndex)
                        .banco("0", "Banco")
                        .cedente("ACME S.A LTDA.", "1")
                        .convenio("1", "1", "1", "1")
                        .sequencialRegistro(rowIndex)
                        .carteira("00");

                remessa.addNovoDetalheSegmentoQ()
                        .sacado(nome, cpf)
                        .banco("0", "Banco")
                        .cedente("ACME S.A LTDA.", "1")
                        .convenio("1", "1", "1", "1")
                        .sequencialRegistro(rowIndex + 1)
                        .carteira("00");

                remessa.addNovoRodapeLote()
                        .quantidadeRegistros(sheet.getLastRowNum())
                        .valorTotalRegistros(1)
                        .banco("0", "Banco")
                        .cedente("ACME S.A LTDA.", "1")
                        .convenio("1", "1", "1", "1")
                        .carteira("00");

                remessa.addNovoRodape()
                        .quantidadeRegistros(sheet.getLastRowNum())
                        .valorTotalRegistros(1)
                        .setVal("codigoRetorno", "1")
                        .banco("0", "Banco").cedente("ACME S.A LTDA.", "1")
                        .convenio("1", "1", "1", "1")
                        .carteira("00");

                String remessaStr = remessa.render();
                String nRemessa = "remessa" + rowIndex + ".txt";

                EnviarMensagemDaRemessa(nRemessa);
                try (FileWriter writer = new FileWriter("C:/Users/Pichau/source/repos/Pagamentos/Pagamentos/BoletoBancario/" + nRemessa)) {
                    writer.write(remessaStr);
                }

                // Verifica se o contador atingiu mil e interrompe o loop
                if (rowIndex == 1000) {
                    break;
                }
            }

            // Feche o workbook após a conclusão do processamento
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * *
     * Cria tabela para remessas.
     *
     * @return DDl para criacao da tabela remmitance.
     */
    private String createRemmitanceTable() {
        return """
            DROP TABLE IF EXISTS remmitance;

            CREATE TABLE remmitance(
                id SERIAL PRIMARY KEY,
                pagador VARCHAR(40),
                valor DECIMAL(10,2),
                CPF CHAR(14) UNIQUE,
                matricula INTEGER,
                emissao TIMESTAMP,
                vencimento_remessa TIMESTAMP,
                nome_beneficiario VARCHAR(30),
                numero_agencia CHAR(5),
                dig_verificador_agencia CHAR(1),
                numero_conta_corrente VARCHAR(12),
                numero_documento_beneficiario VARCHAR(15),
                numero_documento_pagador VARCHAR(11),
                endereco_pagador VARCHAR(40),
                bairro_pagador VARCHAR(40),
                cep_pagador CHAR(8),
                cidade_pagador VARCHAR(15),
                uf_Pagador CHAR(2),
                dt_geracao TIMESTAMP,
                compensado BOOLEAN
            );
        """;
    }

    /**
     * Insere os dados da remessa no banco de dados.
     */
//    private void insertRegisterInDB(Remessa remessa) {
//        
//    }
    /**
     * Cria um valor randomico entre um limite inicial e final.
     *
     * @param minValue Valor inicial.
     * @param maxValue Valor final.
     * @return Um inteiro randomico entre minvalue e maxValue(inclusos).
     */
    private long createRandomValue(long minValue, long maxValue) {
        if (maxValue < minValue) {
            throw new IllegalArgumentException("O valor final deve ser maior que valor inicial");
        }
        return new Random().nextLong((maxValue - minValue) + 1) + minValue;
    }

    public static void generateRemmitances(String url, String user, String password, String filePath, String tableName) {

    }
}

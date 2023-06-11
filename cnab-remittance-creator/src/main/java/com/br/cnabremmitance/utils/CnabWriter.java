package com.br.cnabremmitance.utils;

import com.br.cnabremmitance.models.Remittance;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import net.anschau.cnab.caixa.cnab240.Beneficiario;
import net.anschau.cnab.caixa.cnab240.Pagador;
import net.anschau.cnab.caixa.cnab240.Remessa;
import net.anschau.cnab.caixa.cnab240.Titulo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class CnabWriter {

    // Local da planilha com dados. Caminho absoluto no meu PC: "C:\\boletoReadWriteProject\\Clientes.xlsm".
    private static final String SHEET_FILE_PATH = "../../Clientes.xlsm";
    // Local do hotFolder.
    private static final String HOTFOLDER_PATH = "../../hotFolder";

    static final Logger LOG = Logger.getLogger(CnabWriter.class.getName());

    /**
     * Cria remessas e as inclui no hotfolder como CNAB240.
     *
     * @param remittanceQtt Quantidade de remessas a serem criadas.
     * @param filePath Caminho do hotfolder para armazenar as remessas.
     * @param xlsSheetPath Caminho do arquivo contendo dados a serem consumidos
     * para gerar remessas.
     * @throws java.io.IOException.
     * @throws java.util.concurrent.TimeoutException
     * @throws java.io.IOException.
     * @throws java.util.concurrent.TimeoutException.
     */
    public void generateRemittances(Integer remittanceQtt, String filePath, String xlsSheetPath) throws IOException, TimeoutException {
        // Caminho para armazenar as remessas criadas.
        String hotFolderPath = filePath == null ? HOTFOLDER_PATH : filePath;
        // Caminho da planilha com dados para geração das remessas.
        String sheetPath = xlsSheetPath == null ? SHEET_FILE_PATH : xlsSheetPath;
        // Criando inputStream para manipulacao e insercao das remessas.
        FileInputStream fis = new FileInputStream(sheetPath);
        Workbook workbook = new XSSFWorkbook(fis);
        // Identificando planilha inicial (indice 0).
        Sheet sheet = workbook.getSheetAt(0);

        for (int rowIndex = 1; rowIndex <= remittanceQtt; rowIndex++) {
            Row row = sheet.getRow(rowIndex);

            // Adquire valores da planilha para criar registro
            String nomePagador = row.getCell(0).getStringCellValue();
            Double valorTitulo = row.getCell(1).getNumericCellValue();
            String cpf = row.getCell(2).getStringCellValue();
            String matricula = row.getCell(3).getStringCellValue();
            Date dataEmissao = row.getCell(4).getDateCellValue();

            // gerar valores aleatorios
            String numeroAgencia = createRandomValue(10000, 99999).toString();
            Integer digVerificadorAgencia = createRandomValue(1, 9);
            String numeroContaCorrente = createRandomValue(100000, 999999).toString();
            String numDocBeneficiario = createRandomValue(100000, 999999).toString();
            String numDocPagador = createRandomValue(100000000, 999999999).toString();
            String cep = createRandomValue(10000000, 99999999).toString();

            Beneficiario beneficiario = new Beneficiario(
                    "EMPRESA XYZ",
                    numeroAgencia,
                    digVerificadorAgencia,
                    numeroContaCorrente,
                    numDocBeneficiario
            );

            Pagador pagador = new Pagador(
                    nomePagador,
                    numDocPagador,
                    "Rua qwer",
                    "Bairro asdf",
                    cep,
                    "Cidade zxcv",
                    "PR"
            );

            LocalDate emissao = LocalDate.of(
                    dataEmissao.getYear(),
                    dataEmissao.getMonth() + 1,
                    dataEmissao.getDay()
            );

            // Vencimento das remessas ocorre em 01/06/2019.
            LocalDate vencimento = LocalDate.of(
                    2019,
                    06,
                    1
            );

            Titulo titulo = new Titulo(
                    valorTitulo,
                    emissao,
                    vencimento,
                    3,
                    3,
                    pagador
            );

            LocalDateTime dataHoraGeracao = LocalDateTime.of(
                    LocalDate.now().getDayOfYear(),
                    LocalDate.now().getMonthValue(),
                    LocalDate.now().getDayOfMonth(),
                    LocalDateTime.now().getHour(),
                    LocalDateTime.now().getMinute(),
                    LocalDateTime.now().getSecond()
            );

            Remessa remessaCriada = new Remessa(
                    rowIndex,
                    beneficiario,
                    ImmutableList.of(titulo),
                    dataHoraGeracao,
                    LocalDate.now()
            );
            
            // Cria objeto de remessa reduzido para persistir no banco.
            Remittance remittance = new Remittance(nomePagador, valorTitulo, cpf, matricula, dataHoraGeracao);

            // Criando remessa no modelo CNAB240.
            saveRemittanceInHotFolder(rowIndex, remessaCriada.gerarArquivo(), hotFolderPath);
            // Save in DB
            saveRemittanceInDB(remittance);
        }
    }

    /**
     * Persiste a remessa compensada no DB.
     *
     * @param remittance A remessa que foi compensada.
     */
    public void saveRemittanceInDB(Remittance remittance) {
        
    }

    /**
     * Salva a remessa gerada no hotFolder.
     *
     * @param remittanceValue Numero da remessa gerada.
     * @param cnabRemittance Remessa gerada.
     * @param hotFolderPath Caminho do hotFolder.
     * @throws java.io.IOException.
     */
    public void saveRemittanceInHotFolder(Integer remittanceValue, String cnabRemittance, String hotFolderPath) {
        File file = new File(hotFolderPath + "(" + remittanceValue + ")" + ".cnab240");
        try (FileWriter writer = new FileWriter(file)) {
            // Se o arquivo existe, sobrescreve conteudo.
            if (file.exists()) {
                writer.write(cnabRemittance);
                // Senao, cria o arquivo e escreve a remessa
            } else {
                file.createNewFile();
                writer.write(cnabRemittance);
            }
            writer.close();
        } catch (IOException ex) {
            LOG.throwing(CnabWriter.class.getName(), "saveToHotFolder", ex);
        }
    }

    /**
     * Cria um valor randomico entre um limite inicial e final.
     *
     * @param minValue Valor inicial.
     * @param maxValue Valor final.
     * @return Um inteiro randomico entre minvalue e maxValue(inclusos).
     */
    private Integer createRandomValue(Integer minValue, Integer maxValue) {
        if (maxValue < minValue) {
            throw new IllegalArgumentException("O valor final deve ser maior que valor inicial");
        }
        return new Random().nextInt((maxValue - minValue) + 1) + minValue;
    }
}

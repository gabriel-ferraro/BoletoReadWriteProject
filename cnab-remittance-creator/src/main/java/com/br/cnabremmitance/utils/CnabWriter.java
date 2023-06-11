package com.br.cnabremmitance.utils;

import com.br.cnabremmitance.models.Remittance;
import com.br.cnabremmitance.repositories.RemittanceRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CnabWriter {

    // Local da planilha com dados. Caminho absoluto no meu PC: "C:\\boletoReadWriteProject\\Clientes.xlsm".
    private static final String SHEET_FILE_PATH = "../Clientes.xlsm";
    // Local do hotFolder. Caminho absoluto no meu PC: "C:\\boletoReadWriteProject\\hotFolder".
    private static final String HOTFOLDER_PATH = "../hotFolder";
    // Publicador da mensagem.
    private static MessageSender msgSender;
    // Repository para persistir remessa.
    private static RemittanceRepository remittanceRepository;

    static final Logger LOG = Logger.getLogger(CnabWriter.class.getName());

    @Autowired
    public CnabWriter(MessageSender msgSender, RemittanceRepository remittanceRepository) {
        this.msgSender = msgSender;
        this.remittanceRepository = remittanceRepository;
    }

    /**
     * Cria remessas, inclui no BD e no hotfolder como CNAB240.Flag identifica se
 deve processar remessas logo apos cria-las
     *
     * @param remittanceQtt Quantidade de remessas a serem criadas.
     * @param filePath Caminho do hotfolder para armazenar as remessas.
     * @param xlsSheetPath Caminho do arquivo contendo dados a serem consumidos
     * para gerar remessas. ser compensadas apos criadas.
     * @param compensateRemittances Flag para identificar se a aplicacao de
     * compensacao deve ser notificada com a criacao da remessa.
     * @throws java.io.IOException
     * @throws java.util.concurrent.TimeoutException
     */
    public void generateRemittances(Integer remittanceQtt, String filePath, String xlsSheetPath, Boolean compensateRemittances) throws IOException, TimeoutException {
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
            String payerName = row.getCell(0).getStringCellValue();
            Double titleValue = row.getCell(1).getNumericCellValue();
            String cpf = row.getCell(2).getStringCellValue();
            String matricula = Double.toString(row.getCell(3).getNumericCellValue());
            Date emissionDate = row.getCell(4).getDateCellValue();

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
                    payerName,
                    numDocPagador,
                    "Rua qwer",
                    "Bairro asdf",
                    cep,
                    "Cidade zxcv",
                    "PR"
            );

            LocalDate emission = LocalDate.of(
                    emissionDate.getYear() + 1900, // Adiciona 1900 porque a API original de datas do java foi feita por gente maluca.
                    emissionDate.getMonth() + 1,
                    emissionDate.getDate()
            );

            // Vencimento das remessas ocorre em 17/06/2019.
            LocalDate dueDate = LocalDate.of(
                    2019,
                    06,
                    17
            );

            Titulo title = new Titulo(
                    titleValue,
                    emission,
                    dueDate,
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
                    ImmutableList.of(title),
                    dataHoraGeracao,
                    LocalDate.now()
            );

            // Criando remessa no modelo CNAB240.
            saveRemittanceInHotFolder(rowIndex, remessaCriada.gerarArquivo(), hotFolderPath);
            // Cria objeto de remessa nao compensada para persistir no banco.
            Remittance remittance = new Remittance(payerName, titleValue, cpf, matricula, emission);
            // Salva no BD.
            saveRemittanceInDB(remittance);
            // Se flag verdadeira.
            if (compensateRemittances == true) {
                // Envia mensagem para aplicacao de compensacao compensar remessa.
                msgSender.requestToCompensateRemittances(rowIndex, null);
            }
        }
    }

    /**
     * Persiste a remessa compensada no BD.
     *
     * @param remittance A remessa que foi compensada.
     */
    public void saveRemittanceInDB(Remittance remittance) {
        remittanceRepository.save(remittance);
    }

    /**
     * Salva a remessa gerada no hotFolder.
     *
     * @param remittanceId Numero da Id da remessa gerada.
     * @param cnabRemittance Remessa gerada.
     * @param hotFolderPath Caminho do hotFolder.
     */
    public void saveRemittanceInHotFolder(Integer remittanceId, String cnabRemittance, String hotFolderPath) {
        // sb para nomear local e nome do arquivo de remessa.
        StringBuilder sb = new StringBuilder();
        // Exemplo de caminho de remessa: ../hotFolder/remessa(1).cnab240;
        sb.append(hotFolderPath).append("/remessa(").append(remittanceId).append(").cnab240");
        // Cria remessa como arquivo CNAB.
        File file = new File(sb.toString());
        try (FileWriter writer = new FileWriter(file)) {
            if (file.exists()) {
                // Se o arquivo existe, sobrescreve conteudo.
                writer.write(cnabRemittance);
            } else {
                // Senao, cria o arquivo e escreve a remessa
                file.createNewFile();
                writer.write(cnabRemittance);
            }
            // Fecha o fileWriter.
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

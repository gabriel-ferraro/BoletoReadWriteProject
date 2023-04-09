package XLSMtoPostgresDB;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Classe para converter dados de um arquivo xlsm e inseri-los como registros em
 * uma tabela de um banco de dados PostgresSQL
 *
 * @author Gabriel Ferraro
 */
public class XLSMtoPostgres {

    private final String url;
    private final String user;
    private final String password;

    public XLSMtoPostgres(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Processa os dados do arquivo xlsm e os adiciona como registro de uma
     * tabela (clients).
     *
     * @param filePath caminho do arquivo xlsm.
     * @param tableName nome da tabela onde serão armazenados os registros.
     * @param amountOfData inteiro que define a qtde de itens que serao
     * persistidos.
     * @throws IOException
     * @throws SQLException
     */
    public void convert(String filePath, String tableName, int amountOfData) throws IOException, SQLException {
        try (Connection conn = DriverManager.getConnection(url, user, password); FileInputStream fis = new FileInputStream(filePath); XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            //Carrega o nome da planilha
            XSSFSheet sheet = workbook.getSheetAt(0);
            int numColumns = sheet.getRow(0).getLastCellNum();
            String[] columnNames = new String[numColumns];
            for (int i = 0; i < numColumns; i++) {
                columnNames[i] = sheet.getRow(0).getCell(i).getStringCellValue();
            }
            //Cria a tabela de clients
            String createdTable = createClientsTable();
            
            try (PreparedStatement createStmt = conn.prepareStatement(createdTable)) {
                createStmt.execute();
            }
            //Chama generateInsertQuery e cria a String de para inserção de dados
            String insertQuery = generateInsert(tableName, columnNames);

            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                //sheet.getLastRowNum() - adquire a quantidade total de registros, colocar no lugar de amountOfData para registrar todos 22055 itens do arquivo Clientes.xlsm
                for (int i = 1; i <= amountOfData; i++) {
                    XSSFRow row = sheet.getRow(i);
                    for (int j = 0; j < numColumns; j++) {
                        XSSFCell cell = row.getCell(j);
                        if (cell == null) {
                            stmt.setNull(j + 1, java.sql.Types.NULL);
                        } else {
                            switch (cell.getCellType()) {
                                case STRING:
                                    stmt.setString(j + 1, cell.getStringCellValue());
                                    break;
                                case NUMERIC:
                                    if (DateUtil.isCellDateFormatted(cell)) {
                                        Date date = cell.getDateCellValue();
                                        stmt.setDate(j + 1, new java.sql.Date(date.getTime()));
                                    } else {
                                        stmt.setDouble(j + 1, cell.getNumericCellValue());
                                    }
                                    break;
                                case BOOLEAN:
                                    stmt.setBoolean(j + 1, cell.getBooleanCellValue());
                                    break;
                                default:
                                    stmt.setNull(j + 1, java.sql.Types.NULL);
                            }
                        }
                    }
                    stmt.executeUpdate();
                }
            }
        }
    }

    /**
     * Cria o DML para inserção dos dados.
     *
     * @param tableName nome da tabela onde serao armazenados os registros.
     * @param columnNames nome das colunas da planilha que serao os campos da
     * tabela.
     * @return DML de insercao dos dados.
     */
    private String generateInsert(String tableName, String[] columnNames) {
        StringBuilder sb = new StringBuilder();
        // Identifica o nome das colunas no arquivo xlsm
        sb.append("INSERT INTO ");
        sb.append(tableName);
        sb.append("(");
        for (int i = 0; i < columnNames.length; i++) {
            sb.append(columnNames[i]);
            if (i < columnNames.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("""
            ,vencimento_remessa,
            nome_beneficiario,
            numero_agencia,
            dig_verificador_agencia,
            numero_conta_corrente,
            numero_documento_beneficiario,
            numero_documento_pagador,
            endereco_pagador,
            bairro_pagador,
            cep_pagador,
            cidade_pagador,
            uf_Pagador,
            dt_geracao
        """);
        // Inserindo valores
        sb.append(") VALUES (");
        for (int i = 0; i < columnNames.length; i++) {
            sb.append("?");
            if (i < columnNames.length - 1) {
                sb.append(", ");
            }
        }
        //Gerando valores randomicos
        long numeroAgencia = createRandomValue(10000, 99999);
        long dvAgencia = createRandomValue(1,9);
        long numContaCorrente = createRandomValue(100000,999999);
        long numDocBeneficioario = createRandomValue(100000,999999);
        long numDocPagador = createRandomValue(100000000, 999999999);
        long cep = createRandomValue(10000000, 99999999);
                
        sb.append(",CURRENT_TIMESTAMP + INTERVAL '10 days',");
        sb.append("'EMPRESA XYZ',");
        sb.append(numeroAgencia + ",");
        sb.append(dvAgencia + ",");
        sb.append(numContaCorrente + ",");
        sb.append(numDocBeneficioario + ",");
        sb.append(numDocPagador + ",");
        sb.append("'Rua asdfg',");
        sb.append("'Bairro ABCD',");
        sb.append(cep + ",");
        sb.append("'Cidade ABCD',");
        sb.append("'PR',");
        sb.append("CURRENT_TIMESTAMP");
        
        sb.append(")");
        return sb.toString();
    }
    
    /***
     * Cria tabela para cliente
     * 
     * @return DDl para criacao da tabela clients
     */
    private String createClientsTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("""
            DROP TABLE IF EXISTS remessas;

            CREATE TABLE remessas(
                pagador VARCHAR(40),
                valor DECIMAL(10,2),
                CPF CHAR(14) UNIQUE PRIMARY KEY,
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
                dt_geracao TIMESTAMP
            );
        """);
        return sb.toString();
    }

    /**
     * Cria um valor randomico entre um limite inicial e final.
     *
     * @param initialValue Valor inicial
     * @param finalValue Valor final
     * @return Um inteiro randomico entre initialvalue e finalValue (inclusos)
     */
    private long createRandomValue(long initialValue, long finalValue) {
        if (finalValue < initialValue) {
            throw new IllegalArgumentException("finalValue deve ser maior que InitialValue");
        }
        Random random = new Random();
        return random.nextLong((finalValue - initialValue) + 1) + initialValue;
    }
}

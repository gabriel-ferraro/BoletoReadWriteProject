package XLSMtoPostgresDB;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
     * Processa os dados do arquivo xlsx e os adiciona como registro de uma
     * tabela.
     *
     * @param filePath caminho do arquivo xlsm.
     * @param tableName nome da tabela onde serão armazenados os registros.
     * @param amountOfData inteiro que define a qtde de itens que serão persistidos.
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
            //chama generateInsertQuery e cria a String de query para inserção de dados
            String insertQuery = generateInsertQuery(tableName, columnNames);

            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                //sheet.getLastRowNum() - adquire a quantidade total de registros, colocar no lugar de amountOfData para registrar todos 22055 itens do arquivo Clientes.xlsm
                for (int i = 1; i <= amountOfData; i++) {
                    XSSFRow row = sheet.getRow(i);
                    for (int j = 0; j < numColumns; j++) {
                        XSSFCell cell = row.getCell(j);
                        System.out.println("Valores da celulas: " + cell.getRawValue());
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
     * Cria a query para inserção dos dados.
     * @param tableName nome da tabela onde serão armazenados os registros.
     * @param columnNames nome das colunas da planilha que serão os campos da tabela.
     * @return query de inserção dos dados.
     */
    private String generateInsertQuery(String tableName, String[] columnNames) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(tableName);
        sb.append("(");
        for (int i = 0; i < columnNames.length; i++) {
            sb.append(columnNames[i]);
            if (i < columnNames.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(") VALUES (");
        for (int i = 0; i < columnNames.length; i++) {
            sb.append("?");
            if (i < columnNames.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}

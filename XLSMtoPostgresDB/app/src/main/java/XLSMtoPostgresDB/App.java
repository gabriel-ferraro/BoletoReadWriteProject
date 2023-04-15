package XLSMtoPostgresDB;

import java.io.IOException;
import java.sql.SQLException;

public class App {

    public static void main(String[] args) {
        System.out.println("Iniciando");

        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "admin";
        String password = "123456";

        String filePath = "../../Clientes.xlsm"; //caminho absoluto no meu PC: "C:\\boletoReadWrite\\Clientes.xlsm"
        String tableName = "remessas";

        try {
            XLSMtoPostgres converter = new XLSMtoPostgres(url, user, password);
            converter.convert(filePath, tableName, 50); //carrega is primeiros 50 itens da planilha
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Finalizando");
        }
    }
}
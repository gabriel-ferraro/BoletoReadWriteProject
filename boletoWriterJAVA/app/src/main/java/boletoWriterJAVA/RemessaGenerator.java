package boletoWriterJAVA;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Gabriel Ferraro
 */
public class RemessaGenerator {

    public static void generateremessa() throws IOException {
        // Configurações de conexao ao banco de dados
        String url = "jdbc:postgresql://localhost:5432/postgresDBWriter";
        String user = "admin";
        String password = "123456";
        // Consulta SQL para obter os dados da remessa
        String sql = "SELECT * FROM remessas";

        try {
            // Conexão ao banco de dados
            Connection con = DriverManager.getConnection(url, user, password);

            // Preparação e execução da consulta SQL
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // Criação da remessa e populacao dos seus atributos
            RemessaBuilder remessaBuilder = new RemessaBuilder();
            int registersQtt = 0; // Controle de arquivos de remessa
            while (rs.next()) {
                // Construindo remessa
                remessaBuilder.setNomePagador(rs.getString("pagador"));
                remessaBuilder.setValorTitulo(Double.parseDouble(rs.getString("valor")));
                Date dataEmissao = rs.getDate("emissao"); //data emissao
                remessaBuilder.setDiaEmissao(dataEmissao.getDate());
                remessaBuilder.setMesEmissao(dataEmissao.getMonth());
                remessaBuilder.setAnoEmissao(dataEmissao.getYear());
                Date dataVencimento = rs.getDate("vencimento_remessa"); //data vencimento
                remessaBuilder.setDiaVencimento(dataVencimento.getDate());
                remessaBuilder.setMesVencimento(dataVencimento.getMonth());
                remessaBuilder.setAnoVencimento(dataVencimento.getYear());
                remessaBuilder.setNomeBeneficiario(rs.getString("nome_beneficiario"));
                remessaBuilder.setNumeroAgencia(Long.toString(rs.getLong("numero_agencia")));
                remessaBuilder.setDigVerificadorAgencia(rs.getInt("dig_verificador_agencia"));
                remessaBuilder.setNumeroContaCorrente(Long.toString(rs.getLong("numero_conta_corrente")));
                remessaBuilder.setNumeroDocumentoBeneficiario(Long.toString(rs.getLong("numero_documento_beneficiario")));
                remessaBuilder.setNumeroDocumentoPagador(Long.toString(rs.getLong("numero_documento_pagador")));
                remessaBuilder.setEnderecoPagado(rs.getString("endereco_pagador"));
                remessaBuilder.setBairroPagador(rs.getString("bairro_pagador"));
                remessaBuilder.setCepPagador(Long.toString(rs.getLong("cep_pagador")));
                remessaBuilder.setCidadePagador(rs.getString("cidade_pagador"));
                remessaBuilder.setUfPagador(rs.getString("uf_Pagador"));
                Date dataGeracao = rs.getDate("dt_geracao"); //data geracao
                remessaBuilder.setAnoGeracao(dataGeracao.getYear());
                remessaBuilder.setMesGeracao(dataGeracao.getMonth());
                remessaBuilder.setDiaDoMesGeracao(dataGeracao.getDate());
                remessaBuilder.setHoraGeracao(10);
                remessaBuilder.setMinutosGeracao(10);
                remessaBuilder.setSegundosGeracao(10);
                // Instanciando String de remessa completa
                String remessa = remessaBuilder.buildRemessa();
                registersQtt++; // Incrementando qtde de remessas para criacao dos arquivos
                File file = new File("../../hotfolder/remessa" + "(" + registersQtt + ")" + ".cnab240");
                // Se o arquivo existe, sobrescreve conteudo
                if (file.exists()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(remessa);
                writer.close();
                // Senao, cria o arquivo e escreve a remessa
                } else {
                    file.createNewFile();
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    writer.write(remessa);
                    writer.close();
                }
            }
            // Fechamento dos recursos
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

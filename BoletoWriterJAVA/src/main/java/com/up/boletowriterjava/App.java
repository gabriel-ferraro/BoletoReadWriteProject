/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.up.boletowriterjava;

import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import net.anschau.cnab.caixa.cnab240.Beneficiario;
import net.anschau.cnab.caixa.cnab240.Pagador;
import net.anschau.cnab.caixa.cnab240.Remessa;
import net.anschau.cnab.caixa.cnab240.Titulo;

/**
 *
 * @author Gabriel Ferraro
 */
public class App {

    public static void main(String[] args) {
        System.out.println("Iniciando");

        Beneficiario beneficiario = new Beneficiario("João da Silva", "1087", 1, "123456", "86479743520");

        //SP0000000000000000
        Pagador pagador = new Pagador("Pagador PF", "65453512300", "Rua ABC, 123", "Centro", "12345678", "SAO PAULO", "SP");

        LocalDate emissao = LocalDate.of(2017, 10, 02);
        LocalDate vencimento = LocalDate.of(2017, 9, 29);
        Titulo titulo = new Titulo(6.00d, emissao, vencimento, 3, 3, pagador);
        List<Titulo> titulos = ImmutableList.of(titulo);

        int numeroRemessa = 1;
        LocalDateTime dataHoraGeracao = LocalDateTime.of(2017, 10, 2, 8, 9, 44);
        LocalDate dataGravacao = LocalDate.of(2017, 10, 2);
        Remessa remessa = new Remessa(numeroRemessa, beneficiario, titulos, dataHoraGeracao, dataGravacao);

        String remessaGerada = remessa.gerarArquivo();
        System.out.println("Arquivo gerado:\n" + remessaGerada);
    }
}

package boletoWriterJAVA;

import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.anschau.cnab.caixa.cnab240.Beneficiario;
import net.anschau.cnab.caixa.cnab240.Pagador;
import net.anschau.cnab.caixa.cnab240.Remessa;
import net.anschau.cnab.caixa.cnab240.Titulo;

/**
 * Builder para remssa.
 * @author Gabriel Ferraro
 */
@Getter
@Setter
public class RemessaBuilder {
    private String nomeBeneficiario;
    private String numeroAgencia;
    private int digVerificadorAgencia;
    private String numeroContaCorrente;
    private String numeroDocumentoBeneficiario;
    private String nomePagador;
    private String numeroDocumentoPagador;
    private String enderecoPagado;
    private String bairroPagador;
    private String cepPagador;
    private String cidadePagador;
    private String ufPagador;
    private int anoEmissao;
    private int mesEmissao;
    private int diaEmissao;
    private int anoVencimento;
    private int mesVencimento;
    private int diaVencimento;
    private double valorTitulo;
    private int anoGeracao;
    private int mesGeracao;
    private int diaDoMesGeracao;
    private int horaGeracao;
    private int minutosGeracao;
    private int segundosGeracao;
    
    /**
     * Constroi e retorna uma objeto remessa.
     * @return Objeto de remessa.
     */
    public String buildRemessa() {
        Beneficiario beneficiario = new Beneficiario(
            nomeBeneficiario,
            numeroAgencia,
            digVerificadorAgencia,
            numeroContaCorrente,
            numeroDocumentoBeneficiario
        );

        Pagador pagador = new Pagador(
            nomePagador,
            numeroDocumentoPagador,
            enderecoPagado,
            bairroPagador,
            cepPagador,
            cidadePagador,
            ufPagador
        );

        LocalDate emissao = LocalDate.of(
            anoEmissao,
            mesEmissao,
            diaEmissao
        );

        LocalDate vencimento = LocalDate.of(
            anoVencimento,
            mesVencimento,
            diaVencimento
        );

        Titulo titulo = new Titulo(
            valorTitulo,
            emissao,
            vencimento,
            3,
            3,
            pagador
        );

        List<Titulo> titulos = ImmutableList.of(titulo);

        LocalDateTime dataHoraGeracao = LocalDateTime.of(
            anoGeracao,
            mesGeracao,
            diaDoMesGeracao,
            horaGeracao,
            minutosGeracao,
            segundosGeracao
        );

        LocalDate dataGravacao = LocalDate.now();
        
        int numeroRemessa = 1;

        Remessa remessaCriada = new Remessa(
            numeroRemessa,
            beneficiario,
            titulos,
            dataHoraGeracao,
            dataGravacao
        );

        return remessaCriada.gerarArquivo();
    }
}

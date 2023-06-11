package com.remittance.compensator.controllers;

import com.remittance.compensator.models.Remittance;
import com.remittance.compensator.services.RemessaService;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("remittances")
public class RemessaController {

    private RemessaService remessaService;

    @Autowired
    public RemessaController(RemessaService remessaService) {
        this.remessaService = remessaService;
    }

    /**
     * Retorna todas remessas persistidas no banco.
     *
     * @return todas as remessas registradas.
     */
    @GetMapping("/remessas")
    @Operation(summary = "Retorna todas remessas persistidas no banco",
            description = "Realiza o retorno de todas as remessas na URI.")
    @ApiResponse(responseCode = "200", description = "Remessas retornadas.")
    @ApiResponse(responseCode = "404", description = "Remessas não encontradas.")
    public ResponseEntity<List<Remittance>> findAll() {
        return ResponseEntity.ok().body(remessaService.getAllRemessas());
    }

    /*
     * Retorna uma remessa pelo id
     *
     * @param id referente a remessa
     * @return remessa do id requisitado
     */
    @GetMapping("/remessa/{id}")
    @Operation(summary = "Retorna uma remessa com base no id do parâmetro",
            description = "Realiza o retorno uma remessa na URI.")
    @ApiResponse(responseCode = "200", description = "Remessas retornada.")
    @ApiResponse(responseCode = "404", description = "Remessas não encontrada.")
    public ResponseEntity<Remittance> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(remessaService.getRemessaById(id));
    }

    /*
     * Simula a compensação do valor a ser pago na remessa.recebe artibutos via
     * body, se o valor enviado for igual ao valor a ser compensado, uma
     * mensagem de sucesso é retornada.
     *
     * @param compensacaoRequest Tipo para valores da remessa
     * @return Aprovação ou declínio do pagamento no formato de uma String.
     */
    @PostMapping("/compensa_boleto")
    @Operation(summary = "Realiza a compensação do valor a ser pago na remessa.",
            description = "Compensa valor a ser pago na remessa. recebe artibutos via body, se o valor enviado"
            + " for igual ao valor a ser compensado, uma mensagem de sucesso é retornada")
    @ApiResponse(responseCode = "200", description = "Valor da remessa compensado com sucesso.")
    @ApiResponse(responseCode = "201", description = "Requisição bem sucedida e compensação criada com sucesso.")
    @ApiResponse(responseCode = "400", description = "Erro por valor invalido inserido.")
    public String compensaBoleto(@RequestBody CompensacaoRequest compensacaoRequest) {
        // adquirindo remessa persistida no BD pelo id
        Remittance remessa = remessaService.getRemessaById(Long.valueOf(compensacaoRequest.getId()));

        // mensagem de retorno
        String compensacao;
        if (Double.valueOf(compensacaoRequest.getValue()) > remessa.getValorRemessa()) {
            compensacao = "Valor enviado é maior que o valor da remessa!";
        } else if (Double.valueOf(compensacaoRequest.getValue()) < remessa.getValorRemessa()) {
            compensacao = "Valor enviado é insuficiente para compensar remessa!";
        } else {
            compensacao = "Remessa compensada com sucesso!!!";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("id da remssa: ")
                .append(compensacaoRequest.getId()).append('\n')
                .append("Nome do pagador: ").append(remessa.getPagador()).append('\n')
                .append("Nome da empresa: ").append(remessa.getNomeBeneficiario()).append('\n')
                .append("Data de vencimento da remessa: ").append(remessa.getVencimentoRemessa()).append('\n')
                .append("Valor recebido: ").append(compensacaoRequest.getValue()).append('\n')
                .append("Valor a ser compensado: ").append(remessa.getValorRemessa()).append('\n')
                .append(compensacao);
        return sb.toString();
    }
}

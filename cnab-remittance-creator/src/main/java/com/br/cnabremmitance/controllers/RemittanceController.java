package com.br.cnabremmitance.controllers;

import com.br.cnabremmitance.models.Remittance;
import com.br.cnabremmitance.services.RemittanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("remittances")
public class RemittanceController {

    private RemittanceService remittanceService;

    @Autowired
    public RemittanceController(RemittanceService remittanceService) {
        this.remittanceService = remittanceService;
    }

    @Operation(summary = "Retorna todas remessas persistidas no banco.",
            description = "Realiza o retorno de todas as remessas na URI.")
    @ApiResponse(responseCode = "200", description = "Remessas retornadas.")
    @ApiResponse(responseCode = "404", description = "Remessas nao encontradas.")
    @GetMapping
    public ResponseEntity<List<Remittance>> getAllRemmitances() {
        return ResponseEntity.ok().body(remittanceService.findAll());
    }

    @Operation(summary = "Retorna remessa do id indicado no caminho da URL.",
            description = "Realiza o retorno da remessa.")
    @ApiResponse(responseCode = "200", description = "Remessa retornada.")
    @ApiResponse(responseCode = "404", description = "Remessas nao encontrada.")
    @GetMapping(path = "/{id}")
    public ResponseEntity<Remittance> getRemittance(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(remittanceService.findById(id));
    }

    @Operation(summary = "Retorna todas remessas compensadas",
            description = "Realiza o retorno de todas as remessas com o atributo isCompensated = true.")
    @ApiResponse(responseCode = "200", description = "Remessas retornadas.")
    @ApiResponse(responseCode = "404", description = "Remessas nao encontradas.")
    @GetMapping(path = "/compensated")
    public ResponseEntity<List<Remittance>> getCompensatedRemittances() {
        return ResponseEntity.ok().body(remittanceService.getCompensatedRemittances());
    }

    @Operation(summary = "Retorna todas remessas nao compensadas",
            description = "Realiza o retorno de todas as remessas com o atributo isCompensated = false.")
    @ApiResponse(responseCode = "200", description = "Remessas retornadas.")
    @ApiResponse(responseCode = "404", description = "Remessas nao encontradas.")
    @GetMapping(path = "/non-compensated")
    public ResponseEntity<List<Remittance>> getNonCompensatedRemittances() {
        return ResponseEntity.ok().body(remittanceService.getNonCompensatedRemittances());
    }

    @Operation(summary = "Faz requisicao para que remessas sejam compensadas.",
            description = "Faz requisicao para que quantidade de remessas sejam compensadas pela aplicacao listener via mensageria.")
    @ApiResponse(responseCode = "200", description = "Requsicao enviada com sucesso.")
    @ApiResponse(responseCode = "500", description = "Requsicao nao pode ser enviada com sucesso.")
    @PutMapping(path = "/{amountToProcess}")
    public ResponseEntity<String> requestToCompensateRemittances(@PathVariable("amountToProcess") Integer amountToProcess) {
        remittanceService.requestToCompensateRemittances(amountToProcess);
        return ResponseEntity.ok().body("requisicao para processar " + amountToProcess + " remessas enviada.");
    }

    @Operation(summary = "Cria remessas CNAB240 localmente e persiste remessa como registro no BD.",
            description = "Cria remessas localmente no hotFolder e as salva como registro no BD. As remessas sao salvas com o atributo isCompensated = false.")
    @ApiResponse(responseCode = "200", description = "Remessas criadas com sucesso.")
    @ApiResponse(responseCode = "500", description = "Excecao ocorreu ao tentar processar remessas.")
    @PostMapping(path = "/{amountToCreate}")
    public ResponseEntity<String> createRemittances(@PathVariable("amountToCreate") Integer amountToCreate) {
        remittanceService.createRemittances(amountToCreate, false);
        return ResponseEntity.ok().body(amountToCreate + " remessas criadas.");
    }
    
    @Operation(summary = "Cria remessas CNAB240 e faz a requisicao para compensacao das mesmas.",
            description = "Cria remessas localmente no hotFolder e salva no BD. Faz requisicao para API de compensacao compensar essas remessas")
    @ApiResponse(responseCode = "200", description = "Remessas criadas e requsicao enviada.")
    @ApiResponse(responseCode = "500", description = "Excecao ocorreu ao tentar processar remessas/realizar requisicao.")
    @PostMapping(path = "/create-and-compensate/{amountToCreate}")
    public ResponseEntity<String> createRemittancesAndRequestCompensation(@PathVariable("amountToCreate") Integer amountToCreate) {
        remittanceService.createRemittances(amountToCreate, true);
        return ResponseEntity.ok().body(amountToCreate + " remessas criadas.");
    }
}

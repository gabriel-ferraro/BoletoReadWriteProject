package processa.remessa.remessaAPI.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import processa.remessa.remessaAPI.model.Remessa;
import processa.remessa.remessaAPI.service.RemessaService;

/**
 *
 * @author Gabriel Ferraro
 */
@RestController
@RequestMapping("/remessa")
public class RemessaController {

    @Autowired
    private RemessaService remessaService;

    /***
     * Retorna todas remessas na uri "/remessa"
     * 
     * @return todas as remessas registradas
     */
    @GetMapping
    public ResponseEntity<List<RemessaDTO>> findAll() {
        //adquire remessas
        List<Remessa> remessas = remessaService.getAllRemessas();
        //Constroi as remessas com o dto para exportar objetos na uri
        List<RemessaDTO> remessaDTOList = remessas.stream()
            .map(remessa -> {
                return new RemessaDTO(
                    remessa.getId(),
                    remessa.getPagador(),
                    remessa.getNomeBeneficiario(),
                    remessa.getVencimentoRemessa(),
                    remessa.getValorRemessa()
                );
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok().body(remessaDTOList);
    }
    
    /***
     * Compensa valor a ser pago na remessa. recebe artibutos via body, se o valor enviado for igual ao valor a ser compensado, uma mensagem de sucesso é retornada.
     * 
     * @param clientDTO
     * @return 
     */
    @PostMapping("/compensa_boleto")
    public String compensaBoleto(@RequestBody Map<String, String> body) {
        long id = Long.parseLong(body.get("id"));
        //adquirindo remessa persistida no BD pelo id
        Remessa remessa = remessaService.getRemessaById(id);
        Double valor = Double.parseDouble(body.get("valor"));
        
        String compensacao;
        if(valor > remessa.getValorRemessa()){
            compensacao = "Valor enviado é maior que o valor da remessa!";
        } else if (valor < remessa.getValorRemessa()){
            compensacao = "Valor enviado é insuficiente para compensar remessa!";
        } else {
            compensacao = "Remessa compensada com sucesso!!!";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("id da remssa: ")
        .append(id).append('\n')
        .append("Nome do pagador: ").append(remessa.getPagador()).append('\n')
        .append("Nome da empresa: ").append(remessa.getNomeBeneficiario()).append('\n')
        .append("Data de vencimento da remessa: ").append(remessa.getVencimentoRemessa()).append('\n')
        .append("Valor recebido: ").append(valor).append('\n')
        .append("Valor a ser compensado: ").append(remessa.getValorRemessa()).append('\n')
        .append(compensacao);
        String result = sb.toString();
        return result;
    }
}

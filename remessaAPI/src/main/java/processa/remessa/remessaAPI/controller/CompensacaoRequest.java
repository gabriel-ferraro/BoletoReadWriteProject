package processa.remessa.remessaAPI.controller;

import lombok.Getter;

/**
 * Tipo para receber id e valor da remessa.
 * @author Gabriel Ferraro
 */
@Getter
public class CompensacaoRequest {
    private String id;
    private String value;
}

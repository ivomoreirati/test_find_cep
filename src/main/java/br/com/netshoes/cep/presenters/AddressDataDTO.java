package br.com.netshoes.cep.presenters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDataDTO {
    private String logradouro;
    private String bairro;
    private String localidade;
    private String uf;

    //Basta qualquer campo vir nulo, entao o cep nao existe, mesmo sendo válido
    @JsonIgnore
    public boolean isEmpty() {
        return (this.logradouro == null && this.bairro == null);
    }
}

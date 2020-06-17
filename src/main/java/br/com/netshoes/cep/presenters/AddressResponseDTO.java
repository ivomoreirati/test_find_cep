package br.com.netshoes.cep.presenters;

import br.com.netshoes.cep.presenters.base.BaseDTO;

public class AddressResponseDTO extends BaseDTO<AddressDataDTO> {
    public AddressResponseDTO() {
    }

    public AddressResponseDTO(AddressDataDTO data) {
        super(data);
    }
}
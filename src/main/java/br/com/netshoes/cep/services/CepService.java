package br.com.netshoes.cep.services;

import br.com.netshoes.cep.parameters.CepRequestDTO;
import br.com.netshoes.cep.presenters.AddressDataDTO;

public interface CepService {

    AddressDataDTO findAddressByCep(CepRequestDTO request);
}

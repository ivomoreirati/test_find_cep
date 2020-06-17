package br.com.netshoes.cep.services.impl;

import br.com.netshoes.cep.client.CepClient;
import br.com.netshoes.cep.parameters.CepRequestDTO;
import br.com.netshoes.cep.presenters.AddressDataDTO;
import br.com.netshoes.cep.services.CepService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

import static br.com.netshoes.cep.constants.Constants.*;
import static org.apache.commons.lang.StringUtils.isNumeric;

@Service
public class CepServiceImpl implements CepService {

    private final CepClient cepClient;

    public CepServiceImpl(final CepClient cepClient) {
        this.cepClient = cepClient;
    }

    private AddressDataDTO getAddressByCep(String cep) {
        try {
            return cepClient.find(cep);
        } catch(HttpStatusCodeException hce){
            ThrowException(HttpStatus.NOT_FOUND, ZIP_POSTAL_CODE_NOT_FOUND);
        } catch(ResourceAccessException rae){
            ThrowException(HttpStatus.BAD_GATEWAY, EXTERN_SERVICE_CEP_UNAVAILABLE);
        }
        return new AddressDataDTO();
    }

    @Override
    public AddressDataDTO findAddressByCep(final CepRequestDTO request){
        validationCep(request);
        var result = getAddressByCep(request.getCep());
        if(result.isEmpty()){
            final var lastPositionString = 7;
            return retrieveFindAddressByCep(request.getCep(), lastPositionString);
        }
        return result;
    }

    private void ThrowException(HttpStatus httpStatus, String message) {
        throw new ResponseStatusException(httpStatus, message);
    }

    private void validationCep(CepRequestDTO request){
        if(!isNumeric(request.getCep())) {
            ThrowException(HttpStatus.BAD_REQUEST, ZIP_POSTAL_CODE_INVALID);
        }
        if(Integer.parseInt(request.getCep()) == 0) {
            ThrowException(HttpStatus.BAD_REQUEST, ZIP_POSTAL_CODE_INVALID);
        }
    }

    private String replaceStringByPosition(final String str, final char ch, final int index) {
        char[] chars = str.toCharArray();
        chars[index] = ch;
        return String.valueOf(chars);
    }

    //Percorrendo a String cep substituindo da direita até a esquerda com valor 0, até achar um resultado
    private AddressDataDTO retrieveFindAddressByCep(final String cep, final int indexString) {
        var cepAlter = cep;
        cepAlter = replaceStringByPosition(cepAlter, '0', indexString);
        final var result = getAddressByCep(cepAlter);
        if(!result.isEmpty()) { return result; }
        if(indexString >= 1) { return retrieveFindAddressByCep(cepAlter, indexString - 1); }
        //Após as tentativas de busca, se mesmo assim nao houver resultado, lanca-se a excecao abaixo
        ThrowException(HttpStatus.NOT_FOUND, ZIP_POSTAL_CODE_NOT_FOUND);
        return new AddressDataDTO();
    }
}

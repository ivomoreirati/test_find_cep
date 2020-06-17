package br.com.netshoes.cep.controllers;

import br.com.netshoes.cep.parameters.CepRequestDTO;
import br.com.netshoes.cep.presenters.AddressResponseDTO;
import br.com.netshoes.cep.presenters.ErrorPresenter;
import br.com.netshoes.cep.services.CepService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static br.com.netshoes.cep.constants.Constants.*;

@RestController
@Api(value = "CEP", tags = "CEP")
public class CepController {

    private final CepService cepService;

    public CepController(final CepService cepService){
        this.cepService = cepService;
    }

    @ApiOperation(value = "Consultar endereco pelo CEP",
            notes = "Retorna o endereco pelo cep")
    @ApiResponses({
            @ApiResponse(code = 201, message = ADDRESS_RETURN_SUCCESS),
            @ApiResponse(code = 400, message = ZIP_POSTAL_CODE_INVALID, response = ErrorPresenter.class),
            @ApiResponse(code = 404, message = ZIP_POSTAL_CODE_NOT_FOUND, response = ErrorPresenter.class),
            @ApiResponse(code = 502, message = EXTERN_SERVICE_CEP_UNAVAILABLE, response = ErrorPresenter.class)
    })
    @PostMapping(value = "/v1/address")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AddressResponseDTO> findAddressByCep(@Valid @RequestBody CepRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new AddressResponseDTO(cepService.findAddressByCep(request)));
    }
}

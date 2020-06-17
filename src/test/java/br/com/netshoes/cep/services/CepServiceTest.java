package br.com.netshoes.cep.services;

import br.com.netshoes.cep.client.CepClient;
import br.com.netshoes.cep.parameters.CepRequestDTO;
import br.com.netshoes.cep.presenters.AddressDataDTO;
import br.com.netshoes.cep.services.impl.CepServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

import static br.com.netshoes.cep.constants.Constants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

public class CepServiceTest {

    @Mock
    private CepClient cepClient;

    private CepService cepService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cepService = new CepServiceImpl(cepClient);
    }

    @Test
    public void testMethodFindAddressByCepWhenCepIsValidButEqualZeroThenThrowHttpStatusBadRequest() {
        final var request = CepRequestDTO.builder().cep("00000000").build();
        try {
            cepService.findAddressByCep(request);
        } catch (ResponseStatusException ex){
            assertEquals(BAD_REQUEST, ex.getStatus());
            assertEquals(ZIP_POSTAL_CODE_INVALID, ex.getReason());
        }
    }

    @Test
    public void testMethodFindAddressByCepWhenCepIsInvalidNonNumericThenThrowHttpStatusBadRequest() {
        final var request = CepRequestDTO.builder().cep("abcdefgh").build();
        try {
            cepService.findAddressByCep(request);
        } catch (ResponseStatusException ex){
            assertEquals(BAD_REQUEST, ex.getStatus());
            assertEquals(ZIP_POSTAL_CODE_INVALID, ex.getReason());
        }
    }

    @Test
    public void testMethodFindAddressByCepWhenCepIsValidButNotFoundResponseThenThrowHttpStatusNotFound() {
        final var request = CepRequestDTO.builder().cep("11111111").build();
        when(cepClient.find(any())).thenReturn(new AddressDataDTO());
        try {
            cepService.findAddressByCep(request);
        } catch (ResponseStatusException ex){
            assertEquals(NOT_FOUND, ex.getStatus());
            assertEquals(ZIP_POSTAL_CODE_NOT_FOUND, ex.getReason());
        }
    }

    @Test
    public void testMethodFindAddressByCepWhenCepIsValidAndFoundResponseThenSuccess() {
        final var request = CepRequestDTO.builder().cep("11111111").build();
        final var response = AddressDataDTO.builder()
                .localidade("Sao Paulo")
                .bairro("Vila Guarani")
                .uf("SP")
                .logradouro("Avenida Leonardo da Vinci")
                .build();
        when(cepClient.find(any())).thenReturn(response);
        final var result = cepService.findAddressByCep(request);
        assertEquals("SP", result.getUf());
    }

    @Test
    public void testMethodFindAddressByCepIfEvenSoPassedAllScenariosOfTestAndTheServiceExternReturnAnyHttpStatusNotTreatedThenThrowHttpStatusNotFound() {
        final var request = CepRequestDTO.builder().cep("11111111").build();
        doThrow(new HttpClientErrorException(INTERNAL_SERVER_ERROR)).when(cepClient).find(any());
        try {
            cepService.findAddressByCep(request);
        } catch (ResponseStatusException ex){
            assertEquals(NOT_FOUND, ex.getStatus());
            assertEquals(ZIP_POSTAL_CODE_NOT_FOUND, ex.getReason());
        }
    }

    @Test
    public void testMethodFindAddressByCepIfTheServiceExternIsUnavailableThenThrowHttpStatusBadGateway() {
        final var request = CepRequestDTO.builder().cep("11111111").build();
        doThrow(ResourceAccessException.class).when(cepClient).find(any());
        try {
            cepService.findAddressByCep(request);
        } catch (ResponseStatusException ex){
            assertEquals(BAD_GATEWAY, ex.getStatus());
            assertEquals(EXTERN_SERVICE_CEP_UNAVAILABLE, ex.getReason());
        }
    }
}

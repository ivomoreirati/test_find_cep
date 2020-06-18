package br.com.netshoes.cep.controllers;

import br.com.netshoes.cep.interceptors.HandlerController;
import br.com.netshoes.cep.parameters.CepRequestDTO;
import br.com.netshoes.cep.presenters.AddressDataDTO;
import br.com.netshoes.cep.services.CepService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class CepControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CepService cepService;

    @InjectMocks
    private CepController cepController;

    private ExceptionHandlerExceptionResolver getExceptionResolver(StaticApplicationContext context){
        ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
        resolver.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        resolver.setApplicationContext(context);
        resolver.afterPropertiesSet();
        return resolver;
    }

    @Before
    public void setUp() {
        final StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerBeanDefinition("HandlerController", new RootBeanDefinition(HandlerController.class, null));
        MockitoAnnotations.initMocks(this);
        cepController = new CepController(cepService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(cepController).setHandlerExceptionResolvers(getExceptionResolver(applicationContext)).build();
    }

    @Test
    public void testEndpointFindAddressByCepWhenCepIsValidAndResponseIsNotNullThenThrowHttpStatusCreated() throws Exception {

        final var request = CepRequestDTO.builder().cep("04313001").build();

        final var response = AddressDataDTO.builder()
                .localidade("Sao Paulo")
                .bairro("Vila Guarani")
                .uf("SP")
                .logradouro("Avenida Leonardo da Vinci")
                .build();

        when(cepService.findAddressByCep(any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/address")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.data.uf", Matchers.is("SP")));
    }

    @Test
    public void testEndpointFindAddressByCepWhenCepHaveLenghtNoEqualsEightCharactersThenThrowHttpStatusBadRequest() throws Exception {
        final var request = CepRequestDTO.builder().cep("0431300").build();
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/address")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testEndpointFindAddressByCepWhenCepIsBlankThenThrowHttpStatusBadRequest() throws Exception {
        final var request = CepRequestDTO.builder().cep("").build();
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/address")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testEndpointFindAddressByCepWhenCepIsNullThenThrowHttpStatusBadRequest() throws Exception {
        final var request = CepRequestDTO.builder().cep(null).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/address")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testEndpointFindAddressByCepWhenRequestBodyIsInvalidThenThrowHttpStatusBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/address")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content("{\n" +
                        "  \"cep\": \n" +
                        "}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}


package br.com.netshoes.cep.client;

import br.com.netshoes.cep.presenters.AddressDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "cepService", url = "${services.cep.url}")
public interface CepClient {

    @GetMapping("{cep}/json")
    AddressDataDTO find(@RequestParam("cep") String cep);
}

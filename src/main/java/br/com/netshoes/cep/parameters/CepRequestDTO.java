package br.com.netshoes.cep.parameters;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static br.com.netshoes.cep.constants.Constants.ZIP_POSTAL_CODE_INVALID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CepRequestDTO {
    @ApiModelProperty(required = true, example = "\"04313001\"")
    @NotBlank(message = ZIP_POSTAL_CODE_INVALID)
    @NotNull(message = ZIP_POSTAL_CODE_INVALID)
    @Size(min = 8, max = 8, message = ZIP_POSTAL_CODE_INVALID)
    private String cep;
}

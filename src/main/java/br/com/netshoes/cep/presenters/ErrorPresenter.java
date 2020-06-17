package br.com.netshoes.cep.presenters;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorPresenter {

	@ApiModelProperty(required = true, example = "2020-06-17T21:00:13.086Z", value = "The timestamp where the error occurred.")
	private String timestamp;

	@ApiModelProperty(example = "400", required = true, value = "The HTTP status. Must be the same status returned as request response.")
	@Valid
	@DecimalMin("100")
	@DecimalMax("599")
	private Integer status;

	@ApiModelProperty(example = "Bad Request", required = true, value = "The description of the HTTP status.")
	private String error;

	@ApiModelProperty(required = true, example = "/address")
	private String path;

	@JsonProperty
	@ApiModelProperty
	private String message;

	@Builder
	public ErrorPresenter(final HttpStatus status, final String message) {
		if (!Objects.isNull(status)) {
			this.error = status.name();
			this.status = status.value();
		}
		this.timestamp = DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now());
		this.path = getContextPath();
		this.message = message;
	}


	private static HttpServletRequest getCurrentRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
	}

	private static String getContextPath() {
		return getCurrentRequest().getContextPath() + getCurrentRequest().getServletPath();
	}
}

package br.com.netshoes.cep.config;

import com.google.common.collect.Sets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static io.swagger.models.Scheme.HTTP;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {

    private final Environment environment;

    public SwaggerConfig(final Environment environment) {
        this.environment = environment;
    }

    @Bean
    public Docket swaggerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .directModelSubstitute(ResponseEntity.class, Void.class)
                .directModelSubstitute(Object.class, Void.class)
                .select()
                .apis(
                        RequestHandlerSelectors.basePackage(environment
                                .getProperty("swagger.api.controller.base-package")))
                .paths(PathSelectors.ant("/**"))
                .build()
                .consumes(Sets.newHashSet(APPLICATION_JSON_VALUE))
                .produces(Sets.newHashSet(APPLICATION_JSON_VALUE))
                .protocols(Sets.newHashSet(HTTP.name()))
                .apiInfo(metaData());
    }

    private Contact createContact() {
        return new Contact(null, null, environment.getProperty("swagger.api.contact.email"));
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .license(environment.getProperty("swagger.api.license.name"))
                .licenseUrl(environment.getProperty("swagger.api.license.url"))
                .contact(createContact())
                .title(environment.getProperty("swagger.api.title"))
                .description(environment.getProperty("swagger.api.description"))
                .termsOfServiceUrl(environment.getProperty("swagger.api.termsOfServiceUrl"))
                .version(environment.getProperty("swagger.api.version")).build();
    }


    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
package jo.aspire.task.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jo.aspire.task.controller.AppController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class AppConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(AppController.class.getPackage().getName()))
                .paths(regex("/api.*"))
                .build();
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
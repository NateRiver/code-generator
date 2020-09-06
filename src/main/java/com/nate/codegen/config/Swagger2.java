package com.nate.codegen.config;

import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger configuration
 *
 * @author Nate
 */
@Configuration
@EnableSwagger2
public class Swagger2 {

    @Autowired
    private Environment env;

    /**
     * Docket config
     *
     * @return instance of Docket
     */
    @Bean
    public Docket createRestApi() {
        Predicate<RequestHandler> predicate = new Predicate<RequestHandler>() {

            @Override
            public boolean apply(RequestHandler input) {
                Class<?> clazz = input.declaringClass();
                if (clazz == BasicErrorController.class) {
                    return false;
                }
                if (clazz.isAnnotationPresent(RestController.class)) {
                    return true;
                }
                if (clazz.isAnnotationPresent(Controller.class)) {
                    return true;
                }
                return false;
            }
        };
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().apis(predicate)
                .paths(PathSelectors.any()).build();
    }

    /**
     * ApiInfo config
     *
     * @return instance of ApiInfo
     */
    private ApiInfo apiInfo() {
        Contact contact = new Contact("Nate", "", "socode@foxmail.com");
        return new ApiInfoBuilder().title(env.getProperty("spring.application.name")).description("")
                .contact(contact).version("1.0").build();
    }
}

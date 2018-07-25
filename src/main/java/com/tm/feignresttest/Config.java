package com.tm.feignresttest;


import com.tm.feignresttest.client.ExternalNameServiceClient;
import feign.Feign;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public ExternalNameServiceClient externalNameServiceClient() {
        return Feign.builder().target(ExternalNameServiceClient.class, "http://localhost:8080/external-service/api");
    }

}

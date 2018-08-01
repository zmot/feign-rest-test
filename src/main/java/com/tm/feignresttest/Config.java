package com.tm.feignresttest;


import com.tm.feignresttest.client.ExternalNameServiceClient;
import feign.Feign;
import feign.Response;
import feign.RetryableException;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class Config {
    @Bean
    public ExternalNameServiceClient externalNameServiceClient() {
        return Feign.builder().errorDecoder(new TeapotErrorDecoder()).retryer(new MyRetryer()).target(ExternalNameServiceClient.class, "http://localhost:8085/external-service/api");
    }

    private static class MyRetryer extends Retryer.Default {

    }

    private static class TeapotErrorDecoder extends ErrorDecoder.Default {
        @Override
        public Exception decode(String methodKey, Response response) {
            return response.status() == HttpStatus.I_AM_A_TEAPOT
                    .value() ? new RetryableException("I'M A TEAPOT!", null) : super.decode(methodKey, response);
        }
    }
}

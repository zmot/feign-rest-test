package com.tm.feignresttest.client;

import feign.RequestLine;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(name = "externalName")
public interface ExternalNameServiceClient {
    @RequestLine("GET /name")
    String getName();
}

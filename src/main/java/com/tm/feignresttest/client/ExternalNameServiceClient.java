package com.tm.feignresttest.client;

import feign.RequestLine;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "externalName")
public interface ExternalNameServiceClient {
    @RequestMapping(method = RequestMethod.GET, value = "/name")
    @RequestLine("GET /name")
    String getName();
}

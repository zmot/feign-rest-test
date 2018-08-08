package com.tm.feignresttest.service;

import com.tm.feignresttest.client.ExternalNameServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NameService {
    private final ExternalNameServiceClient externalNameServiceClient;

    @Autowired
    public NameService(ExternalNameServiceClient externalNameServiceClient) {
        this.externalNameServiceClient = externalNameServiceClient;
    }

    String getName() {
        return externalNameServiceClient.getName();
    }
}


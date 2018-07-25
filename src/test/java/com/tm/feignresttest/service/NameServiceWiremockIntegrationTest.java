package com.tm.feignresttest.service;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.tm.feignresttest.App;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NameServiceWiremockIntegrationTest {

    @ClassRule
    public static final WireMockClassRule WIREMOCK = new WireMockClassRule();

    @Autowired
    private NameService nameService;

    @Test
    public void getName() {
        WIREMOCK.stubFor(get(urlEqualTo("/external-service/api/name"))
                .willReturn(aResponse().withBody("some response body")));

        assertThat(nameService.getName(), is("some response body"));
    }
}
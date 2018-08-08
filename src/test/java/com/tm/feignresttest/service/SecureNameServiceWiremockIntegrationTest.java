package com.tm.feignresttest.service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("https")
public class SecureNameServiceWiremockIntegrationTest {
    @Rule
    public WireMockRule wiremock = new WireMockRule(wireMockConfig().httpsPort(8443));

    @Autowired
    private NameService secureNameService;

    @Test
    public void shouldUseHttps() {

        wiremock.stubFor(get(urlEqualTo("/external-service/api/name"))
                .willReturn(aResponse().withStatus(200).withBody("wiremock")));

        assertThat(secureNameService.getName(), is("wiremock"));
    }
}
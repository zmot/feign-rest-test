package com.tm.feignresttest.service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.Rule;
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

    @Rule
    public final WireMockRule WIREMOCK = new WireMockRule(8085);

    @Autowired
    private NameService nameService;

    @Test
    public void shouldReturnName() {
        WIREMOCK.stubFor(get(urlEqualTo("/external-service/api/name"))
                .willReturn(aResponse().withStatus(200).withBody("wiremock")));

        assertThat(nameService.getName(), is("wiremock"));

        WIREMOCK.verify(1, getRequestedFor(urlEqualTo("/external-service/api/name")));
    }

    @Test
    public void shouldReturnOneNameThenOtherName() {
        WIREMOCK.stubFor(get(urlEqualTo("/external-service/api/name"))
                .inScenario("scenario A")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(200).withBody("wiremock"))
                .willSetStateTo("state X"));

        WIREMOCK.stubFor(get(urlEqualTo("/external-service/api/name"))
                .inScenario("scenario A")
                .whenScenarioStateIs("state X")
                .willReturn(aResponse().withStatus(200).withBody("other wiremock")));

        assertThat(nameService.getName(), is("wiremock"));
        assertThat(nameService.getName(), is("other wiremock"));

        WIREMOCK.verify(2, getRequestedFor(urlEqualTo("/external-service/api/name")));
    }

    @Test
    public void shouldRetryOnTeapotStatusAndSucceed() {
        WIREMOCK.stubFor(get(urlEqualTo("/external-service/api/name"))
                .inScenario("scenario B")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(418))
                .willSetStateTo("after teapot"));

        WIREMOCK.stubFor(get(urlEqualTo("/external-service/api/name"))
                .inScenario("scenario B")
                .whenScenarioStateIs("after teapot")
                .willReturn(aResponse().withStatus(200).withBody("wiremock")));

        assertThat(nameService.getName(), is("wiremock"));

        WIREMOCK.verify(2, getRequestedFor(urlEqualTo("/external-service/api/name")));
    }
}
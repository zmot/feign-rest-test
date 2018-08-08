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
    public final WireMockRule server = new WireMockRule(8085);

    @Autowired
    private NameService nameService;

    @Test
    public void shouldReturnName() {
        server.stubFor(get(urlEqualTo("/external-service/api/name"))
                .willReturn(aResponse().withStatus(200).withBody("wiremock")));

        assertThat(nameService.getName(), is("wiremock"));

        server.verify(1, getRequestedFor(urlEqualTo("/external-service/api/name")));
    }

    @Test
    public void shouldReturnOneNameThenOtherName() {
        server.stubFor(get(urlEqualTo("/external-service/api/name"))
                .inScenario("scenario A")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(200).withBody("wiremock"))
                .willSetStateTo("state X"));

        server.stubFor(get(urlEqualTo("/external-service/api/name"))
                .inScenario("scenario A")
                .whenScenarioStateIs("state X")
                .willReturn(aResponse().withStatus(200).withBody("other server")));

        assertThat(nameService.getName(), is("wiremock"));
        assertThat(nameService.getName(), is("other server"));

        server.verify(2, getRequestedFor(urlEqualTo("/external-service/api/name")));
    }

    @Test
    public void shouldRetryOnTeapotStatusAndSucceed() {
        server.stubFor(get(urlEqualTo("/external-service/api/name"))
                .inScenario("scenario B")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(418))
                .willSetStateTo("after teapot"));

        server.stubFor(get(urlEqualTo("/external-service/api/name"))
                .inScenario("scenario B")
                .whenScenarioStateIs("after teapot")
                .willReturn(aResponse().withStatus(200).withBody("wiremock")));

        assertThat(nameService.getName(), is("wiremock"));

        server.verify(2, getRequestedFor(urlEqualTo("/external-service/api/name")));
    }
}
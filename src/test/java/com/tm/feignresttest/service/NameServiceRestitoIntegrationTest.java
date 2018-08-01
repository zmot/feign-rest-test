package com.tm.feignresttest.service;

import com.xebialabs.restito.server.StubServer;
import com.xebialabs.restito.support.junit.StartServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.*;
import static com.xebialabs.restito.semantics.ActionSequence.sequence;
import static com.xebialabs.restito.semantics.Condition.get;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NameServiceRestitoIntegrationTest {
    @Rule
    public final StartServer serverRule = new StartServer(8085);
    private final StubServer server = serverRule.getServer();

    @Autowired
    private NameService nameService;

    @Test
    public void shouldReturnName() {
        whenHttp(server).match(get("/external-service/api/name"))
                .then(status(HttpStatus.OK_200), stringContent("restito"));

        assertThat(nameService.getName(), is("restito"));

        verifyHttp(server).once(get("/external-service/api/name"));
    }

    @Test
    public void shouldReturnOneNameThenOtherName() {
        whenHttp(server).match(get("/external-service/api/name"))
                .then(sequence(
                        composite(status(HttpStatus.OK_200), stringContent("restito")),
                        composite(status(HttpStatus.OK_200), stringContent("other restito"))
                ));

        assertThat(nameService.getName(), is("restito"));
        assertThat(nameService.getName(), is("other restito"));

        verifyHttp(server).times(2, get("/external-service/api/name"));
    }

    @Test
    public void shouldRetryOnTeapotStatusAndSucceed() {
        whenHttp(server).match(get("/external-service/api/name"))
                .then(sequence(
                        composite(status(HttpStatus.getHttpStatus(418))),
                        composite(status(HttpStatus.OK_200), stringContent("restito"))
                ));

        assertThat(nameService.getName(), is("restito"));

        verifyHttp(server).times(2, get("/external-service/api/name"));
    }
}
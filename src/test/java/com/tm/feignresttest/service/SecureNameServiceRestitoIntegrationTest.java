package com.tm.feignresttest.service;

import com.xebialabs.restito.server.StubServer;
import com.xebialabs.restito.support.junit.StartServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.get;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("https")
public class SecureNameServiceRestitoIntegrationTest {
    @Rule
    public final StartServer serverRule = new StartServer(8443);
    private final StubServer server = serverRule.getServer().secured();

    @Autowired
    private NameService secureNameService;

    @Test
    public void shouldUseHttps() {
        whenHttp(server).match(get("/external-service/api/name"))
                .then(status(HttpStatus.OK_200), stringContent("restito"));

        assertThat(secureNameService.getName(), is("restito"));
    }
}
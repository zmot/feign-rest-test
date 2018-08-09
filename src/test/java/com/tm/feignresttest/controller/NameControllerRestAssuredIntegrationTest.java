package com.tm.feignresttest.controller;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.get;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class NameControllerRestAssuredIntegrationTest {

    @Before
    public void setUp() {
        RestAssured.port = 8081;
    }

    @Test
    public void shouldReturnWord() {
        get("/word").then().assertThat().content(is("word"));
    }
}
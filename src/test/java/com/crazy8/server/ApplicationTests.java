package com.crazy8.server;

import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectDirectories;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@Suite
@CucumberContextConfiguration
@SelectDirectories({"src/test/java/com/crazy8", "src/main/resources/cucumber"})
@SpringBootTest(properties = {"server.port-8080"}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext
class ApplicationTests {

}

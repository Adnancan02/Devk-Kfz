package com.devk.test.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue     = "com.devk.test.steps",
        plugin   = {
                "pretty",
                "json:build/reports/cucumber.json"
        }
)
public class TestRunner {

}
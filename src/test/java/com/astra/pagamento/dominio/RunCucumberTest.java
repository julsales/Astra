package com.astra.pagamento.dominio;

import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("com/astra/pagamento/dominio")
@ConfigurationParameter(key = "cucumber.glue", value = "com.astra.pagamento.dominio")
@ConfigurationParameter(key = "cucumber.plugin", value = "pretty, html:target/cucumber-reports/pagamento-cucumber.html")
public class RunCucumberTest {
}

package com.astra.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@CucumberContextConfiguration
@SpringBootTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MYSQL",
    "spring.jpa.show-sql=false",
    "spring.sql.init.mode=never",
    "spring.jpa.defer-datasource-initialization=false"
})
public class CucumberSpringConfiguration {
}

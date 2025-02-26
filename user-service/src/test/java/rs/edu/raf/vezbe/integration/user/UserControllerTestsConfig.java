package rs.edu.raf.vezbe.integration.user;

// Objasnjenje anotacija:
//  - @CucumberContextConfiguration: Konfigurise Cucumber kontekst koji se koristi za pokretanje testova
//  - @SpringBootTest: pokrece celukpni Spring Application Context za potrebe testiranja
//  - @AutoConfigureMockMvc - konfigurise MockMvc servis za slanje HTTP zahteva

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTestsConfig {

}

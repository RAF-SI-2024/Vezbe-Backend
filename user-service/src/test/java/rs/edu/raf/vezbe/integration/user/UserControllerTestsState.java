package rs.edu.raf.vezbe.integration.user;

import io.cucumber.spring.ScenarioScope;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

// State sadrzi stanje testa koje zelimo da delimo izmedju razlicitih koraka.
// @ScenarioScope anotacija se koriste ukoliko zelite da se ova klasa unisti nakon zavrsetka jednog scenarija.
// Ukoliko zelite da delite state izmedju razlicitih scenarija, mozete da obrisete ovu anotaciju, mada se to
// bas ne preporucuje!

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@ScenarioScope
public class UserControllerTestsState {

    String jwtToken;

}

package rs.edu.raf.vezbe.integration.user;

// Ova klasa sadrzi Glue kod, tj. kod koji spaja Cucumber korake sa Java kodom.
// Svaki metod u ovoj klasi se mapira na jedan korak iz .feature fajlova.

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import rs.edu.raf.vezbe.dto.UserDto;
import rs.edu.raf.vezbe.form.LoginResponseForm;
import rs.edu.raf.vezbe.form.UserCreateForm;
import rs.edu.raf.vezbe.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTestsSteps extends UserControllerTestsConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserControllerTestsState userControllerTestsState;

    /**
     * Scenario "Ispravna registracija korisnika"
     */

    @Given("korisnik ne postoji")
    public void korisnik_ne_postoji() {
        Optional<UserDto> userDto = userService.getUser("pera");
        if(userDto.isPresent()) {
            try {
                userService.deleteUser(userDto.get().getUsername());
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }
    @When("korisnik se registruje sa ispravnim podacima")
    public void korisnik_se_registruje_sa_ispravnim_podacima() {
        UserCreateForm userCreateForm = new UserCreateForm();
        userCreateForm.setUsername("pera");
        userCreateForm.setImePrezime("Pera Peric");
        userCreateForm.setPassword("123456");
        userCreateForm.setIsAdmin(Boolean.FALSE);

        try {
            UserDto userDto = userService.createUser(userCreateForm);
            assertEquals(userDto.getUsername(), userCreateForm.getUsername());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Then("korisnik je uspesno registrovan")
    public void korisnik_je_uspesno_registrovan() {
        Optional<UserDto> userDto = userService.getUser("pera");
        if(userDto.isEmpty()) {
            fail("user does not exist");
        }
        assertEquals("pera", userDto.get().getUsername());
        assertEquals("Pera Peric", userDto.get().getImePrezime());
        assertEquals(Boolean.FALSE, userDto.get().getIsAdmin());
    }

    /**
     * Scenario "Ispravna registracija korisnika sa Cucumber parametrima"
     * Scenario "Ispravna registracija drugog korisnika sa Cucumber parametrima"
     */

    @Given("korisnik {string} ne postoji")
    public void korisnik_ne_postoji(String username) {
        Optional<UserDto> userDto = userService.getUser(username);
        if(userDto.isPresent()) {
            try {
                userService.deleteUser(userDto.get().getUsername());
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }
    @When("korisnik {string} se registruje sa imenom {string} i lozinkom {string}")
    public void korisnik_se_registruje_sa_imenom_i_lozinkom(String username, String imePrezime, String password) {
        UserCreateForm userCreateForm = new UserCreateForm();
        userCreateForm.setUsername(username);
        userCreateForm.setImePrezime(imePrezime);
        userCreateForm.setPassword(password);
        userCreateForm.setIsAdmin(Boolean.FALSE);

        try {
            UserDto userDto = userService.createUser(userCreateForm);
            assertEquals(userDto.getUsername(), userCreateForm.getUsername());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Then("korisnik {string} je uspesno registrovan")
    public void korisnik_je_uspesno_registrovan(String username) {
        Optional<UserDto> userDto = userService.getUser(username);
        if(userDto.isEmpty()) {
            fail("user does not exist");
        }
        assertEquals(username, userDto.get().getUsername());
    }

    /**
     * Scenario "Registracija korisnika putem API-a"
     */

    @Given("putem API-a proveravamo da li korisnik {string} vec registrovan")
    public void putem_api_a_proveravamo_da_li_korisnik_vec_registrovan(String username) {
        try {
            // KORAK 1: logujemo se na sistem i smestamo JWT token u state
            ResultActions resultActions = mockMvc.perform(
                    post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .content("{\"username\":\"admin\",\"password\":\"admin\"}")
            ).andExpect(status().isOk());

            MvcResult mvcResult = resultActions.andReturn();

            String loginResponse = mvcResult.getResponse().getContentAsString(); // {"jwt": "..."}
            LoginResponseForm loginResponseForm = objectMapper.readValue(loginResponse, LoginResponseForm.class);
            userControllerTestsState.setJwtToken(loginResponseForm.getJwt());

            // KORAK 2: proveravamo da li korisnik postoji
            // U andExpect delu proveravamo status odgovora:
            // - ako je status 200 (OK), korisnik postoji i treba da ga obrisemo
            // - ako je status 404 (NOT FOUND), korisnik ne postoji i mozemo da nastavimo dalje
            // - ako je bilo koji drugi status, nesto je lose sa serverom i treba da prekinemo test (fail)
            mockMvc.perform(
                    get("/api/username/" + username)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + userControllerTestsState.getJwtToken())
            )
            .andExpect(result -> {
                if(result.getResponse().getStatus() == HttpStatus.OK.value()) {
                    mockMvc.perform(
                        delete("/api/username/" + username)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .header("Authorization", "Bearer " + userControllerTestsState.getJwtToken())
                    ).andExpect(status().isOk());
                } else if(result.getResponse().getStatus() != HttpStatus.NOT_FOUND.value()) {
                    fail("error with the server");
                }
            });
        } catch (Exception e) {
           fail(e.getMessage());
        }
    }
    @When("korisnik {string} poziva API za registraciju sa imenom i prezimenom {string} i lozinkom {string}")
    public void korisnik_poziva_api_za_registraciju_sa_imenom_i_prezimenom_i_lozinkom(String username, String imePrezime, String password) {
        UserCreateForm userCreateForm = new UserCreateForm();
        userCreateForm.setUsername(username);
        userCreateForm.setImePrezime(imePrezime);
        userCreateForm.setPassword(password);
        userCreateForm.setIsAdmin(Boolean.FALSE);

        try {
            // Konvertujemo UserCreateForm u JSON string, zatim saljemo taj string API-u i proveravamo odgovor
            // tako sto parsiramo JSON string u UserDto objekat i proveravamo da li se username poklapa sa prosledjenim
            String jsonUserCreateForm = objectMapper.writeValueAsString(userCreateForm);

            ResultActions resultActions = mockMvc.perform(
                    post("/api")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonUserCreateForm)
                            .header("Authorization", "Bearer " + userControllerTestsState.getJwtToken())
            ).andExpect(status().isOk());

            MvcResult mvcResult = resultActions.andReturn();

            String jsonUserDto = mvcResult.getResponse().getContentAsString();
            UserDto userDto = objectMapper.readValue(jsonUserDto, UserDto.class);
            assertEquals(userDto.getUsername(), userCreateForm.getUsername());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Then("korisnik provera putem API-a da li je korisnik {string} uspesno registrovan")
    public void korisnik_provera_putem_api_a_da_li_je_korisnik_uspesno_registrovan(String username) {
        try {
            // Na kraju provedemo proveru da li je korisnik uspesno registrovan tako sto pozovemo API za dohvatanje
            // podataka o korisniku
            ResultActions resultActions = mockMvc.perform(
                    get("/api/username/" + username)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + userControllerTestsState.getJwtToken())
            ).andExpect(status().isOk());

            MvcResult mvcResult = resultActions.andReturn();

            String jsonUserDto = mvcResult.getResponse().getContentAsString();
            UserDto userDto = objectMapper.readValue(jsonUserDto, UserDto.class);
            assertEquals(username, userDto.getUsername());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}

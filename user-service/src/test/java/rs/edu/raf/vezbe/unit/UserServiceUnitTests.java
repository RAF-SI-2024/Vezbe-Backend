package rs.edu.raf.vezbe.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;
import rs.edu.raf.vezbe.dto.UserDto;
import rs.edu.raf.vezbe.form.UserCreateForm;
import rs.edu.raf.vezbe.model.User;
import rs.edu.raf.vezbe.repository.UserRepository;
import rs.edu.raf.vezbe.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

// Bitno je da vam klasa sa Unit testovima bude anotirana sa ovom anotacijom kako bi Mockito radio kako treba.
@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTests {

    // Klasa koju mockujemo (mozemo imati vise klasa koje mockujemo).
    //
    // VAZNA NAPOMENA: @Mock mockuje **celu klasu**, tj. pravi objekat cije sve metode vracaju null ili ne rade nista.
    // To znaci da svaku metodu koju pozivamo nad mockovanim objektom prvo moramo da mockujuemo, tj. da sa "given" i
    // "willReturn" definisemo kako ce se ta metoda ponasati.
    // Postoji i "parcijalni mock" za koji se koristi "@Spy" anotacija.
    @Mock
    private UserRepository userRepository;

    // Klasa u koju ubacujemo mockovane objekte (mozemo imati vise ovakvih klasa).
    @InjectMocks
    private UserServiceImpl userService;

    // VAZNA NAPOMENA: Svaka test funkcija mora biti anotirana sa "@Test" kako biste mogli da pokrenete taj test!
    @Test
    void testListUsers() {
        // KORAK 1 (INPUT TESTA): Koje objekte cemo dati funkciji koju testiramo.
        User user1 = new User();
        user1.setUsername("Test");
        user1.setPassword("Test");
        user1.setImePrezime("Test Test");
        user1.setIsAdmin(false);

        User user2 = new User();
        user2.setUsername("pera");
        user2.setPassword("1234567");
        user2.setImePrezime("Pera Peric");
        user2.setIsAdmin(false);

        List<User> users = List.of(user1, user2);

        // KORAK 2: Mockujemo metode koje rade sa eksternim servisima, npr. bazom podataka.
        // VAZNA NAPOMENA: Importovati "given" metodu iz Mockito biblioteke!
        given(userRepository.findAll()).willReturn(users);

        // KORAK 3: Pozivamo metodu koju zelimo da testiramo.
        List<UserDto> userDtos = userService.listUsers();

        // KORAK 4: Proveramo da li smo dobili ocekivani rezultat.
        // Mozemo vise puta ponoviti korake 3 i 4.
        // VAZNA NAPOMENA: Importovati "assert*" metode iz JUnit/Jupiter biblioteke!
        for (UserDto udto : userDtos) {
            boolean found = false;
            for (User u : users) {
                if (udto.getUsername().equals(u.getUsername())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                fail("User not found");
            }
        }
    }

    @Test
    void testGetUser() {
        // KORAK 1 (INPUT TESTA): Koje objekte cemo dati funkciji koju testiramo.
        User user1 = new User();
        user1.setUsername("Test");
        user1.setPassword("Test");
        user1.setImePrezime("Test Test");
        user1.setIsAdmin(false);

        // KORAK 2: Mockujemo metode koje rade sa eksternim servisima, npr. bazom podataka.
        given(userRepository.findUserByUsername("Test")).willReturn(Optional.of(user1));

        try {
            // KORAK 3: Pozivamo metodu koju zelimo da testiramo.
            Optional<UserDto> dto = userService.getUser("Test");
            if(dto.isEmpty()) {
                fail("User not found");
            }

            // KORAK 4: Proveramo da li smo dobili ocekivani rezultat.
            assertEquals(dto.get().getUsername(), user1.getUsername());
            assertEquals(dto.get().getImePrezime(), user1.getImePrezime());
            assertEquals(dto.get().getIsAdmin(), user1.getIsAdmin());
        } catch (Exception e) {
            // Ako dodje do exception-a, failujemo test.
            //
            // VAZNA NAPOMENA: Ovde korisiti fail umesto da throw-ujete exception, inace ce test proci,
            // a on zapravo ne radi kako treba.
            //
            // VAZNA NAPOMENA: Importovati "fail" metodu iz JUnit/Jupiter biblioteke!
            fail(e.getMessage());
        }
    }

    @Test
    void testCreateUser() {
        // KORAK 1 (INPUT TESTA): Koje objekte cemo dati funkciji koju testiramo.
        UserCreateForm userCreateForm = new UserCreateForm();
        userCreateForm.setUsername("test");
        userCreateForm.setPassword("123456");
        userCreateForm.setImePrezime("Test Test");
        userCreateForm.setIsAdmin(true);

        // KORAK 2: Mockujemo metode koje rade sa eksternim servisima, npr. bazom podataka.
        //
        // VAZNA NAPOMENA: Ovde imamo primer mockovanja staticke metode (za to morate da imate ukljucen
        // odgovarajuci dependency u pom.xml).
        //
        // VAZNA NAPOMENA: Na vezbama odrzanim u subotu, 9.3.2024. objasnjeno je zasto mockujemo gensalt() metodu.
        // Ukratko, da bi unit testovi radili kako treba, bitno je postici determinizam.
        // To znaci da ako u metodi koju testirate pozivate neku drugu metodu koja ne vraca uvek isti rezultat
        // (npr. vraca random broj/string), tu drugu metodu morate da mockujete. Inace, poziv metode koju testirate
        // ce vratiti drugaciji objekat u odnosu na onaj koji ocekujete, pa ce "assert" metoda da izbaci gresku.
        // U nasem slucaju, "gensalt" metoda je ta koja vraca random string, pa smo nju mockovali.
        // Takodje, obratiti paznju da nismo mockovali celu "hashpw" metodu, vec samo "gensalt" zato sto je
        // "hashpw" metoda deterministcka.
        String salt = "123";
        try (MockedStatic<BCrypt> bc = Mockito.mockStatic(BCrypt.class)) {
            bc.when(BCrypt::gensalt).thenReturn(salt);
            String hashPW = BCrypt.hashpw(userCreateForm.getPassword(), salt);

            // KORAK 3 (OUTPUT TESTA): Koje objekte ocekujemo da nam test vrati.
            User user = new User();
            user.setUsername(userCreateForm.getUsername());
            user.setImePrezime(userCreateForm.getImePrezime());
            user.setIsAdmin(userCreateForm.getIsAdmin());
            user.setPassword(hashPW);

            // KORAK 4: Ponovo mozemo da mockujemo metode koje treba da mockujemo.
            given(userRepository.save(user)).willReturn(user);

            try {
                // KORAK 5: Pozivamo metodu koju zelimo da testiramo.
                UserDto dto = userService.createUser(userCreateForm);

                // KORAK 6: Proveramo da li smo dobili ocekivani rezultat.
                assertEquals(userCreateForm.getUsername(), dto.getUsername());
                assertEquals(userCreateForm.getImePrezime(), dto.getImePrezime());
                assertEquals(userCreateForm.getIsAdmin(), dto.getIsAdmin());
            } catch (Exception e) {
                // KORAK 7: failujemo test ako smo dobili exception.
                fail(e.getMessage());
            }
        }
    }

    @Test
    void testEditUser() {
        // KORAK 1 (INPUT TESTA): Koje objekte cemo dati funkciji koju testiramo.
        User beforeEditUser = new User();
        beforeEditUser.setUsername("test");
        beforeEditUser.setImePrezime("Test Test");
        beforeEditUser.setIsAdmin(false);

        UserCreateForm editUserCreateForm = new UserCreateForm();
        editUserCreateForm.setUsername("test");
        editUserCreateForm.setPassword("123456");
        editUserCreateForm.setImePrezime("Test Test");
        editUserCreateForm.setIsAdmin(true);

        // KORAK 2 (OUTPUT TESTA): Koje objekte ocekujemo da nam test vrati.
        User afterEditUser = new User();
        afterEditUser.setUsername("test");
        afterEditUser.setImePrezime("Test Test");
        afterEditUser.setIsAdmin(true);

        // KORAK 3a: Mockujemo metode koje rade sa eksternim servisima, npr. bazom podataka.
        String salt = "123";
        try (MockedStatic<BCrypt> bc = Mockito.mockStatic(BCrypt.class)) {
            bc.when(BCrypt::gensalt).thenReturn(salt);
            String hashPW = BCrypt.hashpw(editUserCreateForm.getPassword(), salt);
            beforeEditUser.setPassword(hashPW);
            afterEditUser.setPassword(hashPW);

            // KORAK 3b: Ponovo mozemo da mockujemo metode koje treba da mockujemo.
            given(userRepository.findUserByUsername(editUserCreateForm.getUsername())).willReturn(Optional.of(beforeEditUser));
            given(userRepository.save(afterEditUser)).willReturn(afterEditUser);

            try {
                // KORAK 4: Pozivamo metodu koju zelimo da testiramo.
                UserDto dto = userService.editUser(editUserCreateForm);

                // KORAK 5: Proveramo da li smo dobili ocekivani rezultat.
                assertEquals(editUserCreateForm.getUsername(), dto.getUsername());
                assertNotEquals(false, dto.getIsAdmin());
            } catch (Exception e) {
                // KORAK 6: failujemo test ako smo dobili exception.
                fail(e.getMessage());
            }
        }
    }

    @Test
    void testDeleteUser() {
        // KORAK 1 (INPUT TESTA): Koje objekte cemo dati funkciji koju testiramo.
        User user = new User();
        user.setUsername("test");
        user.setImePrezime("Test Test");
        user.setIsAdmin(false);

        // KORAK 2: Mockujemo metode koje rade sa eksternim servisima, npr. bazom podataka.
        // VAZNA NAPOMENA: Za void metode se koristi doNothing() iz Mockito biblioteke kao na nacin prikazan ispod.
        given(userRepository.findUserByUsername(user.getUsername())).willReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        try {
            // KORAK 3: Pozivamo metodu koju zelimo da testiramo.
            UserDto dto = userService.deleteUser(user.getUsername());

            // KORAK 4: Proveramo da li smo dobili ocekivani rezultat.
            assertEquals(dto.getUsername(), user.getUsername());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testIsAdmin() {
        // KORAK 1 (INPUT TESTA): Koje objekte cemo dati funkciji koju testiramo.
        User user1 = new User();
        user1.setUsername("pera");
        user1.setImePrezime("Pera Peric");
        user1.setIsAdmin(false);

        User user2 = new User();
        user2.setUsername("mika");
        user2.setImePrezime("Mika Mikic");
        user2.setIsAdmin(true);

        // KORAK 2: Mockujemo metode koje rade sa eksternim servisima, npr. bazom podataka.
        // VAZNA NAPOMENA: Mozemo istu metodu da mockujemo vise puta za razlicite argumente.
        given(userRepository.findUserByUsername("pera")).willReturn(Optional.of(user1));
        given(userRepository.findUserByUsername("mika")).willReturn(Optional.of(user2));

        try {
            // KORAK 3: Pozivamo metodu koju zelimo da testiramo.
            Boolean peraAdmin = userService.isAdmin("pera");

            // KORAK 4: Proveramo da li smo dobili ocekivani rezultat.
            assertNotNull(peraAdmin);
            assertEquals(peraAdmin, false);

            // Mozemo vise puta ponoviti korake 3 i 4.
            Boolean mikaAdmin = userService.isAdmin("mika");

            assertNotNull(mikaAdmin);
            assertEquals(mikaAdmin, true);
        } catch (Exception e) {
            // KORAK 5: failujemo test ako smo dobili exception.
            fail(e.getMessage());
        }
    }

    // VAZNA NAPOMENA: Ispod su primeri testova koji testiraju metode koje za nepravilni input bacaju exception.

    @Test
    void testCreateUserInvalidForm() {
        // KORAK 1 (INPUT TESTA): Koje objekte cemo dati funkciji koju testiramo.
        UserCreateForm userCreateForm = new UserCreateForm();
        userCreateForm.setUsername("");
        userCreateForm.setPassword("");
        userCreateForm.setImePrezime("");
        userCreateForm.setIsAdmin(true);

        // KORAK 2: Pozivamo metodu koju zelimo da testiramo i proveravamo da li baca Exception koji ocekujemo.
        assertThrows(Exception.class, () -> userService.createUser(userCreateForm));
    }

    @Test
    void testGetUserEmpty() {
        // KORAK 1 (INPUT TESTA): Koje objekte cemo dati funkciji koju testiramo.
        // Obratiti paznju da ovde vracamo null kako bi kasnije izazvali Exception.
        given(userRepository.findUserByUsername("test")).willReturn(null);

        // KORAK 2: Pozivamo metodu koju zelimo da testiramo i proveravamo da li baca Exception koji ocekujemo.
        assertThrows(Exception.class, () -> userService.getUser("test"));
    }

    @Test
    void testEditUserEmpty() {
        // KORAK 1 (INPUT TESTA): Koje objekte cemo dati funkciji koju testiramo.
        UserCreateForm userCreateForm = new UserCreateForm();
        userCreateForm.setUsername("test");

        // KORAK 2: Mockujemo metode koje rade sa eksternim servisima, npr. bazom podataka.
        // Obratiti paznju da ovde vracamo null kako bi kasnije izazvali Exception.
        given(userRepository.findUserByUsername(userCreateForm.getUsername())).willReturn(null);

        // KORAK 3: Pozivamo metodu koju zelimo da testiramo i proveravamo da li baca Exception koji ocekujemo.
        assertThrows(Exception.class, () -> userService.editUser(userCreateForm));
    }

    @Test
    void testIsAdminEmpty() {
        // KORAK 1: Mockujemo metode koje rade sa eksternim servisima, npr. bazom podataka.
        // Obratiti paznju da ovde vracamo null kako bi kasnije izazvali Exception.
        given(userRepository.findUserByUsername("test")).willReturn(null);

        // KORAK 2: Pozivamo metodu koju zelimo da testiramo i proveravamo da li baca Exception koji ocekujemo.
        assertThrows(Exception.class, () -> userService.isAdmin("test"));
    }

}

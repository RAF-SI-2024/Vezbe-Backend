package rs.edu.raf.vezbe.bootstrap;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.edu.raf.vezbe.model.User;
import rs.edu.raf.vezbe.repository.UserRepository;

import java.util.Optional;

@Component
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public BootstrapData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Loading Data...");

        Optional<User> findUser = userRepository.findUserByUsername("admin");
        if(findUser.isEmpty()) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(this.passwordEncoder.encode("admin"));
            user.setImePrezime("RAF Admin");
            user.setIsAdmin(true);

            this.userRepository.save(user);

            System.out.println("Data loaded!");
        }

        try {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(this.passwordEncoder.encode("admin"));
            user.setImePrezime("RAF Admin");
            user.setIsAdmin(true);

            this.userRepository.save(user);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Data loaded!");
    }
}

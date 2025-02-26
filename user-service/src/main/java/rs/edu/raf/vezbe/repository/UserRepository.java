package rs.edu.raf.vezbe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.edu.raf.vezbe.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

}

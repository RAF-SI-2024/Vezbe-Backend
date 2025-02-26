package rs.edu.raf.vezbe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import rs.edu.raf.vezbe.dto.UserDto;
import rs.edu.raf.vezbe.form.UserCreateForm;
import rs.edu.raf.vezbe.model.User;
import rs.edu.raf.vezbe.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> listUsers() {
        return userRepository.findAll().stream().map(this::convertUserToDto).collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> getUser(String username) {
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        return userOpt.map(this::convertUserToDto);
    }

    @Override
    public UserDto createUser(UserCreateForm userCreateForm) throws Exception {
        if (userCreateForm.getUsername().isBlank() ||
                userCreateForm.getPassword().isBlank() ||
                userCreateForm.getImePrezime().isBlank() ||
                userCreateForm.getIsAdmin() == null) {
            throw new Exception("user is missing data");
        }

        User user = new User();
        user.setUsername(userCreateForm.getUsername());
        user.setImePrezime(userCreateForm.getImePrezime());
        user.setIsAdmin(userCreateForm.getIsAdmin());

        String hashPW = BCrypt.hashpw(userCreateForm.getPassword(), BCrypt.gensalt());
        user.setPassword(hashPW);

        user = userRepository.save(user);

        return convertUserToDto(user);
    }

    @Override
    public UserDto editUser(UserCreateForm userCreateForm) throws Exception {
        Optional<User> userOpt = userRepository.findUserByUsername(userCreateForm.getUsername());
        if (userOpt.isEmpty()) {
            throw new Exception("user does not exist");
        }
        User user = userOpt.get();

        if (!userCreateForm.getPassword().isBlank()) {
            String hashPW = BCrypt.hashpw(userCreateForm.getPassword(), BCrypt.gensalt());
            user.setPassword(hashPW);
        }
        if (!userCreateForm.getImePrezime().isBlank()) {
            user.setImePrezime(userCreateForm.getImePrezime());
        }
        if (userCreateForm.getIsAdmin() != null) {
            user.setIsAdmin(userCreateForm.getIsAdmin());
        }

        user = userRepository.save(user);

        return convertUserToDto(user);
    }

    @Override
    public UserDto deleteUser(String username) throws Exception {
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        if (userOpt.isEmpty()) {
            throw new Exception("user does not exist");
        }

        userRepository.delete(userOpt.get());

        return convertUserToDto(userOpt.get());
    }

    @Override
    public boolean isAdmin(String username) throws Exception {
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        if (userOpt.isEmpty()) {
            throw new Exception("user does not exist");
        }

        return userOpt.get().getIsAdmin();
    }

    private UserDto convertUserToDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setIsAdmin(user.getIsAdmin());
        userDto.setImePrezime(user.getImePrezime());

        return userDto;
    }

}

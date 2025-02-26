package rs.edu.raf.vezbe.service;

import rs.edu.raf.vezbe.dto.UserDto;
import rs.edu.raf.vezbe.form.UserCreateForm;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<UserDto> getUser(String username);
    List<UserDto> listUsers();
    UserDto createUser(UserCreateForm userCreateForm) throws Exception;
    UserDto editUser(UserCreateForm userCreateForm) throws Exception;
    UserDto deleteUser(String username) throws Exception;
    boolean isAdmin(String username) throws Exception;

}

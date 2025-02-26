package rs.edu.raf.vezbe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.vezbe.dto.UserDto;
import rs.edu.raf.vezbe.form.UserCreateForm;
import rs.edu.raf.vezbe.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDto>> listUsers() {
        try {
            return ResponseEntity.ok(userService.listUsers());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> getUser(@PathVariable String username) {
        try {
            Optional<UserDto> user = userService.getUser(username);
            return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> createUser(@RequestBody UserCreateForm userCreateForm) {
        try {
            UserDto userDto = userService.createUser(userCreateForm);
            return ResponseEntity.ok().body(userDto);
        } catch (Exception e) {
            if(e.getMessage().equals("user is missing data")) {
                return ResponseEntity.badRequest().build();
            }
            throw new RuntimeException(e);
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> editUser(@RequestBody UserCreateForm userCreateForm) {
        try {
            UserDto userDto = userService.editUser(userCreateForm);
            return ResponseEntity.ok().body(userDto);
        } catch (Exception e) {
            if(e.getMessage().equals("user does not exist")) {
                return ResponseEntity.notFound().build();
            }
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping(value = "/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> deleteUser(@PathVariable String username) {
        try {
            UserDto userDto = userService.deleteUser(username);
            return ResponseEntity.ok().body(userDto);
        } catch (Exception e) {
            if(e.getMessage().equals("user does not exist")) {
                return ResponseEntity.notFound().build();
            }
            throw new RuntimeException(e);
        }
    }

}

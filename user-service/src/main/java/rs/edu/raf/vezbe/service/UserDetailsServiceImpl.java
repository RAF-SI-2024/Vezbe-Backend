package rs.edu.raf.vezbe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.edu.raf.vezbe.model.User;
import rs.edu.raf.vezbe.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        if(userOpt.isEmpty()) {
            throw new UsernameNotFoundException("user does not exist");
        }

        List<GrantedAuthority> roles = new ArrayList<>();

        return new org.springframework.security.core.userdetails.User(userOpt.get().getUsername(), userOpt.get().getPassword(), roles);
    }

}

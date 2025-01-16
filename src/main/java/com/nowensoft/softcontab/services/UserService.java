package com.nowensoft.softcontab.services;



import org.slf4j.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nowensoft.softcontab.models.User;
import com.nowensoft.softcontab.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {
    private static final Logger logger =  LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    // Constructor-based injection
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

     @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException("Usuario no encontrado: " + username);
                });
        return user;
    }  

}



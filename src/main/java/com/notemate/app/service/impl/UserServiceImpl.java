package com.notemate.app.service.impl;

import com.notemate.app.constants.Role;
import com.notemate.app.entity.User;
import com.notemate.app.repository.UserRepository;
import com.notemate.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public User saveNewUser(String username, String password) {
        Optional<User> existingUser = userRepository.findByUsername(username);

        if(existingUser.isPresent()){
            log.error("User with username {} already exists!", username);
            return null;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(List.of(Role.USER));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("User with username {} registered!", username);
        return user;
    }

    @Override
    public User signIn(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            if (authentication.isAuthenticated()) {
                log.info("User with username {} signed in!", username);
                return userRepository.findByUsername(username).orElse(null);
            }
        } catch (Exception e) {
            log.error("Failed to generate authentication token!", e);
            return null;
        }
        log.error("Invalid Credentials!");
        return null;
    }

}

package com.notemate.app.service.impl;

import com.notemate.app.constants.Role;
import com.notemate.app.entity.User;
import com.notemate.app.exceptions.BadRequestException;
import com.notemate.app.exceptions.ResourceAlreadyExistsException;
import com.notemate.app.exceptions.ResourceNotFoundException;
import com.notemate.app.repository.UserRepository;
import com.notemate.app.service.AuthService;
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
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public User saveNewUser(String name, String email, String username, String password) {
        Optional<User> existingUser = userRepository.findByUsername(username);

        if(existingUser.isPresent()){
            throw new ResourceAlreadyExistsException("User with username " + username + " already exists!");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
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
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isEmpty()) {
            throw new ResourceNotFoundException("User with username " + username + " not found!");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            if (authentication.isAuthenticated()) {
                log.info("User with username {} signed in!", username);
                return existingUser.get();
            }
        } catch (Exception e) {
            throw new BadRequestException("Please enter valid credentials!");
        }

        throw new BadRequestException("Please enter valid credentials!");
    }

}

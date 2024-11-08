package com.notemate.app.user.service.impl;

import com.notemate.app.common.exception.BadRequestException;
import com.notemate.app.common.exception.ResourceAlreadyExistsException;
import com.notemate.app.common.exception.ResourceNotFoundException;
import com.notemate.app.user.constant.Role;
import com.notemate.app.user.entity.User;
import com.notemate.app.user.repository.UserRepository;
import com.notemate.app.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public User signUp(String name, String email, String username, String password) {
        Optional<User> existingUser = userRepository.findByUsername(username);

        if(existingUser.isPresent()){
            throw new ResourceAlreadyExistsException("User with username " + username + " already exists!");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("User with username {} registered!", username);
        return user;
    }

    @Override
    public User signIn(String username, String password) {
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found!"));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        if (authentication.isAuthenticated()) {
            log.info("User with username {} signed in!", username);
            return existingUser;
        }

        throw new BadRequestException("Please enter valid credentials!");
    }

}

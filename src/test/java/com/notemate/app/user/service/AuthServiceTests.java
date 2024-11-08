package com.notemate.app.user.service;

import com.notemate.app.common.exception.BadRequestException;
import com.notemate.app.common.exception.ResourceAlreadyExistsException;
import com.notemate.app.common.exception.ResourceNotFoundException;
import com.notemate.app.user.constant.Role;
import com.notemate.app.user.entity.User;
import com.notemate.app.user.repository.UserRepository;
import com.notemate.app.user.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    private AutoCloseable closeable;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp(){
       closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test 1: Sign up a new user and return the user created")
    @Order(1)
    void signUp_UserCreated_ReturnUser(){
        String name = "Niraj Surve";
        String email = "niraj@gmail.com";
        String username = "niraj";
        String password = "Niraj@123";
        Role role = Role.USER;

        Mockito.when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setUsername(username);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(password));

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        User createdUser = authService.signUp(name, email, username, password);

        assertNotNull(createdUser);
        assertEquals(name, createdUser.getName());
        assertEquals(email, createdUser.getEmail());
        assertEquals(username, createdUser.getUsername());
        assertNotNull(createdUser.getPassword());
        assertNotEquals(password, createdUser.getPassword());
        assertEquals("encodedPassword", createdUser.getPassword());
    }

    @Test
    @DisplayName("Test 2: Sign up a new user and throw resource already exists exception when user already exists")
    @Order(2)
    void signUp_UserAlreadyExists_ThrowResourceAlreadyExistsException(){
        String name = "Niraj Surve";
        String email = "niraj@gmail.com";
        String username = "niraj";
        String password = "Niraj@123";

        User existingUser = new User();
        existingUser.setName(name);
        existingUser.setEmail(email);
        existingUser.setUsername(username);
        existingUser.setPassword(passwordEncoder.encode(password));

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));

        Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> authService.signUp(name, email, username, password));
    }

    @Test
    @DisplayName("Test 3: Sign in an existing user and return the authenticated user")
    @Order(3)
    void signIn_UserExists_Authenticated_ReturnUser(){
        String username = "niraj";
        String password = "Niraj@123";

        User existingUser = new User();
        existingUser.setUsername(username);
        existingUser.setPassword(passwordEncoder.encode(password));
        existingUser.setRole(Role.USER);

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        User signedInUser = authService.signIn(username, password);

        assertNotNull(signedInUser);
        assertEquals(username, signedInUser.getUsername());
    }

    @Test
    @DisplayName("Test 4: Sign in an existing user and throw resource not found exception when user with the username not found")
    @Order(4)
    void signIn_UserNotFound_ThrowResourceNotFoundException(){
        String username = "nonExistentUser";
        String password = "password";
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> authService.signIn(username, password));
    }

    @Test
    @DisplayName("Test 5: Sign in an existing user and return bad request exception when the user is not authenticated")
    @Order(5)
    void signIn_UserExists_NotAuthenticated_ThrowBadRequestException(){
        String username = "nonExistentUser";
        String password = "password";

        User existingUser = new User();
        existingUser.setUsername(username);
        existingUser.setPassword(passwordEncoder.encode(password));

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadRequestException("Invalid credentials"));

        Assertions.assertThrows(BadRequestException.class, () -> authService.signIn(username, password));
    }

    @AfterEach
    void cleanup() throws Exception {
        closeable.close();
    }
}

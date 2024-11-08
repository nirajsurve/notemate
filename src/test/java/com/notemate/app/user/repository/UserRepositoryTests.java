package com.notemate.app.user.repository;

import com.notemate.app.user.constant.Role;
import com.notemate.app.user.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    private final String name = "Niraj Surve";
    private final String email = "niraj@gmail.com";
    private final String username = "niraj";
    private final String password = "password";
    private final Role role = Role.USER;

    private User user;

    private boolean isFirstTestTested = true;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
    }

    @Test
    @DisplayName("Test 1: Save user returns persisted user")
    @Order(1)
    void saveUser_ReturnsPersistedUser() {
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser);
        assertEquals(name, savedUser.getName());
        assertEquals(email, savedUser.getEmail());
        assertEquals(username, savedUser.getUsername());
        assertEquals(role, savedUser.getRole());
    }

    @Test
    @DisplayName("Test 2: Find by username returns existing user")
    @Order(2)
    void findByUsername_UserExists_ReturnsUser() {
        userRepository.save(user);

        Optional<User> result = userRepository.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(name, result.get().getName());
        assertEquals(email, result.get().getEmail());
        assertEquals(username, result.get().getUsername());
        assertEquals(role, result.get().getRole());

        isFirstTestTested = false;
    }

    @Test
    @DisplayName("Test 3: Find by username returns empty optional when user not found")
    @Order(3)
    void findByUsername_UserNotFound_ReturnsEmptyOptional() {
        Optional<User> result = userRepository.findByUsername("unknownUsername");
        assertTrue(result.isEmpty());
    }

    @AfterEach
    void cleanUp(){
        if(isFirstTestTested){
            User existingUser = userRepository.findByUsername(username).orElseThrow();
            userRepository.deleteById(existingUser.getId());
        }
    }
}

package com.notemate.app.user.service;

import com.notemate.app.common.exception.ResourceNotFoundException;
import com.notemate.app.user.constant.Role;
import com.notemate.app.user.dto.data.UserData;
import com.notemate.app.user.entity.User;
import com.notemate.app.user.repository.UserRepository;
import com.notemate.app.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Optional;

class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    private AutoCloseable closeable;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test 1: Get user details and return the user data when user exists")
    @Order(1)
    void getUserDetails_UserExists_ReturnUserData() {
        String name = "Niraj Surve";
        String email = "niraj@gmail.com";
        String username = "niraj";
        String password = "Niraj@123";
        Role role = Role.USER;

        User existingUser = new User();
        existingUser.setName(name);
        existingUser.setEmail(email);
        existingUser.setUsername(username);
        existingUser.setPassword(password);
        existingUser.setRole(role);

        UserData userData = new UserData();
        userData.setName(name);
        userData.setEmail(email);
        userData.setUsername(username);
        userData.setRole(role);

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));

        Mockito.when(modelMapper.map(existingUser, UserData.class)).thenReturn(userData);

        UserData result = userService.getUserDetails(username);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(name, result.getName());
        Assertions.assertEquals(email, result.getEmail());
        Assertions.assertEquals(username, result.getUsername());
        Assertions.assertEquals(role, result.getRole());
    }

    @Test
    @DisplayName("Get user details and throw resource not found exception when user not found")
    @Order(2)
    void getUserDetails_UserNotFound_ThrowResourceNotFoundException() {
        String username = "niraj";

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.getUserDetails(username));
    }

    @AfterEach
    void cleanUp() throws Exception {
        closeable.close();
    }
}

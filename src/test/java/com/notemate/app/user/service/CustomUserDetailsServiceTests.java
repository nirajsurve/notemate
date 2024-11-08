package com.notemate.app.user.service;

import com.notemate.app.user.constant.Role;
import com.notemate.app.user.entity.User;
import com.notemate.app.user.repository.UserRepository;
import com.notemate.app.user.service.impl.CustomUserDetailsService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomUserDetailsServiceTests {

    @Mock
    private UserRepository userRepository;

    private AutoCloseable closeable;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp(){
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test 1: load user by username and return user details when user exists")
    @Order(1)
    void loadUserByUsername_UserExists_ReturnUserDetails() {
        String username = "niraj";
        String password = "password123";
        Role role = Role.USER;
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);

        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals(username, userDetails.getUsername());
        Assertions.assertEquals(password, userDetails.getPassword());
        Assertions.assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Test 2: load user by username and throw resource not found exception when user is not found")
    @Order(2)
    void loadUserByUsername_UserNotFound_ThrowResourceNotFoundException(){
        String username = "nonExistentUser";
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(username));
    }

    @AfterEach
    void cleanUp() throws Exception{
        closeable.close();
    }
}

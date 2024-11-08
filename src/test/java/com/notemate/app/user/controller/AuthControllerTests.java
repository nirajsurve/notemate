package com.notemate.app.user.controller;

import com.notemate.app.common.dto.response.CustomApiResponse;
import com.notemate.app.common.exception.InternalServerErrorException;
import com.notemate.app.common.exception.ResourceAlreadyExistsException;
import com.notemate.app.common.exception.ResourceNotFoundException;
import com.notemate.app.common.util.JwtUtil;
import com.notemate.app.user.dto.request.SigninRequest;
import com.notemate.app.user.dto.request.SignupRequest;
import com.notemate.app.user.entity.User;
import com.notemate.app.user.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTests {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthServiceImpl authService;

    @Mock
    private JwtUtil jwtUtil;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test 1: API health check returns API is healthy response")
    @Order(1)
    void healthCheck_ReturnHealthyResponse() {
        ResponseEntity<CustomApiResponse<String>> response = authController.healthCheck();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("API is healthy", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Test 2: Sign up user returns user created response")
    @Order(2)
    void signUp_ValidRequest_ReturnUserCreatedResponse() {
        String name = "Niraj Surve";
        String email = "niraj@gmail.com";
        String username = "niraj";
        String password = "Niraj@123";

        SignupRequest request = createSignupRequest(name, email, username, password);

        User mockUser = new User();
        mockUser.setUsername(username);
        when(authService.signUp(name, email, username, password)).thenReturn(mockUser);

        ResponseEntity<CustomApiResponse<String>> response = authController.signUp(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User Created Successfully!", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Test 3: Sign up user returns BadRequestException when data is invalid")
    @Order(3)
    void signUp_InvalidData_ReturnBadRequest() {
        SignupRequest request = createSignupRequest("Test User", "", "testuser", "");

        assertThrows(InternalServerErrorException.class, () -> authController.signUp(request));
    }

    @Test
    @DisplayName("Test 4: Sign up user returns InternalServerErrorException when user is null")
    @Order(4)
    void signUp_NullUser_ThrowsInternalServerErrorException() {
        SignupRequest request = createSignupRequest("Test User", "test@example.com", "testuser", "password");

        when(authService.signUp(request.getName(), request.getEmail(), request.getUsername(), request.getPassword())).thenReturn(null);

        assertThrows(InternalServerErrorException.class, () -> authController.signUp(request));
    }

    @Test
    @DisplayName("Test 5: Sign up user returns ResourceAlreadyExistsException when user with same username already exists")
    @Order(5)
    void signUp_UserAlreadyExists_ThrowsResourceAlreadyExistsException() {
        SignupRequest request = createSignupRequest("Test User", "test@example.com", "testuser", "password");

        when(authService.signUp(request.getName(), request.getEmail(), request.getUsername(), request.getPassword())).thenThrow(ResourceAlreadyExistsException.class);

        assertThrows(ResourceAlreadyExistsException.class, () -> authController.signUp(request));
    }

    @Test
    @DisplayName("Test 6: Sign in existing user returns JWT token in the response data")
    @Order(6)
    void signIn_ValidRequest_ReturnJwtTokenResponse() {
        SigninRequest request = createSigninRequest("testuser", "password");

        User existingUser = new User();
        existingUser.setUsername("testuser");

        when(authService.signIn(request.getUsername(), request.getPassword())).thenReturn(existingUser);
        when(jwtUtil.generateToken(existingUser.getUsername())).thenReturn("mockedJwtToken");

        ResponseEntity<CustomApiResponse<String>> response = authController.signIn(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User sign-in successful!", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals("mockedJwtToken", response.getBody().getData());
    }

    @Test
    @DisplayName("Test 7: Sign in existing user throws InternalServerErrorException when UserDetails is null")
    @Order(7)
    void signIn_NullUserDetails_ThrowsInternalServerErrorException() {
        SigninRequest request = createSigninRequest("testuser", "password");

        when(authService.signIn(request.getUsername(), request.getPassword())).thenReturn(null);

        assertThrows(InternalServerErrorException.class, () -> authController.signIn(request));
    }

    @Test
    @DisplayName("Test 8: Sign in existing user throws ResourceNotFoundException when user with username not found")
    @Order(8)
    void signIn_UserNotFound_ThrowsResourceNotFoundException() {
        SigninRequest request = createSigninRequest("testuser", "password");

        when(authService.signIn(request.getUsername(), request.getPassword())).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> authController.signIn(request));
    }

    @AfterEach
    void cleanUp() throws Exception {
        closeable.close();
    }

    private SignupRequest createSignupRequest(String name, String email, String username, String password) {
        SignupRequest request = new SignupRequest();
        request.setName(name);
        request.setEmail(email);
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    private SigninRequest createSigninRequest(String username, String password) {
        SigninRequest request = new SigninRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

}

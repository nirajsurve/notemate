package com.notemate.app.user.controller;

import com.notemate.app.common.dto.response.CustomApiResponse;
import com.notemate.app.common.exception.InternalServerErrorException;
import com.notemate.app.user.dto.data.UserData;
import com.notemate.app.user.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTests {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test 1: Get user data - Success")
    @Order(1)
    void getUserData_Success() {
        String username = "testuser";
        UserData mockUserData = new UserData();
        mockUserData.setUsername(username);
        mockUserData.setEmail("testuser@example.com");

        when(authentication.getName()).thenReturn(username);
        when(userService.getUserDetails(username)).thenReturn(mockUserData);

        ResponseEntity<CustomApiResponse<UserData>> response = userController.getUserData(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User data fetched successfully!", response.getBody().getMessage());
        assertEquals(mockUserData, response.getBody().getData());

        verify(userService, times(1)).getUserDetails(username);
    }

    @Test
    @DisplayName("Test 2: Get user data - Internal Server Error")
    @Order(2)
    void getUserData_InternalServerError() {
        String username = "testuser";

        when(authentication.getName()).thenReturn(username);
        when(userService.getUserDetails(username)).thenReturn(null);

        InternalServerErrorException exception = assertThrows(InternalServerErrorException.class, () -> {
            userController.getUserData(authentication);
        });

        assertEquals("Internal Server Error!", exception.getMessage());

        verify(userService, times(1)).getUserDetails(username);
    }

    @AfterEach
    void cleanUp() throws Exception {
        closeable.close();
    }
}

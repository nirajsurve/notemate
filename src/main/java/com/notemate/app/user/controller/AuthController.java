package com.notemate.app.user.controller;

import com.notemate.app.common.dto.response.CustomApiResponse;
import com.notemate.app.common.exception.InternalServerErrorException;
import com.notemate.app.common.util.JwtUtil;
import com.notemate.app.user.dto.request.SigninRequest;
import com.notemate.app.user.dto.request.SignupRequest;
import com.notemate.app.user.entity.User;
import com.notemate.app.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication API (Public)", description = "API for checking health of the api and user authentication")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @GetMapping("/health-check")
    @Operation(summary = "Health Check", description = "Check health of our api")
    @ApiResponse(
            responseCode = "200",
            description = "API is healthy",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CustomApiResponse.class)
            )
    )
    public ResponseEntity<CustomApiResponse<String>> healthCheck() {
        return ResponseEntity.status(HttpStatus.OK).body(CustomApiResponse.success("API is healthy", null));
    }

    @PostMapping("/sign-up")
    @Operation(summary = "Sign up", description = "Sign up as a new user")
    public ResponseEntity<CustomApiResponse<String>> signUp(@Valid @RequestBody SignupRequest request) {
        User newUser = authService.signUp(request.getName(), request.getEmail(), request.getUsername(), request.getPassword());

        if (newUser != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomApiResponse.success("User Created Successfully!", null));
        }

        throw new InternalServerErrorException("Internal Server Error!");
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Sign in", description = "Sign in as a existing user")
    public ResponseEntity<CustomApiResponse<String>> signIn(@Valid @RequestBody SigninRequest request) {
        User userDetails = authService.signIn(request.getUsername(), request.getPassword());

        if (userDetails != null) {
            String token = jwtUtil.generateToken(userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(CustomApiResponse.success("User sign-in successful!", token));
        }

        throw new InternalServerErrorException("Internal Server Error!");
    }

}

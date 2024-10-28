package com.notemate.app.controller;

import com.notemate.app.dto.request.UserRequest;
import com.notemate.app.dto.response.ApiResponse;
import com.notemate.app.entity.User;
import com.notemate.app.service.UserService;
import com.notemate.app.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public")
public class PublicController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/health-check")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("OK", null));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<User>> signUp(@RequestBody UserRequest request) {
        User newUser = userService.saveNewUser(request.getUsername(), request.getPassword());

        if (newUser != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("User Created Successfully!", newUser));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Internal Server Error!"));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<String>> signIn(@RequestBody UserRequest request) {
        User userDetails = userService.signIn(request.getUsername(), request.getPassword());

        if (userDetails != null) {
            String token = jwtUtil.generateToken(userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("User sign-in successful!", token));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Internal Server Error"));
    }

}

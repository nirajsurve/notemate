package com.notemate.app.controller;

import com.notemate.app.dto.data.UserData;
import com.notemate.app.dto.response.ApiResponse;
import com.notemate.app.exceptions.InternalServerErrorException;
import com.notemate.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "User Operations API", description = "API for handling operations specifically related to a user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    @PreAuthorize("hasAnyRole('USER')")
    @Operation(summary = "Get User Details", description = "Get the user details")
    public ResponseEntity<ApiResponse<UserData>> getUserData(@PathVariable String username){
        UserData userData = userService.getUserDetails(username);

        if(userData != null){
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("User data fetched successfully!", userData));
        }

        throw new InternalServerErrorException("Internal Server Error!");
    }
}

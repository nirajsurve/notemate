package com.notemate.app.user.controller;

import com.notemate.app.common.dto.response.CustomApiResponse;
import com.notemate.app.common.exception.InternalServerErrorException;
import com.notemate.app.user.dto.data.UserData;
import com.notemate.app.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "User Operations API", description = "API for handling operations specifically related to a user")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get User Details", description = "Get the user details")
    public ResponseEntity<CustomApiResponse<UserData>> getUserData(Authentication authentication){
        UserData userData = userService.getUserDetails(authentication.getName());

        if(userData != null){
            return ResponseEntity.status(HttpStatus.OK).body(CustomApiResponse.success("User data fetched successfully!", userData));
        }

        throw new InternalServerErrorException("Internal Server Error!");
    }
}

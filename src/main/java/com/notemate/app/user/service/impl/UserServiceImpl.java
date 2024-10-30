package com.notemate.app.user.service.impl;

import com.notemate.app.user.dto.data.UserData;
import com.notemate.app.user.entity.User;
import com.notemate.app.common.exception.ResourceNotFoundException;
import com.notemate.app.user.repository.UserRepository;
import com.notemate.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserData getUserDetails(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found!"));

        log.info("Successfully fetched user details for user {}", username);
        return modelMapper.map(user, UserData.class);
    }
}

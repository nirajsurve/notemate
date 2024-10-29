package com.notemate.app.service.impl;

import com.notemate.app.dto.data.UserData;
import com.notemate.app.entity.User;
import com.notemate.app.exceptions.ResourceNotFoundException;
import com.notemate.app.repository.UserRepository;
import com.notemate.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserData getUserDetails(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found!"));

        return modelMapper.map(user, UserData.class);
    }
}

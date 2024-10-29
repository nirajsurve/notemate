package com.notemate.app.service;

import com.notemate.app.dto.data.UserData;

public interface UserService {
    UserData getUserDetails(String username);
}

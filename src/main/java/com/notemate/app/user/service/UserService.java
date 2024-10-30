package com.notemate.app.user.service;

import com.notemate.app.user.dto.data.UserData;

public interface UserService {
    UserData getUserDetails(String username);
}

package com.notemate.app.service;

import com.notemate.app.entity.User;

public interface UserService {

    User saveNewUser(String username, String password);

    User signIn(String username, String password);
}

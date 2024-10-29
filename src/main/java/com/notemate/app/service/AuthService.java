package com.notemate.app.service;

import com.notemate.app.entity.User;

public interface AuthService {

    User saveNewUser(String name, String email, String username, String password);

    User signIn(String username, String password);
}

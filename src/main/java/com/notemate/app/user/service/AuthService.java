package com.notemate.app.user.service;

import com.notemate.app.user.entity.User;

public interface AuthService {

    User signUp(String name, String email, String username, String password);

    User signIn(String username, String password);
}

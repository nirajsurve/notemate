package com.notemate.app.user.dto.data;

import com.notemate.app.user.constant.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserData {
    private String name;
    private String email;
    private String username;
    private Role role;
}

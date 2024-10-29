package com.notemate.app.dto.data;

import com.notemate.app.constants.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserData {
    private String name;
    private String email;
    private String username;
    private List<Role> roles;
}

package com.notemate.app.user.entity;


import com.notemate.app.note.entity.Note;
import com.notemate.app.user.constant.Role;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private ObjectId id;

    private String name;

    private String email;

    @Indexed(unique = true)
    @NonNull
    private String username;

    private String password;

    private Role role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @DBRef
    private List<Note> notes = new ArrayList<>();
}

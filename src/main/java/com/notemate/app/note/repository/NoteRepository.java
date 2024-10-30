package com.notemate.app.note.repository;

import com.notemate.app.note.entity.Note;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoteRepository extends MongoRepository<Note, ObjectId> {
    Optional<Note> findByTitle(String title);
}

package com.notemate.app.note.service;

import com.notemate.app.note.entity.Note;
import org.bson.types.ObjectId;

import java.util.List;

public interface NoteService {
    List<Note> getAllNotes(String username);

    Note createNote(String username, String title, String content);

    Note getNoteById(String username, ObjectId id);

    Note updateNote(String username, ObjectId id, String title, String content);

    boolean deleteNote(String username, ObjectId id);
}

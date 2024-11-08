package com.notemate.app.note.repository;

import com.notemate.app.note.entity.Note;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NoteRepositoryTests {

    @Autowired
    private NoteRepository noteRepository;

    private String title = "Test title";
    private String content = "Test content";

    @BeforeEach
    void setUp() {
        noteRepository.deleteAll();
    }

    @Test
    @DisplayName("Test 1: Find the optional note by title")
    @Order(1)
    void findByTitle_ReturnOptionalNote() {
        Note mockNote = saveMockNote(title, content);

        Optional<Note> note = noteRepository.findByTitle(title);

        assertTrue(note.isPresent());
        assertEquals(mockNote.getTitle(), note.get().getTitle());
        assertEquals(mockNote.getContent(), note.get().getContent());
    }

    @Test
    @DisplayName("Test 2: Returns the empty optional when note does not exist")
    @Order(2)
    void findByTitle_ReturnEmptyOptionalNote() {
        Optional<Note> note = noteRepository.findByTitle("Nonexistent title");

        assertTrue(note.isEmpty());
    }

    private Note saveMockNote(String title, String content) {
        Note mockNote = new Note();
        mockNote.setTitle(title);
        mockNote.setContent(content);
        mockNote.setCreatedAt(LocalDateTime.now());
        mockNote.setUpdatedAt(LocalDateTime.now());
        return noteRepository.save(mockNote);
    }
}
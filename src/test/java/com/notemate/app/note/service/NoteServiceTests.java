package com.notemate.app.note.service;

import com.notemate.app.common.exception.ResourceAlreadyExistsException;
import com.notemate.app.common.exception.ResourceNotFoundException;
import com.notemate.app.note.entity.Note;
import com.notemate.app.note.repository.NoteRepository;
import com.notemate.app.note.service.impl.NoteServiceImpl;
import com.notemate.app.user.entity.User;
import com.notemate.app.user.repository.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NoteServiceImplTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NoteServiceImpl noteService;

    private User mockUser;
    private Note mockNote;
    private final String username = "testuser";
    private final ObjectId noteId = new ObjectId();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockNote = new Note();
        mockNote.setId(noteId);
        mockNote.setTitle("Test Note");
        mockNote.setContent("Test Content");
        mockNote.setCreatedAt(LocalDateTime.now());
        mockNote.setUpdatedAt(LocalDateTime.now());

        mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setNotes(new ArrayList<>(List.of(mockNote)));
    }

    @Test
    @DisplayName("Test getAllNotes - Success")
    void testGetAllNotes_Success() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        List<Note> notes = noteService.getAllNotes(username);

        assertEquals(1, notes.size());
        assertEquals(mockNote.getTitle(), notes.get(0).getTitle());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Test getAllNotes - User Not Found")
    void testGetAllNotes_UserNotFound() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> noteService.getAllNotes(username));
    }

    @Test
    @DisplayName("Test getNoteById - Success")
    void testGetNoteById_Success() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        Note note = noteService.getNoteById(username, noteId);

        assertEquals(mockNote.getId(), note.getId());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Test getNoteById - Note Not Found")
    void testGetNoteById_NoteNotFound() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        assertThrows(ResourceNotFoundException.class, () -> noteService.getNoteById(username, new ObjectId()));
    }

    @Test
    @DisplayName("Test createNote - Success")
    void testCreateNote_Success() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(noteRepository.findByTitle(mockNote.getTitle())).thenReturn(Optional.empty());
        when(noteRepository.save(any(Note.class))).thenReturn(mockNote);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        Note createdNote = noteService.createNote(username, mockNote.getTitle(), mockNote.getContent());

        assertNotNull(createdNote);
        assertEquals(mockNote.getTitle(), createdNote.getTitle());
        verify(noteRepository, times(1)).save(any(Note.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Test createNote - Note Already Exists")
    void testCreateNote_NoteAlreadyExists() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(noteRepository.findByTitle(mockNote.getTitle())).thenReturn(Optional.of(mockNote));

        assertThrows(ResourceAlreadyExistsException.class, () ->
                noteService.createNote(username, mockNote.getTitle(), mockNote.getContent()));
    }

    @Test
    @DisplayName("Test updateNote - Success")
    void testUpdateNote_Success() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(noteRepository.findById(noteId)).thenReturn(Optional.of(mockNote));

        String updatedTitle = "Updated Title";
        String updatedContent = "Updated Content";
        Note updatedNote = noteService.updateNote(username, noteId, updatedTitle, updatedContent);

        assertEquals(updatedTitle, updatedNote.getTitle());
        assertEquals(updatedContent, updatedNote.getContent());
        verify(noteRepository, times(1)).save(mockNote);
    }

    @Test
    @DisplayName("Test updateNote - Note Not Found")
    void testUpdateNote_NoteNotFound() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(noteRepository.findById(noteId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                noteService.updateNote(username, noteId, "New Title", "New Content"));
    }

    @Test
    @DisplayName("Test deleteNote - Success")
    void testDeleteNote_Success() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        boolean result = noteService.deleteNote(username, noteId);

        assertTrue(result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(noteRepository, times(1)).delete(mockNote);
    }

    @Test
    @DisplayName("Test deleteNote - Note Not Found")
    void testDeleteNote_NoteNotFound() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        assertThrows(ResourceNotFoundException.class, () ->
                noteService.deleteNote(username, new ObjectId()));
    }

    @Test
    @DisplayName("Test deleteNote - User Not Found")
    void testDeleteNote_UserNotFound() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                noteService.deleteNote(username, noteId));
    }
}

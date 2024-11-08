package com.notemate.app.note.controller;

import com.notemate.app.common.dto.response.CustomApiResponse;
import com.notemate.app.common.exception.InternalServerErrorException;
import com.notemate.app.note.dto.request.NoteRequest;
import com.notemate.app.note.entity.Note;
import com.notemate.app.note.service.NoteService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NoteControllerTests {
    @Mock
    private NoteService noteService;

    @Mock
    private Authentication authentication;

    private AutoCloseable closeable;

    @InjectMocks
    private NoteController noteController;

    @BeforeEach
    void setUp(){
        closeable = MockitoAnnotations.openMocks(this);
        when(authentication.getName()).thenReturn("testUser");
    }

    @Test
    @DisplayName("Test 1: Get all notes of an existing user")
    @Order(1)
    void getAllUserNotes_ReturnListOfNotesInResponse(){
        List<Note> mockNotes = new ArrayList<>();
        mockNotes.add(new Note());
        when(noteService.getAllNotes("testUser")).thenReturn(mockNotes);

        ResponseEntity<CustomApiResponse<List<Note>>> response = noteController.getAllUserNotes(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Notes fetched successfully!", response.getBody().getMessage());
        assertEquals(mockNotes, response.getBody().getData());
    }

    @Test
    @DisplayName("Test 2: Get note of an existing user by note id")
    @Order(2)
    void getUserNoteById_ReturnExistingNoteInResponse(){
        Note mockNote = createMockNote("Test Title", "Test Content");

        when(noteService.getNoteById(authentication.getName(), mockNote.getId())).thenReturn(mockNote);

        ResponseEntity<CustomApiResponse<Note>> response = noteController.getUserNoteById(authentication, mockNote.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Note fetched successfully!", response.getBody().getMessage());
        assertEquals(mockNote, response.getBody().getData());
    }

    @Test
    @DisplayName("Test 3: Throw internal server error exception when note is null")
    @Order(3)
    void getUserNoteById_ThrowInternalServerErrorException(){
        when(noteService.getNoteById(authentication.getName(), ObjectId.get())).thenThrow(InternalServerErrorException.class);

        assertThrows(InternalServerErrorException.class, () -> noteController.getUserNoteById(authentication, ObjectId.get()));
    }

    @Test
    @DisplayName("Test 4: Create a note for an existing user")
    @Order(4)
    void createNote_ReturnNoteCreatedResponse(){
        String title = "Test Note";
        String content = "Test Content";

        Note mockNote = createMockNote(title, content);

        NoteRequest request = createNoteRequest(title, content);

        when(noteService.createNote(authentication.getName(), title, content)).thenReturn(mockNote);

        ResponseEntity<CustomApiResponse<String>> response = noteController.createNote(authentication, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Note created!", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
        assertNull(response.getBody().getData());
        assertNull(response.getBody().getError());
    }

    @Test
    @DisplayName("Test 5: Throw internal server error exception when created note is null")
    @Order(5)
    void createNote_ThrowInternalServerErrorException(){
        NoteRequest request = createNoteRequest("Test note", "Test Content");
        when(noteService.createNote(authentication.getName(), request.getTitle(), request.getContent())).thenThrow(InternalServerErrorException.class);

        assertThrows(InternalServerErrorException.class, () -> noteController.createNote(authentication, request));
    }

    @Test
    @DisplayName("Test 6: Update an existing note")
    @Order(6)
    void updateNote_ReturnNoteUpdatedResponse(){
        String title = "Test Note";
        String content = "Test Content";

        Note mockNote = createMockNote("Test Note", "Test Content");

        NoteRequest request = createNoteRequest(title, content);

        when(noteService.updateNote(authentication.getName(), mockNote.getId(), title, content)).thenReturn(mockNote);

        ResponseEntity<CustomApiResponse<String>> response = noteController.updateNote(authentication, mockNote.getId(), request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Note updated!", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
        assertNull(response.getBody().getData());
        assertNull(response.getBody().getError());
    }

    @Test
    @DisplayName("Test 7: Throw internal server error exception when updated note is null")
    @Order(7)
    void updateNote_ThrowInternalServerErrorException(){
        NoteRequest request = createNoteRequest("Test note", "Test Content");
        when(noteService.updateNote(authentication.getName(), ObjectId.get(), request.getTitle(), request.getContent())).thenThrow(InternalServerErrorException.class);

        assertThrows(InternalServerErrorException.class, () -> noteController.updateNote(authentication, ObjectId.get(), request));
    }

    @Test
    @DisplayName("Test 8: Delete an existing note")
    @Order(8)
    void deleteNote_ReturnNoteUpdatedResponse(){
        ObjectId noteId = ObjectId.get();
        when(noteService.deleteNote(authentication.getName(),noteId)).thenReturn(true);

        ResponseEntity<CustomApiResponse<String>> response = noteController.deleteNote(authentication, noteId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Note deleted!", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
        assertNull(response.getBody().getData());
        assertNull(response.getBody().getError());
    }

    @Test
    @DisplayName("Test 9: Throw internal server error exception when user is not deleted")
    @Order(9)
    void deleteNote_ThrowInternalServerErrorException(){
        when(noteService.deleteNote(authentication.getName(), ObjectId.get())).thenThrow(InternalServerErrorException.class);

        assertThrows(InternalServerErrorException.class, () -> noteController.deleteNote(authentication, ObjectId.get()));
    }

    private Note createMockNote(String title, String content){
        Note mockNote = new Note();
        mockNote.setId(ObjectId.get());
        mockNote.setTitle(title);
        mockNote.setContent(content);
        mockNote.setCreatedAt(LocalDateTime.now());
        mockNote.setUpdatedAt(LocalDateTime.now());
        return mockNote;
    }

    private NoteRequest createNoteRequest(String title, String content){
        NoteRequest request = new NoteRequest();
        request.setTitle(title);
        request.setContent(content);
        return request;
    }

    @AfterEach
    void cleanUp() throws Exception {
        closeable.close();
    }
}

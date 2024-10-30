package com.notemate.app.note.service.impl;

import com.notemate.app.common.exception.ResourceAlreadyExistsException;
import com.notemate.app.common.exception.ResourceNotFoundException;
import com.notemate.app.note.entity.Note;
import com.notemate.app.note.repository.NoteRepository;
import com.notemate.app.note.service.NoteService;
import com.notemate.app.user.entity.User;
import com.notemate.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Override
    public List<Note> getAllNotes(String username){
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found!"));

        log.info("Successfully fetched all notes for user {}", username);
        return existingUser.getNotes();
    }

    @Override
    public Note getNoteById(String username, ObjectId id){
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found!"));

        log.info("Successfully fetched note with ID {} for user {}", id, username);
        return existingUser.getNotes().stream()
                .filter(note -> note.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Note with ID " + id + " not found for user " + username));

    }

    @Override
    @Transactional
    public Note createNote(String username, String title, String content){
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found!"));

        Optional<Note> existingNote = noteRepository.findByTitle(title);

        if(existingNote.isPresent()){
            throw new ResourceAlreadyExistsException("Note with title " + title + " already exists!");
        }

        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());
        noteRepository.save(note);

        existingUser.getNotes().add(note);
        userRepository.save(existingUser);

        log.info("Successfully created note for user {}", username);
        return note;
    }

    @Override
    public Note updateNote(String username, ObjectId id, String title, String content){
        userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found!"));

        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found!"));

        existingNote.setTitle(title);
        existingNote.setContent(content);
        existingNote.setUpdatedAt(LocalDateTime.now());
        noteRepository.save(existingNote);

        log.info("Successfully updated note with ID {} for user {}", id, username);
        return existingNote;
    }

    @Override
    @Transactional
    public boolean deleteNote(String username, ObjectId id) {
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found!"));

        Note existingNote = existingUser.getNotes().stream()
                .filter(note -> note.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Note not found for user " + username));

        existingUser.getNotes().remove(existingNote);
        userRepository.save(existingUser);

        noteRepository.delete(existingNote);
        log.info("Successfully deleted note with ID {} for user {}", id, username);
        return true;
    }

}

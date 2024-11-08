package com.notemate.app.note.controller;

import com.notemate.app.common.exception.InternalServerErrorException;
import com.notemate.app.note.dto.request.NoteRequest;
import com.notemate.app.note.entity.Note;
import com.notemate.app.note.service.NoteService;
import com.notemate.app.common.dto.response.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/note")
@RequiredArgsConstructor
@Tag(name = "Notes API", description = "API for creating, accessing and manipulating notes of specific user")
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    @Operation(summary = "Get all notes", description = "Get all the notes of specific user")
    public ResponseEntity<CustomApiResponse<List<Note>>> getAllUserNotes(Authentication authentication) {
        List<Note> notes = noteService.getAllNotes(authentication.getName());
        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomApiResponse.success("Notes fetched successfully!", notes));
    }

    @GetMapping("/{noteId}")
    @Operation(summary = "Get an existing note", description = "Get an existing note of specific user by note id")
    public ResponseEntity<CustomApiResponse<Note>> getUserNoteById(Authentication authentication, @PathVariable ObjectId noteId) {
        Note note = noteService.getNoteById(authentication.getName(), noteId);

        if(note != null){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(CustomApiResponse.success("Note fetched successfully!", note));
        }

        throw new InternalServerErrorException("Internal Server Error!");
    }

    @PostMapping
    @Operation(summary = "Create a new note", description = "Create a new note for a specific user")
    public ResponseEntity<CustomApiResponse<String>> createNote(Authentication authentication, @RequestBody NoteRequest request){
        Note note = noteService.createNote(authentication.getName(), request.getTitle(), request.getContent());

        if(note != null){
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomApiResponse.success("Note created!", null));
        }

        throw new InternalServerErrorException("Internal Server Error!");
    }

    @PutMapping("/{noteId}")
    @Operation(summary = "Update an existing note", description = "Update an existing note for a specific user by note id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Note updated successfully!",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomApiResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found! or Note not found!",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomApiResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Note already exists!",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomApiResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomApiResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized Access",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomApiResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomApiResponse.class)
                    )
            )
    }
    )
    public ResponseEntity<CustomApiResponse<String>> updateNote(Authentication authentication, @PathVariable ObjectId noteId, @RequestBody NoteRequest request){
        Note note = noteService.updateNote(authentication.getName(), noteId, request.getTitle(), request.getContent());

        if(note != null){
            return ResponseEntity.status(HttpStatus.OK).body(CustomApiResponse.success("Note updated!", null));
        }

        throw new InternalServerErrorException("Internal Server Error!");
    }

    @DeleteMapping("/{noteId}")
    @Operation(summary = "Delete an existing note", description = "Delete an existing note for a specific user by note id")
    public ResponseEntity<CustomApiResponse<String>> deleteNote(Authentication authentication, @PathVariable ObjectId noteId){
        boolean isDeleted = noteService.deleteNote(authentication.getName(), noteId);

        if(isDeleted){
            return ResponseEntity.status(HttpStatus.OK).body(CustomApiResponse.success("Note deleted!", null));
        }

        throw new InternalServerErrorException("Internal Server Error!");
    }

}

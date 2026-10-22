package com.example.demo.AppUser;

import com.vaadin.flow.component.UI;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name ="Notes")
public class AppUserNotesModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer NoteId;

    @Column(name = "NoteName", nullable = true, unique = true, length = 100)
    private String notename;

    @Column(name = "FileName", nullable = false, length = 255)
    private String filename;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "username", nullable = false, length = 255)
    private String username;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Lob
    @Column(name = "Content")
    private byte[] content;

    @Column(name = "CreateDate", nullable = false)
    private LocalDateTime createddate;

    // --- Getters & Setters ---


    public Integer getNoteId() {
        return NoteId;
    }

    public void setNoteId(Integer noteId) {
        NoteId = noteId;
    }

    public String getNotename() {
        return notename;
    }

    public void setNotename(String notename) {
        this.notename = notename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public LocalDateTime getCreateddate() {
        return createddate;
    }

    public void setCreateddate(LocalDateTime createddate) {
        this.createddate = createddate;
    }
}

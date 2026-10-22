package com.example.demo.AppUser;
import com.example.demo.AppExpenseMainViewModel;
import com.example.demo.AppExpenseRepository;
import com.example.demo.financeview.AppNotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotesService {

    @Autowired
    private AppNotesRepository repository;

    public void save(AppUserNotesModel model) {
        repository.save(model);
    }

    public List<AppUserNotesModel> getAllNotes() {
        return repository.findAll();
    }

    public void deleteNotes(Integer id) {
        repository.deleteById(id);
    }

    public List<AppUserNotesModel> searchNotes(String searchValue) {

        String search = searchValue.toLowerCase();

        return repository.findAll()
                .stream()
                .filter(note ->
                        (note.getNotename() != null &&
                                note.getNotename().toLowerCase().contains(search))
                                ||
                                (note.getFilename() != null &&
                                        note.getFilename().toLowerCase().contains(search))
                )
                .toList();
    }
}
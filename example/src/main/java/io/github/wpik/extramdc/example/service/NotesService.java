package io.github.wpik.extramdc.example.service;

import io.github.wpik.extramdc.example.model.Note;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class NotesService {

    private Map<String, Note> notes = new ConcurrentHashMap<>();

    public String create(Note note) {
        log.debug("Creating note {}", note);
        String id = UUID.randomUUID().toString();
        notes.put(id, note);
        return id;
    }

    public Optional<Note> getById(String id) {
        log.debug("Returning note for {}", id);
        return Optional.ofNullable(notes.get(id));
    }

    public void delete(String id) {
        log.debug("Deleting note with id {}", id);
        notes.remove(id);
    }

    public void deleteAll() {
        log.debug("Deleting all notes");
        notes.clear();
    }

    public int size() {
        return notes.size();
    }
}

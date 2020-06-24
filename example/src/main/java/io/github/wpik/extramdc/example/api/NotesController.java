package io.github.wpik.extramdc.example.api;

import io.github.wpik.extramdc.annotation.ExtraMdc;
import io.github.wpik.extramdc.annotation.MdcField;
import io.github.wpik.extramdc.example.model.Note;
import io.github.wpik.extramdc.example.service.NotesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/note")
@Slf4j
@RequiredArgsConstructor
public class NotesController {
    private final NotesService notesService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ExtraMdc
    String addNote(
            @MdcField(name = "author", expression = "author")
            @MdcField(name = "title", expression = "title")
            @RequestBody @Valid Note note) {
        log.debug("Adding new note {}", note);
        return notesService.create(note);
    }

    @GetMapping("/{id}")
    @ExtraMdc
    Note getNote(@MdcField(name = "id") @PathVariable String id) {
        log.debug("Returning note with id {}", id);
        return notesService.getById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    @DeleteMapping("/{id}")
    @ExtraMdc
    void deleteNote(@MdcField(name = "id") @PathVariable String id) {
        log.debug("Deleting note with id {}", id);
        notesService.delete(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    void handleNotFound(NoSuchElementException exception) {
    }
}

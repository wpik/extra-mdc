package io.github.wpik.extramdc.example.api;

import io.github.wpik.extramdc.example.model.Note;
import io.github.wpik.extramdc.example.service.NotesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotesControllerTest {

    @LocalServerPort
    private int localPort;

    @Autowired
    private NotesService notesService;

    private TestRestTemplate testRestTemplate = new TestRestTemplate();

    @BeforeEach
    void setup(){
        notesService.deleteAll();
    }

    @Test
    void addNote() {
        //given
        Note note = Note.builder().author("John").title("Meeting notes").build();
        assertThat(notesService.size()).isEqualTo(0);

        //when
        ResponseEntity<String> response =
                testRestTemplate.postForEntity("http://localhost:" + localPort + "/note", note, String.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        String id = response.getBody();
        Note noteFromDb = notesService.getById(id).orElseThrow(AssertionError::new);

        assertThat(noteFromDb.getAuthor()).isEqualTo("John");
        assertThat(noteFromDb.getTitle()).isEqualTo("Meeting notes");
        assertThat(noteFromDb.getBody()).isNull();
        assertThat(notesService.size()).isEqualTo(1);
    }

    @Test
    void addInvalidNote() {
        //given
        Note note = Note.builder().author("Just author").build();
        assertThat(notesService.size()).isEqualTo(0);

        //when
        ResponseEntity<String> response =
                testRestTemplate.postForEntity("http://localhost:" + localPort + "/note", note, String.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(notesService.size()).isEqualTo(0);
    }

    @Test
    void getNote() {
        //given
        Note note = Note.builder().author("Alice").title("Meeting notes").body("Write tests").build();
        String id = notesService.create(note);
        assertThat(notesService.size()).isEqualTo(1);

        //when
        ResponseEntity<Note> response =
                testRestTemplate.getForEntity(
                        "http://localhost:" + localPort + "/note/{id}", Note.class, Collections.singletonMap("id", id));

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getAuthor()).isEqualTo("Alice");
        assertThat(response.getBody().getTitle()).isEqualTo("Meeting notes");
        assertThat(response.getBody().getBody()).isEqualTo("Write tests");
    }

    @Test
    void getNonExistingNote() {
        //given
        String id = UUID.randomUUID().toString();
        assertThat(notesService.size()).isEqualTo(0);

        //when
        ResponseEntity<Note> response =
                testRestTemplate.getForEntity(
                        "http://localhost:" + localPort + "/note/{id}", Note.class, Collections.singletonMap("id", id));

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteNote() {
        //given
        Note note = Note.builder().author("Bob").title("Scratchpad").body("To be or not to be").build();
        String id = notesService.create(note);
        assertThat(notesService.size()).isEqualTo(1);

        Note noteFromService = notesService.getById(id).orElseThrow(AssertionError::new);
        assertThat(noteFromService).isNotNull();
        assertThat(noteFromService.getAuthor()).isEqualTo("Bob");
        assertThat(noteFromService.getTitle()).isEqualTo("Scratchpad");
        assertThat(noteFromService.getBody()).isEqualTo("To be or not to be");

        //when
        testRestTemplate.delete("http://localhost:" + localPort + "/note/{id}", Collections.singletonMap("id", id));

        //then
        Optional<Note> noteOptional = notesService.getById(id);
        assertThat(noteOptional.isPresent()).isFalse();
        assertThat(notesService.size()).isEqualTo(0);
    }

    @Test
    void deleteNonExistingNote() {
        //given
        String id = UUID.randomUUID().toString();
        assertThat(notesService.size()).isEqualTo(0);

        //when
        testRestTemplate.delete("http://localhost:" + localPort + "/note/{id}", Collections.singletonMap("id", id));
        assertThat(notesService.size()).isEqualTo(0);
    }
}

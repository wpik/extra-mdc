package io.github.wpik.extramdc.example.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Note {
    @NotBlank
    private String author;
    @NotBlank
    private String title;
    private String body;
}

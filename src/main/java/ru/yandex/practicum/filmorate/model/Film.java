package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class Film {
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @NotNull
    @Size(max=200, message = "Описание фильма не может быть длиннее 200 символов")
    private String description;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма не может быть отрицательной или равна нулю")
    private int duration;

    private int rate;

    @NotNull
    private Rating mpa;

    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();
}
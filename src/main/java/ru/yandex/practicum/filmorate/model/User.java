package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class User {

    private int id;
    private String name;

    @NotBlank
    private String login;

    @Email(message = "Введен некорректный адрес электронной почты")
    @NotBlank
    private String email;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
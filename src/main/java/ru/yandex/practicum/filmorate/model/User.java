package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;


@Data
@AllArgsConstructor
public class User {
    private int id;

    @Email(message = "Ошибка: Некорректный email")
    @NotBlank
    private String email;
    private String login;
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past
    private LocalDate birthday;

}
package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.HashSet;

@Data
@Builder
public class User {
    @PositiveOrZero(message = "id пользователя не может быть отрицательным числом")
    private int id;
    @Email(message = "Проверьте правильность заполнения адреса почты")
    @NotBlank
    private String email;
    @NotBlank(message = "Логин не должен быть пустым")
    private String login;
    private String name;
    @PastOrPresent(message = "День рождения не может быть в будущем")
    private LocalDate birthday;
    private HashSet<Integer> friends;
/*
    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

 */
}

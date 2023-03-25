package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
public class Review {

    private Long reviewId;
    @NotBlank(message = "Контент не должен быть пустым.")
    private String content;

    @NotNull(message = "Поле должно быть True or False.")
    private Boolean isPositive;

    @NotNull(message = "Id пользователя не должно быть пустым.")
    private Long userId;

    @NotNull(message = "Id фильма не должно быть пустым.")
    private Long filmId;

    private Integer useful;
}

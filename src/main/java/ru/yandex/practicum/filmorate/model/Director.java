package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class Director {

    private Long id;

    @NotBlank(message = "Имя режиссра не должно быть пустым или содержать пробелы!")
    private String name;
}

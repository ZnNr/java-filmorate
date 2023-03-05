package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping //получить полный список рейтингов
    public Collection<Mpa> getMpa() {
        return mpaService.getMpa();
    }

    @GetMapping("/{id}") //получить рейтинг по id
    public Mpa getMpaById(@PathVariable int id) {
        return mpaService.getMpaById(id);
    }
}

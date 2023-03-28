package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Эндпоинт по сохранинию объекта.
     * @param review сам объект.
     * @return Возвращает сохраненый объект.
     */
    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.create(review);
    }

    /**
     * Эндпоинт по обновлению объекта.
     * @param review сам объект.
     * @return Возвращает обновленный объект.
     */
    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    /**
     * Эндпоинт по удалиению отзыва по его id.
     * @param id идентификатор отзыва.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reviewService.deleteById(id);
    }

    /**
     * Эндпоинт по нахождению отзыва по его id.
     * @param id идентификатор отзыва.
     * @return Возвращает отзыв
     */
    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) {
        return reviewService.findById(id);
    }

    /**
     * Эндпоинт по получению всех отзывов согласна параметрам.
     * @param count количиства отзывов, по умолчанию 10.
     * @param filmId идентификатор фильма.
     * @return Возварщает отзывы.
     */
    @GetMapping
    public List<Review> findReviews(@RequestParam(defaultValue = "10") Integer count,
                                       @RequestParam(required = false) Long filmId) {

        return reviewService.findReviews(count, filmId);
    }
}

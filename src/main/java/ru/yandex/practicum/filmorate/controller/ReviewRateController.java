package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.ReviewRateService;


@RestController
@AllArgsConstructor
@RequestMapping("/reviews")
public class ReviewRateController {

    private final ReviewRateService reviewRateService;

    /**
     * Эндпоинт позволяет ставить лайки отзывам.
     * @param id идентификатор отзыва.
     * @param userId идентификатор пользователя.
     */
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewRateService.addLike(id, userId);
    }

    /**
     * Эндпоинт поу далинию лайка отзыву.
     * @param id иденитификатор отзыва.
     * @param userId идентификатор пользователя.
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewRateService.deleteLikeOrDislike(id, userId);
    }

    /**
     * Эндпоинт позволяет ставить дизлайки отзывам.
     * @param id идентификатор отзыва.
     * @param userId идентификатор пользователя.
     */
    @PutMapping("/{id}/dislike/{userId}")
    public void addDisLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewRateService.addDislike(id, userId);
    }

    /**
     * Эндпоинт по удалинию дизлайка отзыву.
     * @param id иденитификатор отзыва.
     * @param userId идентификатор пользователя.
     */
    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDisLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewRateService.deleteLikeOrDislike(id, userId);
    }
}
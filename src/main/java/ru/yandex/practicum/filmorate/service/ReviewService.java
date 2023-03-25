package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.utility.constants.EventType;
import ru.yandex.practicum.filmorate.utility.constants.Operation;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewDao reviewDao;

    private final EventService eventService;

    private final UserService userService;

    private final FilmService filmService;



    public void deleteById(Long id) {
        Review review = reviewDao.findById(id);
        eventService.addEvent(new Event(Instant.now().toEpochMilli(),
                review.getUserId(),
                EventType.REVIEW,
                Operation.REMOVE,
                id));

        reviewDao.deleteById(id);
    }

    public Review create(Review review) {
        userService.findUserById(review.getUserId());
        filmService.findFilmById(review.getFilmId());

        Review newReview = reviewDao.add(review);
        eventService.addEvent(new Event(Instant.now().toEpochMilli(),
                newReview.getUserId(),
                EventType.REVIEW,
                Operation.ADD,
                review.getReviewId()));

         return newReview;
    }

    public Review update(Review review) {
        Review oldReview = reviewDao.findById(review.getReviewId());
        eventService.addEvent(new Event(Instant.now().toEpochMilli(),
                oldReview.getUserId(),
                EventType.REVIEW,
                Operation.UPDATE,
                oldReview.getFilmId()));

        return reviewDao.update(review);
    }

    public Review findById(Long id) {
        return reviewDao.findById(id);
    }


    public List<Review> findReviews(int limit, Long filmId) {
        if (filmId != null) {
            return reviewDao.findReviewsByFilmId(limit, filmId);
        }

        return reviewDao.findAllReviews(limit);
    }
}

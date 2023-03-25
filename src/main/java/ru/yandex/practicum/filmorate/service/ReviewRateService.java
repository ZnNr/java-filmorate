package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dao.ReviewRateDao;


@Service
@AllArgsConstructor
public class ReviewRateService {

    private final ReviewRateDao reviewRateDao;

    private final ReviewDao reviewDao;

    private final UserService userService;


    public void addLike(Long revId, Long userId) {
        reviewRateDao.addLike(revId, userId);
    }

    public void addDislike(Long revId, Long userId) {
        reviewRateDao.addDislike(revId, userId);
    }


    public void deleteLikeOrDislike(Long revId, Long userId) {
        userService.findUserById(userId);
        reviewDao.findById(revId);

        reviewRateDao.deleteLikeOrDislike(revId, userId);
    }
}

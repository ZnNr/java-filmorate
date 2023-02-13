package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {

    private int id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @PositiveOrZero
    private int duration;
    @PositiveOrZero
    private Set<Integer> likes = new HashSet<>();

    public static final Comparator<Film> COMPARE_BY_LIKES = new Comparator<Film>() {
        @Override
        public int compare(Film o1, Film o2) {
            return o1.getLikes().size() - o2.getLikes().size();
        }
    };
}
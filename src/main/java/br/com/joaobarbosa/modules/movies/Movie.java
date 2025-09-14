package br.com.joaobarbosa.modules.movies;

import br.com.joaobarbosa.modules.movies.dto.CreateMovieDto;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity(name = "movies")
@Table(name = "movies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "title")
    private String title;

    @Column(nullable = false, name = "duration_min")
    private Integer durationMin;

    @Column(nullable = false, name = "rating")
    private String rating;

    @Column(nullable = false, name = "synopsis")
    private String synopsis;

    @Column(nullable = false, name = "created_at", updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(nullable = false, name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;

    public Movie(CreateMovieDto createMovieDto) {
        this.title = createMovieDto.getTitle();
        this.durationMin = createMovieDto.getDurationMin();
        this.rating = createMovieDto.getRating();
        this.synopsis = createMovieDto.getSynopsis();
    }
}

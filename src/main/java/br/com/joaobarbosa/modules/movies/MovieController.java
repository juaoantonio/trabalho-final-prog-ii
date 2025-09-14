package br.com.joaobarbosa.modules.movies;

import br.com.joaobarbosa.config.security.annotations.PublicEndpoint;
import br.com.joaobarbosa.config.security.annotations.RequireAdmin;
import br.com.joaobarbosa.modules.movies.dto.CreateMovieDto;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    @RequireAdmin
    public ResponseEntity<Movie> saveMovie(@RequestBody CreateMovieDto data) {
        log.info(String.valueOf(data));
        var movie = movieService.saveMovie(data);
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/{id}")
    @PublicEndpoint
    public ResponseEntity<Movie> findById(@PathVariable UUID id) {

        return ResponseEntity.ok(movieService.findById(id));
    }

    @GetMapping
    @PublicEndpoint
    public ResponseEntity<List<Movie>> findAll() {
        List<Movie> movies = movieService.findAll();

        if (movies.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(movies);
    }

    @DeleteMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        movieService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<Movie> updateMovie(
            @PathVariable UUID id, @RequestBody CreateMovieDto updatedMovie) {
        Movie updated = movieService.updateById(id, updatedMovie);
        return ResponseEntity.ok(updated);
    }
}

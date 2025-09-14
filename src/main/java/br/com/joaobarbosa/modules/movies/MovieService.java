package br.com.joaobarbosa.modules.movies;

import br.com.joaobarbosa.modules.movies.dto.CreateMovieDto;
import br.com.joaobarbosa.shared.exceptions.client.NotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MovieService {
    private final MovieRepository repository;

    public MovieService(MovieRepository repository) {
        this.repository = repository;
    }

    public Movie saveMovie(CreateMovieDto data) {
        Movie newMovie = new Movie(data);
        return repository.save(newMovie);
    }

    public Movie findById(UUID id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Filme Não Encontrado"));
    }

    public List<Movie> findAll() {
        return repository.findAll();
    }

    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Filme com id " + id + " não encontrado");
        }
        repository.deleteById(id);
    }

    public Movie updateById(UUID id, CreateMovieDto updatedMovie) {
        Movie movieEntity =
                repository
                        .findById(id)
                        .orElseThrow(() -> new NotFoundException("Filme Não Encontrado"));

        if (updatedMovie.getTitle() != null) movieEntity.setTitle(updatedMovie.getTitle());
        if (updatedMovie.getDurationMin() != null)
            movieEntity.setDurationMin(updatedMovie.getDurationMin());
        if (updatedMovie.getRating() != null) movieEntity.setRating(updatedMovie.getRating());
        if (updatedMovie.getSynopsis() != null) movieEntity.setSynopsis(updatedMovie.getSynopsis());

        return repository.save(movieEntity);
    }
}

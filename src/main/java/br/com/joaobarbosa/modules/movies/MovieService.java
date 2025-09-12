package br.com.joaobarbosa.modules.movies;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MovieService {
    private final MovieRepository repository;

    public MovieService(MovieRepository repository) {
        this.repository = repository;
    }

    //create
    public void saveMovie(Movie movie){
        repository.saveAndFlush(movie);
    }

    //read
    /*public Movie findById(UUID id){
        return repository.findById(id);
    }*/

    public Movie findByTitle(String title){
        return repository.findByTitle(title);
    }

    //delete
    void deleteById(UUID id){
        repository.deleteById(id);
    }

    //update
    public void updateById(UUID id, Movie movie){
        Movie movieEntity = repository.findById(id).orElseThrow(
                () -> new RuntimeException("Movie Not Found")
        );
        Movie updatedMovie = movie.

    }

}

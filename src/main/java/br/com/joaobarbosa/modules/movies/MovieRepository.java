package br.com.joaobarbosa.modules.movies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository <Movie, UUID> {
    //Movie findById(UUID id);
    Movie findByTitle(String title);
}

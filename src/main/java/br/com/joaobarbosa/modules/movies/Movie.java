package br.com.joaobarbosa.modules.movies;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "movies")
@Table(name = "movies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID ID;

    @Column()
    public String title;

    @Column()
    public int durationMin;

    @Column()
    public String rating;

    @Column()
    public String synopsis;

    @Column()
    public Instant createdAt;


}

package br.com.joaobarbosa.modules.movies.dto;

import lombok.Data;

@Data
public class CreateMovieDto {
    private String title;
    private Integer durationMin;
    private String synopsis;
    private String rating;
}

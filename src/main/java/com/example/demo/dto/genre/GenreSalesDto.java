package com.example.demo.dto.genre;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GenreSalesDto {
    private Long genreId;
    private String genreName;
    private Integer totalSold;
}

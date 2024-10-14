package com.example.oqp.content.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentDto {
    private Long id;

    private String title;

    private String frontImage;

    private String writer;

    private LocalDateTime createAt;

    private String category;

    private Integer rating;
}

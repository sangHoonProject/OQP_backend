package com.example.oqp.content.controller.request;

import com.example.oqp.content.model.entity.ContentEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentModifyRequest {
    private Long id;

    private String title;

    private String category;

    public static ContentEntity toEntity(ContentEntity entity, ContentModifyRequest request, String path) {
        if(request.getTitle() != null){
            entity.setTitle(request.getTitle());
        }

        if(path != null){
            entity.setFrontImage(path);
        }

        if(request.getCategory() != null){
            entity.setCategory(request.getCategory());
        }
        return entity;
    }
}

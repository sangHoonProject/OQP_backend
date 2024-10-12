package com.example.oqp.content.controller;

import com.example.oqp.content.pagination.PaginationResponse;
import com.example.oqp.content.model.entity.ContentEntity;
import com.example.oqp.content.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/content")
public class ContentController {

    private final ContentService contentService;

    @Operation(summary = "콘텐츠 전체 조회")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "성공 시 200 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class))
            })
    )
    @GetMapping("/all")
    public ResponseEntity<PaginationResponse<List<ContentEntity>>> all(@PageableDefault(size = 10, page = 0, sort = "rating", direction = Sort.Direction.DESC) Pageable pageable){
        PaginationResponse<List<ContentEntity>> all = contentService.all(pageable);

        return ResponseEntity.ok(all);
    }
}

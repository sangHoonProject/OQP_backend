package com.example.oqp.content.controller;

import com.example.oqp.common.security.custom.CustomUserDetails;
import com.example.oqp.content.controller.response.FileUrlResponse;
import com.example.oqp.content.pagination.PaginationResponse;
import com.example.oqp.content.model.entity.ContentEntity;
import com.example.oqp.content.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @Operation(summary = "썸네일 업로드", description = "파일 업로드 후 저장된 url을 리턴해줌", security = {
            @SecurityRequirement(name = "Authorization")
    })
    @Parameter(name = "file", description = "파일 multipart/form-data 로 보내야함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드에 성공하면 200 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = FileUrlResponse.class))
            })
    })
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        FileUrlResponse upload = contentService.upload(file);
        return ResponseEntity.ok(upload);
    }

}

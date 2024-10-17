package com.example.oqp.content.controller;

import com.example.oqp.common.error.response.ErrorResponse;
import com.example.oqp.common.security.custom.CustomUserDetails;
import com.example.oqp.content.controller.request.ContentAddRequest;
import com.example.oqp.content.controller.request.ContentModifyRequest;
import com.example.oqp.content.model.dto.ContentDto;
import com.example.oqp.content.pagination.PaginationResponse;
import com.example.oqp.content.service.ContentService;
import com.example.oqp.quiz.controller.request.QuizAddRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.http.MediaType;
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

    @Operation(summary = "Content & Quiz 추가", security = {
            @SecurityRequirement(name = "Authorization")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공시 200 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ContentDto.class))
            }),
            @ApiResponse(responseCode = "421", description = "썸네일이 없을시 421 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "422", description = "퀴즈 이미지가 퀴즈 수보다 많을때 422 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ContentDto> add(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart(name = "contentAddRequest") ContentAddRequest contentAddRequest,
            @RequestPart(name = "contentImage") MultipartFile contentImage,
            @Parameter(description = "List형태의 MultipartFile", content = @Content(array = @ArraySchema(schema = @Schema(implementation = MultipartFile.class))))
            @RequestPart(name = "quizImages") List<MultipartFile> quizImage
            ) throws IOException {
        ContentDto dto = contentService.add(customUserDetails, contentAddRequest, contentImage, quizImage);

        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "content 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 조회 성공시 200 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class))
            })
    })
    @GetMapping("/all")
    public ResponseEntity<PaginationResponse<List<ContentDto>>> all(@PageableDefault(size = 20, page = 0)Pageable pageable){
        PaginationResponse<List<ContentDto>> all = contentService.all(pageable);

        return ResponseEntity.ok(all);
    }



}

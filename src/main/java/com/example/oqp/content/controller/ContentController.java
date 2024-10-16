package com.example.oqp.content.controller;

import com.example.oqp.common.error.response.ErrorResponse;
import com.example.oqp.common.security.custom.CustomUserDetails;
import com.example.oqp.content.controller.request.ContentAddRequest;
import com.example.oqp.content.controller.request.ContentModifyRequest;
import com.example.oqp.content.model.dto.ContentDto;
import com.example.oqp.content.pagination.PaginationResponse;
import com.example.oqp.content.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(summary = "콘텐츠 전체 조회")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "성공 시 200 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class))
            })
    )
    @GetMapping("/all")
    public ResponseEntity<PaginationResponse<List<ContentDto>>> all(@PageableDefault(size = 10, page = 0, sort = "rating", direction = Sort.Direction.DESC) Pageable pageable){
        PaginationResponse<List<ContentDto>> all = contentService.all(pageable);

        return ResponseEntity.ok(all);
    }

    @Operation(summary = "Content 추가", description = "Content 추가", security = {
            @SecurityRequirement(name = "Authorization")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드에 성공하면 200 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ContentDto.class))
            })
    })
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(
            @RequestPart(name = "file") MultipartFile file,
            @RequestPart(name = "request")ContentAddRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) throws IOException {
        ContentDto upload = contentService.upload(file, request, userDetails);
        return ResponseEntity.ok(upload);
    }

    @Operation(summary = "콘텐츠 수정", description = "콘텐츠 id, title, category를 받아서 수정한다.", security = {
            @SecurityRequirement(name = "Authorization")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공 . 200 반환", content =  {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ContentDto.class))
            }),
            @ApiResponse(responseCode = "408", description = "요청한 유저가 다를시 408 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "420", description = "수정할 콘텐츠를 찾지 못했을 . 420 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    @PatchMapping(value = "/modify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> modify(
            @RequestPart(name = "file", required = false) MultipartFile file,
            @RequestPart(name = "request", required = false) ContentModifyRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails

    ) throws IOException {
        ContentDto modify = contentService.modify(userDetails, request, file);

        return ResponseEntity.ok(modify);
    }

}

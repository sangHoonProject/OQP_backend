package com.example.oqp.content.controller;

import com.example.oqp.common.error.response.ErrorResponse;
import com.example.oqp.common.security.custom.CustomUserDetails;
import com.example.oqp.content.controller.request.ContentAddRequest;
import com.example.oqp.content.controller.request.ContentModifyRequest;
import com.example.oqp.content.controller.request.ContentQuizDeleteRequest;
import com.example.oqp.content.controller.response.ContentQuizDeleteResponse;
import com.example.oqp.content.model.dto.ContentDto;
import com.example.oqp.content.pagination.PaginationResponse;
import com.example.oqp.content.service.ContentService;
import com.example.oqp.quiz.controller.request.QuizAddRequest;
import com.example.oqp.quiz.controller.request.QuizModifyRequest;
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
import org.springframework.data.repository.query.Param;
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

    @PatchMapping(value = "/modify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Content & Quiz 수정", security = {
            @SecurityRequirement(name = "Authorization")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공시 200 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ContentDto.class))
            }),
            @ApiResponse(responseCode = "408", description = "본인이 추가한 콘텐츠가 아닐 경우 408 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "420", description = "콘텐츠를 찾지 못했을 경우 420 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "423", description = "퀴즈를 찾지 못했을 경우 423 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public ResponseEntity<ContentDto> modify(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,

            @Parameter(description = "application/json 타입으로 보내면 됌 id 필디는 content 고유키를 의미함 무슨 콘텐츠를 " +
                    "수정할지 알아야하기 때문에 다른 필드는 null로 설정하더라도 content id는 필수값임 ")
            @RequestPart(name = "contentRequest", required = false) ContentModifyRequest contentRequest,

            @Parameter(description = "List 형태로 quizRequest를 받음 id필드는 quiz 고유키를 의미함", content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuizAddRequest.class))))
            @RequestPart(name = "quizRequest", required = false)List<QuizModifyRequest> quizRequest,

            @Parameter(description = "multipart/form-data 으로 보내면 됌")
            @RequestPart(name = "contentImg", required = false) MultipartFile contentImg,

            @Parameter(description = "List형태의 MultipartFile", content = @Content(array = @ArraySchema(schema = @Schema(implementation = MultipartFile.class))))
            @RequestPart(name = "quizImgs", required = false) List<MultipartFile> quizImgs
            ) throws IOException {
        ContentDto modify = contentService.modify(customUserDetails, contentRequest, quizRequest, contentImg, quizImgs);

        return ResponseEntity.ok(modify);
    }

    @Operation(summary = "콘텐츠 & 퀴즈 삭제", description = "Content Id는 필수로 넣어줘야함", security = {
            @SecurityRequirement(name = "Authorization")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공시 200 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ContentQuizDeleteResponse.class))
            }),
            @ApiResponse(responseCode = "407", description = "사용자를 찾지 못할경우 407 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "408", description = "삭제할 콘텐츠와 유저가 같지 않을 경우 408 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "420", description = "콘텐츠를 삭제하지 못했을 경우 420 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "423", description = "퀴즈를 찾지 못했을 경우 423 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "424", description = "Quiz에 연결된 Content ID 가 다르면 424 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(
            @RequestBody @Valid ContentQuizDeleteRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        ContentQuizDeleteResponse delete = contentService.delete(request, customUserDetails);

        return ResponseEntity.ok(delete);
    }

}

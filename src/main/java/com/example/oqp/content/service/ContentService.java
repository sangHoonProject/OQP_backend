package com.example.oqp.content.service;

import com.example.oqp.common.error.CustomException;
import com.example.oqp.common.error.ErrorCode;
import com.example.oqp.common.security.custom.CustomUserDetails;
import com.example.oqp.content.controller.request.ContentAddRequest;
import com.example.oqp.content.model.dto.ContentDto;
import com.example.oqp.content.model.repository.CustomContentRepository;
import com.example.oqp.content.pagination.Pagination;
import com.example.oqp.content.pagination.PaginationResponse;
import com.example.oqp.content.model.entity.ContentEntity;
import com.example.oqp.content.model.repository.ContentRepository;
import com.example.oqp.quiz.controller.request.QuizAddRequest;
import com.example.oqp.quiz.model.dto.QuizDto;
import com.example.oqp.quiz.model.entity.QuizEntity;
import com.example.oqp.quiz.model.repository.QuizRepository;
import com.example.oqp.user.model.entity.UserEntity;
import com.example.oqp.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;
    private final CustomContentRepository customContentRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private String UPLOAD_CONTENT_PATH = "upload/content/";
    private String UPLOAD_QUIZ_PATH = "upload/quiz/";

    public PaginationResponse<List<ContentDto>> all(Pageable pageable) {
        var list = contentRepository.findAll(pageable);
        var pagination = Pagination.builder()
                .size(list.getSize())
                .page(list.getNumber())
                .element(list.getNumberOfElements())
                .totalElement(list.getTotalElements())
                .totalPage(list.getTotalPages())
                .build();

        List<ContentDto> elements = list.getContent().stream()
                .map(content -> {
                    return ContentDto.builder()
                            .id(content.getId())
                            .title(content.getTitle())
                            .frontImage(content.getFrontImage())
                            .writer(content.getWriter())
                            .createAt(content.getCreateAt())
                            .category(content.getCategory())
                            .rating(content.getRating())
                            .userId(content.getUserId().getId())
                            .build();
                }).collect(Collectors.toList());

        PaginationResponse<List<ContentDto>> page = PaginationResponse.<List<ContentDto>>builder()
                .body(elements)
                .pagination(pagination)
                .build();

        return page;
    }

    public ContentDto add(CustomUserDetails customUserDetails, ContentAddRequest contentAddRequest, List<QuizAddRequest> quizAddRequests, MultipartFile contentImage, List<MultipartFile> quizImage) throws IOException {
        UserEntity user = userRepository.findByUserId(customUserDetails.getUsername());
        if(contentImage != null){
            File contentFile = new File(UPLOAD_CONTENT_PATH);
            if(!contentFile.exists()){
                contentFile.mkdirs();
            }

            UUID uuid = UUID.randomUUID();
            String contentImageName = uuid.toString() + contentImage.getOriginalFilename();
            Path contentPath = Paths.get(UPLOAD_CONTENT_PATH + contentImageName);

            log.info("contentPath : {}", contentPath);

            contentImage.transferTo(contentPath);

            ContentEntity entity = ContentAddRequest.toEntity(contentAddRequest, contentPath.toString(), user);
            ContentEntity save = contentRepository.save(entity);

            File quizFile = new File(UPLOAD_QUIZ_PATH);
            if(!quizFile.exists()){
                quizFile.mkdirs();
            }

            UUID quizId = UUID.randomUUID();

            String fileName = null;

            List<String> quizImageName = new ArrayList<>();
            for(MultipartFile file : quizImage){
                fileName = file.getOriginalFilename();
                if(fileName != null){
                    fileName += quizId.toString();
                    quizImageName.add(fileName);
                }
            }

            List<Path> path = new ArrayList<>();
            for(String name : quizImageName){
                if(name != null){
                    path.add(Paths.get(UPLOAD_QUIZ_PATH + name));
                }
            }

            List<Path> quizImagePath = new ArrayList<>();
            for(Path url : path){
                quizImagePath = quizImage.stream()
                        .map((image) -> {
                            try {
                                image.transferTo(url);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            return url;
                        })
                        .collect(Collectors.toList());
            }

            List<QuizEntity> quiz = new ArrayList<>();
            for(Path quizUrl : quizImagePath){
                if(quizUrl != null){
                    quiz = quizAddRequests.stream()
                            .map((request) -> {
                                return QuizAddRequest.toQuizEntity(request, quizUrl.toString(), save);
                            })
                            .collect(Collectors.toList());
                }
            }

            List<QuizEntity> quizEntities = quizRepository.saveAll(quiz);

            List<QuizDto> quizDtos = quizEntities.stream()
                    .map((value) -> {
                        return QuizDto.builder()
                                .id(value.getId())
                                .problem(value.getProblem())
                                .image(value.getImage())
                                .correct(value.getCorrect())
                                .createAt(value.getCreateAt())
                                .contentId(value.getContent().getId())
                                .build();
                    }).collect(Collectors.toList());

            ContentDto contentDto = ContentDto.builder()
                    .id(save.getId())
                    .title(save.getTitle())
                    .frontImage(save.getFrontImage())
                    .writer(save.getWriter())
                    .createAt(save.getCreateAt())
                    .category(save.getCategory())
                    .rating(save.getRating())
                    .userId(save.getUserId().getId())
                    .quiz(quizDtos)
                    .build();

            return contentDto;

        }else{
            throw new CustomException(ErrorCode.CONTENT_NOT_FOUND_IMAGE);
        }
    }
}

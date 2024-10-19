package com.example.oqp.content.service;

import com.example.oqp.common.error.CustomException;
import com.example.oqp.common.error.ErrorCode;
import com.example.oqp.common.security.custom.CustomUserDetails;
import com.example.oqp.content.controller.request.ContentAddRequest;
import com.example.oqp.content.controller.request.ContentModifyRequest;
import com.example.oqp.content.controller.request.ContentQuizDeleteRequest;
import com.example.oqp.content.controller.response.ContentQuizDeleteResponse;
import com.example.oqp.content.model.dto.ContentDto;
import com.example.oqp.content.pagination.Pagination;
import com.example.oqp.content.pagination.PaginationResponse;
import com.example.oqp.content.model.entity.ContentEntity;
import com.example.oqp.content.model.repository.ContentRepository;
import com.example.oqp.quiz.controller.request.QuizAddRequest;
import com.example.oqp.quiz.controller.request.QuizModifyRequest;
import com.example.oqp.quiz.model.dto.QuizDto;
import com.example.oqp.quiz.model.entity.QuizEntity;
import com.example.oqp.quiz.model.repository.QuizRepository;
import com.example.oqp.user.model.entity.UserEntity;
import com.example.oqp.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;
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

        List<ContentEntity> content = list.getContent();

        List<ContentDto> elements = content.stream()
                .map(value -> {
                    List<QuizDto> dtos = value.getQuizList().stream().map(quiz -> {
                        return QuizDto.builder()
                                .id(quiz.getId())
                                .image(quiz.getImage())
                                .problem(quiz.getProblem())
                                .correct(quiz.getCorrect())
                                .createAt(quiz.getCreateAt())
                                .contentId(quiz.getContent().getId())
                                .build();
                    }).collect(Collectors.toList());

                    return ContentDto.builder()
                            .id(value.getId())
                            .title(value.getTitle())
                            .frontImage(value.getFrontImage())
                            .writer(value.getWriter())
                            .createAt(value.getCreateAt())
                            .category(value.getCategory())
                            .rating(value.getRating())
                            .userId(value.getUserId().getId())
                            .quiz(dtos)
                            .build();
                }).collect(Collectors.toList());

        PaginationResponse<List<ContentDto>> page = PaginationResponse.<List<ContentDto>>builder()
                .body(elements)
                .pagination(pagination)
                .build();

        return page;
    }

    public ContentDto add(CustomUserDetails customUserDetails, ContentAddRequest contentAddRequest, MultipartFile contentImage, List<MultipartFile> quizImage) throws IOException {
        if(quizImage.size() > contentAddRequest.getQuizAddRequests().size()){
            throw new CustomException(ErrorCode.QUIZ_IMAGE_OVER);
        }
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

            File quizFile = new File(UPLOAD_QUIZ_PATH);
            if(!quizFile.exists()){
                quizFile.mkdirs();
            }

            UUID quizId = UUID.randomUUID();

            String fileName = null;
            String quizImageOriginalFileName = null;

            List<String> quizImageName = new ArrayList<>();
            for(MultipartFile file : quizImage){
                quizImageOriginalFileName = file.getOriginalFilename();
                if(quizImageOriginalFileName != null){
                    fileName += quizId.toString() + quizImageOriginalFileName;
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


            ContentEntity entity = ContentAddRequest.toEntity(contentAddRequest, contentPath.toString(), user);
            ContentEntity save = contentRepository.save(entity);

            List<QuizEntity> quiz = new ArrayList<>();
            for(Path quizUrl : quizImagePath){
                if(quizUrl != null){
                    quiz = contentAddRequest.getQuizAddRequests().stream()
                            .map((request) -> {
                                return QuizAddRequest.toQuizEntity(request, quizUrl.toString(), save);
                            })
                            .collect(Collectors.toList());
                }
            }

            List<QuizEntity> quizEntities = quizRepository.saveAll(quiz);

            save.setQuizList(quizEntities);


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

    @Transactional
    public ContentDto modify(CustomUserDetails customUserDetails, ContentModifyRequest contentRequest, List<QuizModifyRequest> quizRequest, MultipartFile contentImg, List<MultipartFile> quizImgs) throws IOException {
        UserEntity user = userRepository.findByUserId(customUserDetails.getUsername());
        ContentEntity content = contentRepository.findById(contentRequest.getId()).orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND_IMAGE));

        if(!user.getId().equals(content.getUserId().getId())){
            throw new CustomException(ErrorCode.USER_NOT_SAME);
        }

        String contentPath = null;
        if(contentImg != null){
            File contentFile = new File(UPLOAD_CONTENT_PATH);
            if(!contentFile.exists()){
                contentFile.mkdirs();
            }

            UUID uuid = UUID.randomUUID();
            String contentImageName = uuid.toString() + contentImg.getOriginalFilename();
            Path path = Paths.get(UPLOAD_CONTENT_PATH + contentImageName);

            contentImg.transferTo(path);
            contentPath = path.toString();
        }

        ContentEntity contentEntity = ContentModifyRequest.toEntity(content, contentRequest, contentPath);

        List<MultipartFile> quizImage = new ArrayList<>();
        if(quizImgs != null){
            quizImage = quizImgs.stream().filter(Objects::nonNull).collect(Collectors.toList());
        }

        List<QuizEntity> quizEntities = content.getQuizList();
        if(!quizImage.isEmpty()){
            File quizFile = new File(UPLOAD_QUIZ_PATH);
            if(!quizFile.exists()){
                quizFile.mkdirs();
            }

            UUID quizId = UUID.randomUUID();
            List<Path> quizImagePath = quizImage.stream().map(value -> {
                String fileName = quizId.toString() + value.getOriginalFilename();
                Path path = Paths.get(UPLOAD_QUIZ_PATH + fileName);
                try {
                    value.transferTo(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                return path;
            }).collect(Collectors.toList());

            List<QuizEntity> quizEntityList = quizEntities.stream().map(entity -> {
                QuizModifyRequest quizModifyRequest = quizRequest.stream()
                        .filter(request -> request.getId().equals(entity.getId()))
                        .findFirst()
                        .orElseThrow(() -> new CustomException(ErrorCode.QUIZ_NOT_FOUND));

                String quizImageURL = quizImagePath.stream()
                        .filter(path -> quizModifyRequest.getId().equals(entity.getId()))
                        .findFirst()
                        .map(Path::toString)
                        .orElse(entity.getImage());

                return QuizModifyRequest.toEntity(entity, quizModifyRequest, quizImageURL);
            }).collect(Collectors.toList());

            List<QuizEntity> quizEntitySave = quizRepository.saveAll(quizEntityList);
            contentEntity.setQuizList(quizEntitySave);
            ContentEntity contentSave = contentRepository.save(contentEntity);

            List<QuizDto> quizDtos = contentSave.getQuizList().stream().map(entity -> {
                return QuizDto.builder()
                        .id(entity.getId())
                        .problem(entity.getProblem())
                        .image(entity.getImage())
                        .correct(entity.getCorrect())
                        .createAt(entity.getCreateAt())
                        .contentId(entity.getContent().getId())
                        .build();
            }).collect(Collectors.toList());

            return ContentDto.builder()
                    .id(contentSave.getId())
                    .title(contentSave.getTitle())
                    .frontImage(contentSave.getFrontImage())
                    .writer(contentSave.getWriter())
                    .createAt(contentSave.getCreateAt())
                    .category(contentSave.getCategory())
                    .rating(contentSave.getRating())
                    .userId(contentSave.getUserId().getId())
                    .quiz(quizDtos)
                    .build();

        }else{
            if(quizRequest == null){
                List<QuizDto> quizDtos = contentEntity.getQuizList().stream().map(value -> {
                    return QuizDto.builder()
                            .id(value.getId())
                            .problem(value.getProblem())
                            .image(value.getImage())
                            .correct(value.getCorrect())
                            .createAt(value.getCreateAt())
                            .contentId(value.getContent().getId())
                            .build();
                }).collect(Collectors.toList());
                log.info("quizDtos : {}", quizDtos.toString());

                return ContentDto.builder()
                        .id(contentEntity.getId())
                        .title(contentEntity.getTitle())
                        .frontImage(contentEntity.getFrontImage())
                        .writer(contentEntity.getWriter())
                        .createAt(contentEntity.getCreateAt())
                        .category(contentEntity.getCategory())
                        .rating(contentEntity.getRating())
                        .userId(contentEntity.getUserId().getId())
                        .quiz(quizDtos)
                        .build();
            }
            List<QuizEntity> quizEntityList = quizEntities.stream().map(entity -> {
                QuizModifyRequest quizModifyRequest = quizRequest.stream()
                        .filter(request -> request.getId().equals(entity.getId()))
                        .findFirst()
                        .orElseThrow(() -> new CustomException(ErrorCode.QUIZ_NOT_FOUND));

                return QuizModifyRequest.toEntity(entity, quizModifyRequest);
            }).collect(Collectors.toList());

            quizRepository.saveAll(quizEntityList);

            List<QuizDto> quizDtos = quizEntityList.stream().map(value -> {
                return QuizDto.builder()
                        .id(value.getId())
                        .problem(value.getProblem())
                        .image(value.getImage())
                        .correct(value.getCorrect())
                        .createAt(value.getCreateAt())
                        .contentId(value.getContent().getId())
                        .build();
            }).collect(Collectors.toList());

            log.info("quizDtos : {}", quizDtos.toString());

            return ContentDto.builder()
                    .id(contentEntity.getId())
                    .title(contentEntity.getTitle())
                    .frontImage(contentEntity.getFrontImage())
                    .writer(contentEntity.getWriter())
                    .createAt(contentEntity.getCreateAt())
                    .category(contentEntity.getCategory())
                    .rating(contentEntity.getRating())
                    .userId(contentEntity.getUserId().getId())
                    .quiz(quizDtos)
                    .build();

        }

    }

    @Transactional
    public ContentQuizDeleteResponse delete(ContentQuizDeleteRequest request, CustomUserDetails customUserDetails) {
        UserEntity user = userRepository.findByUserId(customUserDetails.getUsername());

        if(user == null){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        ContentEntity content = contentRepository.findById(request.getContentId()).orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));

        if(!content.getUserId().getId().equals(user.getId())){
            throw new CustomException(ErrorCode.USER_NOT_SAME);
        }

        if(request.getQuizId() != null){
            if(!content.getId().equals(request.getContentId())){
                throw new CustomException(ErrorCode.CONTENT_QUIZ_NOT_SAME_ID);
            }
            log.info("quiz id : {}", request.getQuizId());
            QuizEntity quizEntity = quizRepository.findById(request.getQuizId()).orElseThrow(() -> new CustomException(ErrorCode.QUIZ_NOT_FOUND));

            quizRepository.delete(quizEntity);

            return ContentQuizDeleteResponse.builder()
                    .contentDelete(request.getContentId() + "번 콘텐츠는 그대로 있습니다.")
                    .quizDelete(request.getQuizId() + "번 퀴즈가 삭제되었습니다.")
                    .build();
        }

        quizRepository.deleteByContentId(request.getContentId());
        contentRepository.delete(content);

        return ContentQuizDeleteResponse.builder()
                .contentDelete(request.getContentId() + " 콘텐츠가 삭제되었습니다.")
                .quizDelete(request.getContentId() + "번 콘텐츠의 퀴즈가 삭제되었습니다.")
                .build();

    }
}

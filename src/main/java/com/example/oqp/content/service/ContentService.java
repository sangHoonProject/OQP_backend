package com.example.oqp.content.service;

import com.example.oqp.common.error.CustomException;
import com.example.oqp.common.error.ErrorCode;
import com.example.oqp.common.security.custom.CustomUserDetails;
import com.example.oqp.content.controller.request.ContentAddRequest;
import com.example.oqp.content.controller.request.ContentModifyRequest;
import com.example.oqp.content.model.dto.ContentDto;
import com.example.oqp.content.model.repository.CustomContentRepository;
import com.example.oqp.content.pagination.Pagination;
import com.example.oqp.content.pagination.PaginationResponse;
import com.example.oqp.content.model.entity.ContentEntity;
import com.example.oqp.content.model.repository.ContentRepository;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;
    private final CustomContentRepository customContentRepository;
    private final UserRepository userRepository;
    private String UPLOAD_PATH = "upload/";

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
                            .build();
                }).collect(Collectors.toList());

        PaginationResponse<List<ContentDto>> page = PaginationResponse.<List<ContentDto>>builder()
                .body(elements)
                .pagination(pagination)
                .build();

        return page;
    }

    public ContentDto upload(MultipartFile file, ContentAddRequest request, CustomUserDetails userDetails) throws IOException {
        File directory = new File(UPLOAD_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        UUID uuid = UUID.randomUUID();
        String fileName = uuid.toString() + "." + file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_PATH + fileName);
        file.transferTo(path);

        String userId = userDetails.getUsername();
        UserEntity user = userRepository.findByUserId(userId);
        user.setPostingCount(user.getPostingCount() + 1);
        userRepository.save(user);
        log.info("user : {}", user);

        ContentEntity content = ContentAddRequest.toEntity(request, path.toString(), user);
        ContentEntity save = contentRepository.save(content);

        return ContentDto.builder()
                .id(save.getId())
                .title(save.getTitle())
                .frontImage(content.getFrontImage())
                .writer(content.getWriter())
                .rating(content.getRating())
                .category(content.getCategory())
                .createAt(content.getCreateAt())
                .build();
    }


    public ContentDto modify(CustomUserDetails userDetails, ContentModifyRequest request, MultipartFile file) throws IOException {
        ContentEntity content = contentRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));

        String oldUrl = content.getFrontImage();

        File directory = new File(UPLOAD_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        UUID uuid = UUID.randomUUID();
        String fileName = uuid.toString() + "_" + file.getOriginalFilename();
        Path path = Paths.get(oldUrl);
        path.toFile().delete();

        Path newPath = Paths.get(UPLOAD_PATH + fileName);
        file.transferTo(newPath);

        ContentEntity modify = ContentModifyRequest.toEntity(content, request, newPath.toString());
        ContentEntity save = contentRepository.save(modify);

        return ContentDto.builder()
                .id(save.getId())
                .title(save.getTitle())
                .frontImage(save.getFrontImage())
                .writer(save.getWriter())
                .createAt(save.getCreateAt())
                .category(save.getCategory())
                .rating(save.getRating())
                .build();
    }
}

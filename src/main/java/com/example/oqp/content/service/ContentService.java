package com.example.oqp.content.service;

import com.example.oqp.common.security.custom.CustomUserDetails;
import com.example.oqp.content.controller.request.ContentAddRequest;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;
    private final CustomContentRepository customContentRepository;
    private final UserRepository userRepository;
    private String UPLOAD_PATH = "upload/";

    public PaginationResponse<List<ContentEntity>> all(Pageable pageable) {
        var list = contentRepository.findAll(pageable);
        var pagination = Pagination.builder()
                .size(list.getSize())
                .page(list.getNumber())
                .element(list.getNumberOfElements())
                .totalElement(list.getTotalElements())
                .totalPage(list.getTotalPages())
                .build();

        PaginationResponse<List<ContentEntity>> page = PaginationResponse.<List<ContentEntity>>builder()
                .body(list.getContent())
                .pagination(pagination)
                .build();

        return page;
    }

    public ContentEntity upload(MultipartFile file, ContentAddRequest request, CustomUserDetails userDetails) throws IOException {
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
        log.info("user : {}", user);

        ContentEntity content = ContentAddRequest.toEntity(request, path.toString(), user);
        return contentRepository.save(content);
    }


}

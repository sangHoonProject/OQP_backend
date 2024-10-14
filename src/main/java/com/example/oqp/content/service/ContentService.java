package com.example.oqp.content.service;

import com.example.oqp.common.security.custom.CustomUserDetails;
import com.example.oqp.content.controller.response.FileUrlResponse;
import com.example.oqp.content.model.repository.CustomContentRepository;
import com.example.oqp.content.pagination.Pagination;
import com.example.oqp.content.pagination.PaginationResponse;
import com.example.oqp.content.model.entity.ContentEntity;
import com.example.oqp.content.model.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
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
public class ContentService {

    private final ContentRepository contentRepository;
    private final CustomContentRepository customContentRepository;
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

    public FileUrlResponse upload(MultipartFile file) throws IOException {
        File directory = new File(UPLOAD_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        UUID uuid = UUID.randomUUID();
        String fileName = uuid.toString() + "." + file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_PATH + fileName);
        file.transferTo(path);

        return FileUrlResponse.builder()
                .url(path.toString())
                .build();
    }
}

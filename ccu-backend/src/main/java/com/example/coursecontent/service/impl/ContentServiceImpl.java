package com.example.coursecontent.service.impl;

import com.example.coursecontent.service.ContentService;

import com.example.coursecontent.domain.CourseContent;
import com.example.coursecontent.domain.StorageType;
import com.example.coursecontent.dto.request.UpdateContentReq;
import com.example.coursecontent.dto.response.ContentMetadataRes;
import com.example.coursecontent.dto.response.PaginatedRes;
import com.example.coursecontent.exception.NotFoundException;
import com.example.coursecontent.exception.StorageException;
import com.example.coursecontent.exception.UnauthorizedException;
import com.example.coursecontent.mapper.ContentMapper;
import com.example.coursecontent.repository.CourseContentRepository;
import com.example.coursecontent.storage.StorageService;
import com.example.coursecontent.validation.FileValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final CourseContentRepository contentRepository;
    private final StorageService storageService;
    private final FileValidationService validationService;
    private final ContentMapper contentMapper;

    @Transactional
    public ContentMetadataRes upload(MultipartFile file, Principal user) {
        validationService.validate(file);

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown");
        if (originalFilename.contains("..")) {
            throw new StorageException("Cannot store file with relative path outside current directory " + originalFilename);
        }

        String extension = StringUtils.getFilenameExtension(originalFilename);
        if (extension == null) extension = "";
        
        StorageType type = determineActiveStorageType();

        try {
            String storageKey = storageService.store(originalFilename, file.getInputStream(), file.getSize(), file.getContentType());
            
            CourseContent content = CourseContent.builder()
                    .id(UUID.randomUUID())
                    .originalFileName(originalFilename)
                    .contentType(file.getContentType())
                    .extension(extension)
                    .sizeBytes(file.getSize())
                    .uploadDate(LocalDateTime.now())
                    .storageKey(storageKey)
                    .storageType(type)
                    .uploadedBy(user.getName())
                    .build();

            CourseContent savedContent = contentRepository.save(content);
            return contentMapper.toMetadataRes(savedContent);

        } catch (IOException e) {
            throw new StorageException("Failed to read uploaded file", e);
        }
    }

    @Transactional(readOnly = true)
    public PaginatedRes<ContentMetadataRes> list(Pageable pageable) {
        Page<CourseContent> page = contentRepository.findAll(pageable);
        return PaginatedRes.fromPage(page.map(contentMapper::toMetadataRes));
    }

    @Transactional(readOnly = true)
    public ContentMetadataRes get(UUID id) {
        CourseContent content = getEntity(id);
        return contentMapper.toMetadataRes(content);
    }

    @Transactional(readOnly = true)
    public Resource download(UUID id) {
        CourseContent content = getEntity(id);
        return storageService.loadAsResource(content.getStorageKey());
    }

    @Transactional
    public ContentMetadataRes update(UUID id, UpdateContentReq req, Principal principal) {
        CourseContent content = getEntity(id);
        validateOwnership(content, principal);

        content.setOriginalFileName(req.getName());
        CourseContent saved = contentRepository.save(content);
        return contentMapper.toMetadataRes(saved);
    }

    @Transactional
    public void delete(UUID id, Principal principal) {
        CourseContent content = getEntity(id);
        validateOwnership(content, principal);

        try {
            storageService.delete(content.getStorageKey());
        } catch (Exception e) {
            throw new StorageException("Failed to delete file from storage", e);
        }
        contentRepository.deleteById(id);
    }

    private void validateOwnership(CourseContent content, Principal principal) {
        if (!content.getUploadedBy().equals(principal.getName())) {
            throw new UnauthorizedException("You are not allowed to modify this content");
        }
    }

    private CourseContent getEntity(UUID id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course content not found with ID: " + id));
    }

    private StorageType determineActiveStorageType() {
        return storageService.getClass().getSimpleName().contains("S3") ? StorageType.S3 : StorageType.LOCAL;
    }
}

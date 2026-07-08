package com.example.coursecontent.controller;

import com.example.coursecontent.domain.CourseContent;
import com.example.coursecontent.dto.request.UpdateContentReq;
import com.example.coursecontent.dto.response.ContentMetadataRes;
import com.example.coursecontent.dto.response.PaginatedRes;
import com.example.coursecontent.dto.response.StandardResponse;
import com.example.coursecontent.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contents")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ContentController {

    private final ContentService contentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload course content")
    public ResponseEntity<StandardResponse<ContentMetadataRes>> upload(
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        return ResponseEntity.ok(StandardResponse.success("Content uploaded successfully", contentService.upload(file, principal)));
    }

    @GetMapping
    @Operation(summary = "List all course contents")
    public ResponseEntity<StandardResponse<PaginatedRes<ContentMetadataRes>>> list(
            @org.springframework.data.web.PageableDefault(sort = "uploadDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(StandardResponse.success("Contents fetched successfully", contentService.list(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single course content metadata")
    public ResponseEntity<StandardResponse<ContentMetadataRes>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(StandardResponse.success("Content fetched successfully", contentService.get(id)));
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download or stream course content")
    public ResponseEntity<Resource> download(@PathVariable UUID id) {
        Resource resource = contentService.download(id);
        ContentMetadataRes content = contentService.get(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(content.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + content.getName() + "\"")
                .body(resource);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Rename / update course content metadata")
    public ResponseEntity<StandardResponse<ContentMetadataRes>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateContentReq req,
            Principal principal) {
        return ResponseEntity.ok(StandardResponse.success("Content updated successfully", contentService.update(id, req, principal)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete course content")
    public ResponseEntity<StandardResponse<Void>> delete(
            @PathVariable UUID id,
            Principal principal) {
        contentService.delete(id, principal);
        return ResponseEntity.ok(StandardResponse.success("Content deleted successfully", null));
    }
}


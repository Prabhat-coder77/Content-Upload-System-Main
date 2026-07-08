package com.example.coursecontent.service;

import com.example.coursecontent.dto.request.UpdateContentReq;
import com.example.coursecontent.dto.response.ContentMetadataRes;
import com.example.coursecontent.dto.response.PaginatedRes;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.UUID;

public interface ContentService {
    ContentMetadataRes upload(MultipartFile file, Principal user);
    PaginatedRes<ContentMetadataRes> list(Pageable pageable);
    ContentMetadataRes get(UUID id);
    Resource download(UUID id);
    ContentMetadataRes update(UUID id, UpdateContentReq req, Principal principal);
    void delete(UUID id, Principal principal);
}

package com.example.coursecontent.mapper;

import com.example.coursecontent.domain.CourseContent;
import com.example.coursecontent.dto.response.ContentMetadataRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Mapper(componentModel = "spring")
public interface ContentMapper {
    
    @Mapping(source = "originalFileName", target = "name")
    @Mapping(source = "contentType", target = "type")
    @Mapping(source = "sizeBytes", target = "size")
    @Mapping(source = "content", target = "downloadUrl", qualifiedByName = "generateDownloadUrl")
    ContentMetadataRes toMetadataRes(CourseContent content);
    
    @Named("generateDownloadUrl")
    default String generateDownloadUrl(CourseContent content) {
        if (content == null || content.getId() == null) {
            return null;
        }
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/contents/")
                .path(content.getId().toString())
                .path("/download")
                .toUriString();
    }
}

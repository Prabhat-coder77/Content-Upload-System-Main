package com.example.coursecontent.dto.response;

import com.example.coursecontent.domain.CourseContent;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ContentMetadataRes {
    private UUID id;
    private String name;
    private String type;
    private Long size;
    private LocalDateTime uploadDate;
    private String downloadUrl;

}

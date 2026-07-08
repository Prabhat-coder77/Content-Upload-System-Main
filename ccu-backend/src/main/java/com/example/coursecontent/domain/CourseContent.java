package com.example.coursecontent.domain;

import jakarta.persistence.*;
import jakarta.persistence.Index;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "course_content", indexes = {
        @Index(name = "idx_course_content_uploaded_by", columnList = "uploaded_by"),
        @Index(name = "idx_course_content_created_at", columnList = "created_at"),
        @Index(name = "idx_course_content_original_file_name", columnList = "original_file_name"),
        @Index(name = "idx_course_content_content_type", columnList = "content_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseContent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    private String originalFileName;

    private String contentType;

    private String extension;

    private Long sizeBytes;

    private LocalDateTime uploadDate;

    private String storageKey;

    @Enumerated(EnumType.STRING)
    private StorageType storageType;

    private String uploadedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }
}

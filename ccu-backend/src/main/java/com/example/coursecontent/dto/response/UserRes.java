package com.example.coursecontent.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class UserRes {
    private UUID id;
    private String username;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;
}


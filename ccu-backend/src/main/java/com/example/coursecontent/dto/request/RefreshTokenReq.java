package com.example.coursecontent.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenReq {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}

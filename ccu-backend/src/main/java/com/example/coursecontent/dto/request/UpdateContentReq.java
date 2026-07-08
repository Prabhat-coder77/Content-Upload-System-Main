package com.example.coursecontent.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateContentReq {

    @NotBlank(message = "File name is required")
    private String name;
}

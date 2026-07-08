package com.example.coursecontent.controller;

import com.example.coursecontent.domain.User;
import com.example.coursecontent.dto.response.StandardResponse;
import com.example.coursecontent.dto.response.UserRes;
import com.example.coursecontent.exception.NotFoundException;
import com.example.coursecontent.mapper.UserMapper;
import com.example.coursecontent.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<StandardResponse<UserRes>> getCurrentUser(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
        return ResponseEntity.ok(StandardResponse.success("Profile fetched successfully", userMapper.toUserRes(user)));
    }
}

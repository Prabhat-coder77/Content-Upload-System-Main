package com.example.coursecontent.service;

import com.example.coursecontent.dto.response.UserRes;

import java.security.Principal;

public interface UserService {
    UserRes getMe(Principal principal);
}

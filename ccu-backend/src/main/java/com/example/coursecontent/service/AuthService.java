package com.example.coursecontent.service;

import com.example.coursecontent.dto.request.LoginReq;
import com.example.coursecontent.dto.request.RegisterReq;
import com.example.coursecontent.dto.response.TokenRes;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    TokenRes register(RegisterReq req, HttpServletRequest request);
    TokenRes login(LoginReq req, HttpServletRequest request);
    TokenRes refreshToken(String refreshToken, HttpServletRequest request);
    void logout(String refreshToken);
}

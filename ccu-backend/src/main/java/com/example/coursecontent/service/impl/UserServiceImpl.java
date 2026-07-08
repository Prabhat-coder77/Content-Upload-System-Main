package com.example.coursecontent.service.impl;

import com.example.coursecontent.dto.response.UserRes;
import com.example.coursecontent.exception.NotFoundException;
import com.example.coursecontent.mapper.UserMapper;
import com.example.coursecontent.repository.UserRepository;
import com.example.coursecontent.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserRes getMe(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .or(() -> userRepository.findByUsername(principal.getName()))
                .map(userMapper::toUserRes)
                .orElseThrow(() -> new NotFoundException("User not found: " + principal.getName()));
    }
}

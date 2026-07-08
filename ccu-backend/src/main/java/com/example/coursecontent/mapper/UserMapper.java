package com.example.coursecontent.mapper;

import com.example.coursecontent.domain.User;
import com.example.coursecontent.dto.response.UserRes;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserRes toUserRes(User user);
}


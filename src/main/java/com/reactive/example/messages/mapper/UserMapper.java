package com.reactive.example.messages.mapper;

import com.reactive.example.messages.dto.UserCreateDto;
import com.reactive.example.messages.dto.UserDto;
import com.reactive.example.messages.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User to(UserCreateDto source);

    UserDto from(User source);
}

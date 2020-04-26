package com.reactive.example.messages.service;

import com.reactive.example.messages.dto.UserCreateDto;
import com.reactive.example.messages.dto.UserDto;
import com.reactive.example.messages.exception.BadRequestException;
import com.reactive.example.messages.exception.NotFoundException;
import com.reactive.example.messages.mapper.UserMapper;
import com.reactive.example.messages.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public Mono<UserDto> createUser(UserCreateDto userCreateDto) {
        return userRepository.existsById(userCreateDto.getLogin())
                .flatMap(isLoginExist -> isLoginExist
                        ? Mono.error(new BadRequestException("LOGIN_NOT_UNIQUE"))
                        : userRepository.save(userMapper.to(userCreateDto)))
                .map(userMapper::from);
    }

    public Mono<UserDto> getUserByLogin(String login) {
        return userRepository.findById(login)
                .map(userMapper::from);
    }

    public Mono<Void> checkIfExistByLogin(String login) {
        return userRepository.existsById(login)
                .flatMap(isUserExist -> {
                    if (!isUserExist) {
                        return Mono.error(new NotFoundException("USER_NOT_FOUND"));
                    }
                    return Mono.empty();
                });
    }

}

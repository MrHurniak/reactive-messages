package com.reactive.example.messages.service;

import com.reactive.example.messages.component.security.encryption.EncryptionComponent;
import com.reactive.example.messages.component.security.entity.AdaptedUserDetails;
import com.reactive.example.messages.component.security.hashing.HashingComponent;
import com.reactive.example.messages.component.security.token.JwtSecurityComponent;
import com.reactive.example.messages.dto.TokenDto;
import com.reactive.example.messages.dto.UserCreateDto;
import com.reactive.example.messages.dto.UserCredentialsDto;
import com.reactive.example.messages.dto.UserDto;
import com.reactive.example.messages.exception.AuthorizationException;
import com.reactive.example.messages.exception.BadRequestException;
import com.reactive.example.messages.exception.NotFoundException;
import com.reactive.example.messages.mapper.UserMapper;
import com.reactive.example.messages.model.User;
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
    private final HashingComponent hashingComponent;
    private final EncryptionComponent encryptionComponent;
    private final JwtSecurityComponent jwtSecurityComponent;

    public Mono<UserDto> createUser(UserCreateDto userCreateDto) {
        return userRepository.existsById(userCreateDto.getLogin())
                .flatMap(isLoginExist -> isLoginExist
                        ? Mono.error(new BadRequestException("LOGIN_NOT_UNIQUE"))
                        : userRepository.save(processEntityBeforeSave(userCreateDto)))
                .map(userMapper::from);
    }

    public Mono<TokenDto> signInUser(UserCredentialsDto userSignInDto) {
        return userRepository.findById(userSignInDto.getLogin())
                .filter(databaseUser -> hashingComponent.isEquals(userSignInDto.getPassword(), databaseUser.getPassword()))
                .map(AdaptedUserDetails::new)
                .map(user -> new TokenDto().setToken(jwtSecurityComponent.generateToken(user)))
                .switchIfEmpty(Mono.error(AuthorizationException::new));
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

    private User processEntityBeforeSave(UserCreateDto userCreateDto) {
        return userMapper.to(userCreateDto)
                .setEmail(encryptionComponent.encrypt(userCreateDto.getEmail()))
                .setPassword(hashingComponent.hash(userCreateDto.getPassword()));
    }
}

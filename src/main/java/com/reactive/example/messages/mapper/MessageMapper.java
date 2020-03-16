package com.reactive.example.messages.mapper;

import com.reactive.example.messages.dto.MessageCreateDto;
import com.reactive.example.messages.dto.MessageDto;
import com.reactive.example.messages.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "messageText", source = "message")
    Message to(MessageCreateDto source);


    @Mapping(target = "message", source = "messageText")
    MessageDto from(Message source);
}

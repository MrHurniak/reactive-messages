package com.reactive.example.messages.mapper;

import com.reactive.example.messages.dto.MessageCreateDto;
import com.reactive.example.messages.dto.MessageDto;
import com.reactive.example.messages.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "messageText", source = "source.message")
    Message to(MessageCreateDto source, Message.MessageId id);


    @Mapping(target = "userId", source = "id.userId")
    @Mapping(target = "id", source = "id.messageId")
    @Mapping(target = "message", source = "source.messageText")
    MessageDto from(Message source);
}

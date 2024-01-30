package demo.chatapp.message;

import demo.chatapp.message.domain.Message;
import demo.chatapp.channel.service.dto.MessageResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageResponse messageToMessageResponse(Message message);
}

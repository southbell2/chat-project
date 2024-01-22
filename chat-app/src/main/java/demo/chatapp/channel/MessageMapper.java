package demo.chatapp.channel;

import demo.chatapp.channel.domain.Message;
import demo.chatapp.channel.service.dto.MessageResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageResponse messageToMessageResponse(Message message);
}

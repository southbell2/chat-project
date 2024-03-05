package demo.chatapp.message;

import demo.chatapp.channel.service.dto.MessageResponse;
import demo.chatapp.message.domain.Message;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-03-03T12:59:29+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.7 (Oracle Corporation)"
)
@Component
public class MessageMapperImpl implements MessageMapper {

    @Override
    public MessageResponse messageToMessageResponse(Message message) {
        if ( message == null ) {
            return null;
        }

        MessageResponse messageResponse = new MessageResponse();

        messageResponse.setNickname( message.getNickname() );
        messageResponse.setContent( message.getContent() );
        messageResponse.setCreatedAt( message.getCreatedAt() );

        return messageResponse;
    }
}

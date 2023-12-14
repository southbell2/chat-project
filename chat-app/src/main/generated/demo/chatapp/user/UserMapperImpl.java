package demo.chatapp.user;

import demo.chatapp.user.domain.User;
import demo.chatapp.user.service.dto.UserInfoResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-12-15T04:40:42+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserInfoResponse userToUserInfoResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserInfoResponse userInfoResponse = new UserInfoResponse();

        userInfoResponse.setEmail( user.getEmail() );
        userInfoResponse.setNickname( user.getNickname() );
        userInfoResponse.setCreatedAt( user.getCreatedAt() );

        return userInfoResponse;
    }
}

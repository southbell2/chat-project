package demo.chatapp.user;

import demo.chatapp.user.domain.User;
import demo.chatapp.user.service.dto.UserInfoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserInfoResponse userToUserInfoResponse(User user);
}

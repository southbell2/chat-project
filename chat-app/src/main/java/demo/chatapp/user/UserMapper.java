package demo.chatapp.user;

import demo.chatapp.user.domain.RoleType;
import demo.chatapp.user.domain.User;
import demo.chatapp.user.service.dto.UserInfoAdminResponse;
import demo.chatapp.user.service.dto.UserInfoResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserInfoResponse userToUserInfoResponse(User user);

    UserInfoAdminResponse userToUserInfoAdminResponse(User user, List<RoleType> roles);
}

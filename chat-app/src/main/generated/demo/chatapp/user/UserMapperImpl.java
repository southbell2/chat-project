package demo.chatapp.user;

import demo.chatapp.user.domain.RoleType;
import demo.chatapp.user.domain.User;
import demo.chatapp.user.service.dto.PagedUserResponse;
import demo.chatapp.user.service.dto.UserInfoAdminResponse;
import demo.chatapp.user.service.dto.UserInfoResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-01-20T07:29:36+0900",
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

    @Override
    public UserInfoAdminResponse userToUserInfoAdminResponse(User user, List<RoleType> roles) {
        if ( user == null && roles == null ) {
            return null;
        }

        UserInfoAdminResponse userInfoAdminResponse = new UserInfoAdminResponse();

        if ( user != null ) {
            userInfoAdminResponse.setId( user.getId() );
            userInfoAdminResponse.setEmail( user.getEmail() );
            userInfoAdminResponse.setNickname( user.getNickname() );
            userInfoAdminResponse.setCreatedAt( user.getCreatedAt() );
        }
        List<RoleType> list = roles;
        if ( list != null ) {
            userInfoAdminResponse.setRoles( new ArrayList<RoleType>( list ) );
        }

        return userInfoAdminResponse;
    }

    @Override
    public PagedUserResponse userToPagedUserResponse(User user, List<RoleType> roles) {
        if ( user == null && roles == null ) {
            return null;
        }

        PagedUserResponse pagedUserResponse = new PagedUserResponse();

        if ( user != null ) {
            pagedUserResponse.setId( user.getId() );
            pagedUserResponse.setEmail( user.getEmail() );
        }
        List<RoleType> list = roles;
        if ( list != null ) {
            pagedUserResponse.setRoles( new ArrayList<RoleType>( list ) );
        }

        return pagedUserResponse;
    }
}

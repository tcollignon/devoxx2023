package org.tcollignon.user.mapper;

import org.tcollignon.user.front.CreateUserFront;
import org.tcollignon.user.object.User;

public class CreateUserFrontMapper {

    public static User map(CreateUserFront createUser) {
        User user = new User();
        user.nickname = createUser.nickname;
        user.name = createUser.name;
        user.firstName = createUser.firstName;
        user.email = createUser.email;
        user.password = createUser.password;
        user.acceptNewsletter = createUser.acceptNewsletter;
        user.desc = createUser.desc;
        return user;
    }
}
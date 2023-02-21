package org.tcollignon.user.mapper;

import org.tcollignon.user.front.UserFrontLight;
import org.tcollignon.user.object.User;

public class UserFrontLightMapper {

    public static UserFrontLight map(User user) {
        return new UserFrontLight(user.id, user.nickname, user.roles.split(","));
    }
}

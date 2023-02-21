package org.tcollignon.user.mapper;

import org.apache.commons.text.StringSubstitutor;
import org.tcollignon.user.front.UserFront;
import org.tcollignon.user.object.User;

public class UserFrontMapper {

    public static UserFront map(User user) {
        UserFront userFront = new UserFront(user.id, user.active, user.nickname, user.name, user.firstName, user.email, user.creationDate, user.roles.split(","));
        userFront.acceptNewsletter = user.acceptNewsletter;
        userFront.vipLevel = user.vipLevel.getLevel();
        userFront.profilImage = user.profilImage;

        StringSubstitutor interpolator = StringSubstitutor.createInterpolator();
        userFront.interpolateDesc = interpolator.replace(user.desc);
        userFront.desc = user.desc;
        return userFront;
    }

}

package org.tcollignon.user.front;

public class UserFrontLight {
    public long id;
    public String nickname;
    public String[] roles;

    public UserFrontLight(long id, String nickname, String... roles) {
        this.id = id;
        this.nickname = nickname;
        this.roles = roles;
    }
}

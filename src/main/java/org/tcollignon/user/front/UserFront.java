package org.tcollignon.user.front;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;

import static org.tcollignon.user.object.User.ADMIN_ROLE;

public class UserFront {
    public long id;
    public boolean active;
    public String nickname;
    public String name;
    public String desc;
    public String interpolateDesc;
    public String firstName;
    public String email;
    public Instant creationDate;
    public boolean acceptNewsletter;
    public String[] roles;
    public int vipLevel;
    public String profilImage;

    public UserFront(long id, boolean active, String nickname, String name, String firstName, String email, Instant creationDate, String... roles) {
        this.id = id;
        this.active = active;
        this.nickname = nickname;
        this.name = name;
        this.firstName = firstName;
        this.email = email;
        this.creationDate = creationDate;
        this.roles = roles;
    }

    @JsonIgnore
    public boolean isAdmin() {
        for (String role : roles) {
            if (ADMIN_ROLE.equals(role)) {
                return true;
            }
        }
        return false;
    }
}

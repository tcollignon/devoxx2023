package org.tcollignon.user.object;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Arrays;

@Entity
@Table(name = "UserTable", uniqueConstraints = {
    @UniqueConstraint(name = "email_user_uc", columnNames = "email"),
    @UniqueConstraint(name = "nickname_user_uc", columnNames = "nickname")})
@UserDefinition
public class User extends PanacheEntity {
    public static final String USER_ROLE = "user";
    public static final String ADMIN_ROLE = "admin";

    @NotNull
    @Size(max = 160)
    public String nickname;
    public String name;
    public String firstName;
    @Column(length = 2000)
    public String desc;

    @Username
    @Size(max = 254)
    @NotNull
    @Email
    public String email;
    @Password
    public String password;
    @Roles
    public String roles;

    public boolean active;
    public Instant creationDate;
    public boolean acceptNewsletter = true;
    
    public String profilImage;

    public VipLevel vipLevel = VipLevel.LEVEL_0;

    public User() {
        this.roles = USER_ROLE;
    }

    public User(String nickname, String email, String password, String firstName, String name) {
        this();
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.name = name;
    }

    public void addAdminRole() {
        if (!isAdmin()) {
            this.roles += "," + ADMIN_ROLE;
        }
    }

    public void removeAdminRole() {
        if (isAdmin()) {
            this.roles = this.roles.replace("," + ADMIN_ROLE, "");
        }
    }

    public void setVipLevel(int level) {
        this.vipLevel = VipLevel.fromLevel(level);
    }

    public static User findByNickname(String nickname) {
        return find("lower(nickname)=?1", nickname.toLowerCase()).firstResult();
    }

    public static User findByEmail(String email) {
        return find("lower(email)=?1", email.toLowerCase()).firstResult();
    }

    public boolean isVipLevel1() {
        return this.vipLevel.getLevel() >= 1;
    }

    public boolean isAdmin() {
        return Arrays.stream(roles.split(",")).filter(role -> role.equals(ADMIN_ROLE)).count() == 1;
    }
}

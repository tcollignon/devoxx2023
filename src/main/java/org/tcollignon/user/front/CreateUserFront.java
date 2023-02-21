package org.tcollignon.user.front;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateUserFront {
    @NotBlank
    public String nickname;
    public String name;
    public String firstName;
    public String desc;
    @NotBlank
    @Email
    public String email;
    @Size(min = 6, message = "password should have min 6 characters")
    public String password;
    public boolean acceptNewsletter;
    
    public CreateUserFront(String nickname, String name, String firstName, String email, String password) {
        this.nickname = nickname;
        this.name = name;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
    }
}

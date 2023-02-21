package org.tcollignon.user.service;

import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tcollignon.user.exception.EmailAlreadyExistException;
import org.tcollignon.user.exception.NicknameAlreadyExistException;
import org.tcollignon.user.object.User;

import javax.inject.Inject;

@QuarkusTest
public class UserServiceTest {

    @Inject
    UserService userService;

    @BeforeEach
    void init() {
        userService.deleteAllUser();
    }

    @Test
    public void should_return_error_if_create_2_user_with_same_email() {
        //Given
        userService.createUser(new User("tom2", "tom@gmail.com", "toto", "", ""));

        //when
        try {
            userService.createUser(new User("tom3", "tom@gmail.com", "toto", "", ""));
            Assertions.fail("We need exception here");
        } catch (EmailAlreadyExistException e) {
            //then
            Assertions.assertThat(e).hasMessage("The email tom@gmail.com still exist");
        }
    }

    @Test
    public void should_return_error_if_create_2_user_with_same_email_even_if_inactive() {
        //Given
        User user = userService.createUser(new User("tom2", "tom@gmail.com", "toto", "", ""));
        userService.inactive(user.id);

        //when
        try {
            userService.createUser(new User("tom3", "tom@gmail.com", "toto", "", ""));
            Assertions.fail("We need exception here");
        } catch (EmailAlreadyExistException e) {
            //then
            Assertions.assertThat(e).hasMessage("The email tom@gmail.com still exist");
        }
    }

    @Test
    public void should_return_error_if_create_2_user_with_same_nickname() {
        //Given
        userService.createUser(new User("tom4", "tom@gmail.com", "toto", "", ""));

        //when
        try {
            userService.createUser(new User("tom4", "tom2@gmail.com", "toto", "", ""));
            Assertions.fail("We need exception here");
        } catch (NicknameAlreadyExistException e) {
            //then
            Assertions.assertThat(e).hasMessage("The nickname tom4 still exist");
        }
    }

    @Test
    public void should_return_error_if_create_2_user_with_same_nickname_even_if_inactive() {
        //Given
        User user = userService.createUser(new User("tom4", "tom@gmail.com", "toto", "", ""));
        userService.inactive(user.id);

        //when
        try {
            userService.createUser(new User("tom4", "tom2@gmail.com", "toto", "", ""));
            Assertions.fail("We need exception here");
        } catch (NicknameAlreadyExistException e) {
            //then
            Assertions.assertThat(e).hasMessage("The nickname tom4 still exist");
        }
    }

    @Test
    public void should_return_error_if_create_2_admin_with_same_email() {
        //Given
        userService.createAndActiveAdmin("tom11", "tom11@gmail.com", "toto");

        //when
        try {
            userService.createAndActiveAdmin("tom12", "tom11@gmail.com", "toto");
            Assertions.fail("We need exception here");
        } catch (EmailAlreadyExistException e) {
            //then
            Assertions.assertThat(e).hasMessage("The email tom11@gmail.com still exist");
        }
    }

    @Test
    public void should_return_error_if_create_2_admin_with_same_nickname() {
        //Given
        userService.createAndActiveAdmin("tom13", "tom13@gmail.com", "toto");

        //when
        try {
            userService.createAndActiveAdmin("tom13", "tom14@gmail.com", "toto");
            Assertions.fail("We need exception here");
        } catch (NicknameAlreadyExistException e) {
            //then
            Assertions.assertThat(e).hasMessage("The nickname tom13 still exist");
        }
    }

    @Test
    public void should_return_error_if_update_user_with_same_nickname() {
        //Given
        userService.createUser(new User("tom2", "tom2@gmail.com", "toto", "", ""));
        User tomcat = userService.createUser(new User("tom4", "tom4@gmail.com", "toto", "", ""));
        tomcat.nickname = "tom2";

        //when
        try {
            userService.updateUser(tomcat);
            Assertions.fail("We need exception here");
        } catch (NicknameAlreadyExistException e) {
            //then
            Assertions.assertThat(e).hasMessage("The nickname tom2 still exist");
        }
    }
}

package org.tcollignon.user.object;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tcollignon.user.service.UserService;

import javax.inject.Inject;

@QuarkusTest
public class UserTest {

    @Inject
    UserService userService;

    @BeforeEach
    void init() {
        userService.deleteAllUser();
    }

    @Test
    public void should_return_user_when_findByNickname_is_call() {
        //Given
        User user = new User("toto", "toto@pts.com", "pass", "", "");
        user = userService.createUser(user);

        //When
        User result = User.findByNickname("toto");

        //Then
        Assertions.assertEquals(result.id, user.id);
    }

    @Test
    public void should_return_user_when_findByEmail_is_call() {
        //Given
        User user = new User("tata", "tata@pts.com", "pass", "", "");
        user = userService.createUser(user);

        //When
        User result = User.findByEmail("tata@pts.com");

        //Then
        Assertions.assertEquals(result.id, user.id);
    }

    @Test
    public void should_return_admin_when_findByEmail_is_call() {
        //Given
        User admin = userService.createAndActiveAdmin("admin", "admin@pts.com", "pass");

        //When
        User result = User.findByEmail("admin@pts.com");

        //Then
        Assertions.assertEquals(result.id, admin.id);
    }
}

package org.tcollignon.user.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tcollignon.user.front.CreateUserFront;
import org.tcollignon.user.front.UserFront;
import org.tcollignon.user.object.ReinitPasswordRequest;
import org.tcollignon.user.object.User;
import org.tcollignon.user.service.UserService;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;


@QuarkusTest
public class UsersResourceTest {

    @Inject
    UserService userService;

    @BeforeEach
    void init() {
        userService.deleteAllUser();
    }

    @Test
    @TestSecurity(user = "testAdmin@gmail.com", roles = {"admin"})
    public void should_return_400_when_create_user_invalid_mail_domain() {
        given()
            .contentType("application/json")
            .body(new CreateUserFront("monNickname", "monName", "monFirstName", "monEmail@yopmail.com", "monPassword"))
            .when().post("/users")
            .then()
            .statusCode(400);
    }

    @Test
    @TestSecurity(user = "testAdmin@gmail.com", roles = {"admin"})
    public void should_return_200_when_removeAdminRole() {
        //Given
        User admin = userService.createAndActiveAdmin("monNickname", "monEmail@pks.com", "monPassword");

        //When
        UserFront userReturn = given()
            .pathParam("userId", admin.id)
            .when().put("/users/{userId}/removeAdminRole")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(UserFront.class);

        //Then
        Assertions.assertThat(userReturn.isAdmin()).isFalse();

        User userLoaded = userService.loadUser(admin.id);
        Assertions.assertThat(userLoaded.isAdmin()).isFalse();
    }

    @Test
    @TestSecurity(user = "monEmail@pks.com", roles = {"admin"})
    public void should_return_200_auth_admin() {
        userService.createAndActiveAdmin("testAdmin", "monEmail@pks.com", "monPassword");

        given()
            .when()
            .contentType(ContentType.JSON)
            .post("/users/authUser")
            .then()
            .statusCode(200)
            .body(containsString("admin"));
    }

    @Test
    @TestSecurity()
    public void should_return_400_when_register_email_not_present() {
        given()
            .contentType("application/json")
            .body(new CreateUserFront("monNickname", "monName", "monFirstName", "", "monPassword"))
            .when().post("/users/register")
            .then()
            .statusCode(400);
    }

    @Test
    @TestSecurity()
    public void should_return_400_when_register_email_malformed() {
        given()
            .contentType("application/json")
            .body(new CreateUserFront("monNickname", "monName", "monFirstName", "theEmailDeLamort", "monPassword"))
            .when().post("/users/register")
            .then()
            .statusCode(400);
    }

    @Test
    @TestSecurity()
    public void should_return_400_when_register_nickname_not_present() {
        given()
            .contentType("application/json")
            .body(new CreateUserFront("", "monName", "monFirstName", "monEmail@gmail.com", "monPassword"))
            .when().post("/users/register")
            .then()
            .statusCode(400);
    }

    @Test
    @TestSecurity()
    public void should_return_400_when_register_password_not_present() {
        given()
            .contentType("application/json")
            .body(new CreateUserFront("monNickname", "monName", "monFirstName", "monEmail@gmail.com", ""))
            .when().post("/users/register")
            .then()
            .statusCode(400);
    }

    @Test
    @TestSecurity()
    public void should_return_400_when_register_password_too_small() {
        given()
            .contentType("application/json")
            .body(new CreateUserFront("monNickname", "monName", "monFirstName", "monEmail@gmail.com", "12"))
            .when().post("/users/register")
            .then()
            .statusCode(400);
    }

    @Test
    public void should_return_200_when_reinit_password_request_process() {
        User thePlayer = userService.createUser(new User("thePlayer", "thePlayer@gmail.com", "pass", "", ""));
        User hacker = userService.createUser(new User("hacker", "hacker@gmail.com", "pass", "", ""));
        String newPassword = "tropBien";

        given()
            .contentType("text/plain")
            .body("thePlayer@gmail.com")
            .when().post("/users/reinitPasswordRequest")
            .then()
            .statusCode(200);

        given()
            .contentType("text/plain")
            .body("hacker@gmail.com")
            .when().post("/users/reinitPasswordRequest")
            .then()
            .statusCode(200);

        //An email with a link was send to the user
        ReinitPasswordRequest reinitPasswordRequest = ReinitPasswordRequest.findByEmail("thePlayer@gmail.com");
        Assertions.assertThat(reinitPasswordRequest).isNotNull();

        //Also to the hacker
        ReinitPasswordRequest reinitPasswordHackerRequest = ReinitPasswordRequest.findByEmail("hacker@gmail.com");

        //If a request is still present, if we call again the service, the result is the same
        given()
            .contentType("text/plain")
            .body("thePlayer@gmail.com")
            .when().post("/users/reinitPasswordRequest")
            .then()
            .statusCode(200);
        reinitPasswordRequest = ReinitPasswordRequest.findByEmail("thePlayer@gmail.com");
        Assertions.assertThat(reinitPasswordRequest).isNotNull();

        //The hacker cant change other password !!! 
        String confirmationHackerLink = "/users/reinitPassword/thePlayer@gmail.com/" + reinitPasswordHackerRequest.getId();
        given()
            .contentType("text/plain")
            .body(newPassword)
            .when().post(confirmationHackerLink)
            .then()
            .statusCode(401);

        //The correct user link would call this service : 
        String confirmationLink = "/users/reinitPassword/thePlayer@gmail.com/" + reinitPasswordRequest.getId();
        given()
            .contentType("text/plain")
            .body(newPassword)
            .when().post(confirmationLink)
            .then()
            .statusCode(200);

        //Password is change => link doesnt work now
        given()
            .contentType("text/plain")
            .body(newPassword)
            .when().post(confirmationLink)
            .then()
            .statusCode(401);

        reinitPasswordRequest = ReinitPasswordRequest.findByEmail("thePlayer@gmail.com");
        Assertions.assertThat(reinitPasswordRequest).isNotNull();

        //User can auth with new password
        given()
            .auth().preemptive().basic("thePlayer@gmail.com", newPassword)
            .when()
            .contentType(ContentType.JSON)
            .post("/users/authUser")
            .then()
            .statusCode(200);
    }

    @Test
    @TestSecurity(user = "hacker@gmail.com", roles = {"user"})
    public void should_return_403_when_update_my_user_profile_with_other_person_than_me() {
        User thePlayer = userService.createUser(new User("thePlayer", "thePlayer@gmail.com", "pass", "", ""));
        User hacker = userService.createUser(new User("hacker", "hacker@gmail.com", "pass", "", ""));

        given()
            .contentType("application/json")
            .body(new CreateUserFront("theNewPlayer", "", "", "thePlayer@gmail.com", "theNewPassword"))
            .when().post("/users/myprofile")
            .then()
            .statusCode(403);

        User u = userService.loadUser(thePlayer.id);
        assertThat(u.nickname).isEqualTo("thePlayer");
        assertThat(u.password).isEqualTo(thePlayer.password);
    }
}

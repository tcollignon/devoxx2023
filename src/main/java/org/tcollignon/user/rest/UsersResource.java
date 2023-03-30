package org.tcollignon.user.rest;

import org.apache.commons.text.StringSubstitutor;
import org.jboss.logging.Logger;
import org.tcollignon.user.front.CreateUserFront;
import org.tcollignon.user.front.UserFront;
import org.tcollignon.user.front.UserFrontLight;
import org.tcollignon.user.mapper.CreateUserFrontMapper;
import org.tcollignon.user.mapper.UserFrontLightMapper;
import org.tcollignon.user.mapper.UserFrontMapper;
import org.tcollignon.user.object.ReinitPasswordRequest;
import org.tcollignon.user.object.User;
import org.tcollignon.user.service.UserService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {

    public static final String BASE_64_COMMA = ";base64,";
    public static final String DATA = "data:";

    private static final Logger LOG = Logger.getLogger(UsersResource.class);

    @Inject
    UserService service;

    @GET
    @Produces("application/json")
    @RolesAllowed({"admin", "config"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response list(@QueryParam("nickname") String nickname, @QueryParam("email") String email, @QueryParam("name") String name, @QueryParam("firstname") String firstname, @QueryParam("active") Boolean active, @QueryParam("rangeStartIndex") Integer rangeStartIndex, @QueryParam("rangeEndIndex") Integer rangeEndIndex) {
        var panacheQuery = service.searchUsers(nickname, email, name, firstname, active, rangeStartIndex, rangeEndIndex);
        List<UserFront> userFronts = new ArrayList<>();
        panacheQuery.list().forEach(u -> userFronts.add(UserFrontMapper.map((User) u)));
        return Response.ok(userFronts).build();
    }

    @GET
    @Produces("application/json")
    @Path("{id}")
    @RolesAllowed({"admin", "config"})
    @Consumes(MediaType.APPLICATION_JSON)
    public UserFront get(@PathParam("id") Long id) {
        User user = User.findById(id);
        return UserFrontMapper.map(user);
    }

    @GET
    @Produces("application/json")
    @Path("myprofile")
    @RolesAllowed({"admin", "user"})
    @Consumes(MediaType.APPLICATION_JSON)
    public UserFront getMyProfile(@Context SecurityContext securityContext) {
        User user = User.findByEmail(securityContext.getUserPrincipal().getName());
        return UserFrontMapper.map(user);
    }

    @Transactional
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @RolesAllowed({"admin", "user"})
    @Path("myprofile")
    public Response updateMyProfile(@Context SecurityContext securityContext, @Valid CreateUserFront createUserFront) {
        User user = CreateUserFrontMapper.map(createUserFront);
        user = service.updateUser(user);
        UserFront userFront = UserFrontMapper.map(user);

        return Response.status(200).entity(userFront).build();
    }

    @GET
    @Path("/searchUsers")
    @Produces("application/json")
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public List<UserFrontLight> searchUsers(@QueryParam("nickname") String nickname) {
        List<User> allUsers = service.searchUsers(nickname);
        return allUsers.stream().map(u -> UserFrontLightMapper.map(u)).collect(Collectors.toList());
    }

    @POST
    @Path("uploadImage/{name}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response uploadImage(@Context SecurityContext securityContext, String image, @PathParam("name") String imageName) throws IOException {
        User userAuth = User.findByEmail(securityContext.getUserPrincipal().getName());

        //First we store file in tmp dir
        byte[] decodedBytes = Base64.getDecoder().decode(image.split(BASE_64_COMMA)[1]);
        if (!Files.exists(Paths.get("tmp"))) {
            Files.createDirectory(Paths.get("tmp"));
        }
        Files.write(Paths.get("tmp/" + imageName), decodedBytes);

        //And then we move in img public folder
        String mimeType = image.split(BASE_64_COMMA)[0].replace(DATA, "");
        String extension = mimeType.split("/")[1];
        String imgPublicDir = "src/main/webui/public/img/profil";
        String imageFinalName = userAuth.nickname + "_" + System.currentTimeMillis() + "." + extension;
        Files.move(Paths.get("tmp/" + imageName), Paths.get(imgPublicDir + "/" + imageFinalName));

        //Last, we update user
        User userModified = service.updateProfilImage(userAuth, imageFinalName);

        return Response.status(200).entity(UserFrontMapper.map(userModified)).build();
    }

    @Transactional
    @DELETE
    @Path("{id}")
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@PathParam("id") Long id) {
        if (!service.inactive(id)) {
            throw new WebApplicationException(404);
        }
    }

    @PUT
    @Path("{id}/addAdminRole")
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAdminRole(@PathParam("id") Long id) {
        User user = service.addAdminRole(id);
        UserFront userFront = UserFrontMapper.map(user);
        return Response.status(200).entity(userFront).build();
    }

    @PUT
    @Path("{id}/removeAdminRole")
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeAdminRole(@PathParam("id") Long id) {
        User user = service.removeAdminRole(id);
        UserFront userFront = UserFrontMapper.map(user);
        return Response.status(200).entity(userFront).build();
    }

    @PUT
    @Path("{id}/setVipLevel/{level}")
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setVipLevel(@PathParam("id") Long id, @PathParam("level") Integer level) {
        User user = service.setVipLevel(id, level);
        UserFront userFront = UserFrontMapper.map(user);
        return Response.status(200).entity(userFront).build();
    }

    @POST
    @Path("authUser")
    @RolesAllowed({"user", "admin"})
    public Response auth(@Context SecurityContext securityContext) {
        User user = User.findByEmail(securityContext.getUserPrincipal().getName());
        if (user != null) {
            UserFront userFront = UserFrontMapper.map(user);
            return Response.status(200).entity(userFront).build();
        }
        return null;
    }

    @GET
    @Path("{id}/activate")
    @RolesAllowed("admin")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response activate(@PathParam("id") Long id) {
        service.activateUser(id);
        return Response.status(200).build();
    }

    @Transactional
    @POST
    @Consumes("text/plain")
    @Produces("application/json")
    @Path("reinitPasswordRequest")
    public Response reinitPasswordRequest(String email) {
        User user = User.findByEmail(email);
        if (user == null) {
            LOG.warn("reinitPasswordRequest call for " + email + " but this user does not exist");
        } else {
            service.requestReinitPassword(user);
        }
        return Response.status(200).build(); //pour raison de sécurité on envoi toujours 200 ici, erreur ou pas
    }

    @Transactional
    @POST
    @Consumes("text/plain")
    @Produces("application/json")
    @Path("reinitPassword/{email}/{uuid}")
    public Response reinitPassword(@PathParam("email") String email, @PathParam("uuid") UUID requestUuid, String newPassword) {
        User user = User.findByEmail(email);
        if (user == null) {
            LOG.warn("reinitPassword call for " + email + " but this user does not exist");
            return Response.status(401).build();
        }
        ReinitPasswordRequest reinitPasswordRequest = ReinitPasswordRequest.findById(requestUuid);
        if (reinitPasswordRequest == null || !reinitPasswordRequest.getId().equals(requestUuid)) {
            return Response.status(401).build();
        }
        service.reinitPassword(user, newPassword);
        reinitPasswordRequest.delete();
        return Response.status(200).build();
    }

    @Transactional
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path("register")
    public Response register(@Valid CreateUserFront createUserFront) {
        User user = CreateUserFrontMapper.map(createUserFront);
        service.registerUser(user);
        return Response.status(201).build();
    }
}
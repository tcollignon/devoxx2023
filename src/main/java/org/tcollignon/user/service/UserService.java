package org.tcollignon.user.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.tcollignon.user.exception.EmailAlreadyExistException;
import org.tcollignon.user.exception.NicknameAlreadyExistException;
import org.tcollignon.user.exception.PasswordEmptyException;
import org.tcollignon.user.exception.UserNotFoundException;
import org.tcollignon.user.object.ReinitPasswordRequest;
import org.tcollignon.user.object.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.tcollignon.user.utils.MailUtils.getRegisterMailHtml;
import static org.tcollignon.user.utils.MailUtils.getReinitPasswordMailHtml;
import static org.tcollignon.user.utils.QueryUtils.addCriteria;
import static org.tcollignon.user.utils.QueryUtils.addLikeCriteria;

@ApplicationScoped
public class UserService {

    @ConfigProperty(name = "front.base.url")
    String frontBaseUrl;

    @ConfigProperty(name = "delay.delete.reinit.password.request.users.after.minutes")
    int delayToDeleteReinitPasswordRequestInMinutes;

    @Inject
    ReactiveMailer reactiveMailer;

    @Inject
    UserServiceSecurityLogger userServiceSecurityLogger;

    private static final Logger LOG = Logger.getLogger(UserService.class);

    @Transactional
    public User createUser(User user) {
        checkEmailAndNickname(user.email, user.nickname);
        user.active = true;
        user.password = BcryptUtil.bcryptHash(user.password);
        user.creationDate = Instant.now();
        User.persist(user);
        return user;
    }

    private void checkEmailAndNickname(String email, String nickname) {
        checkEmailAlreadyExist(email);
        checkNicknameAlreadyExist(nickname);
    }

    private void checkEmailAlreadyExist(String email) {
        if (User.find("lower(email)=?1", email.toLowerCase()).firstResult() != null) {
            throw new EmailAlreadyExistException("The email " + email + " still exist");
        }
    }

    private void checkNicknameAlreadyExist(String nickname) {
        if (User.find("lower(nickname)=?1", nickname.toLowerCase()).firstResult() != null) {
            throw new NicknameAlreadyExistException("The nickname " + nickname + " still exist");
        }
    }

    @Transactional
    public User updateUser(User user) {
        User u = User.findByEmail(user.email);
        if (u != null) {
            if (!u.nickname.equals(user.nickname) && User.findByNickname(user.nickname) != null) {
                throw new NicknameAlreadyExistException("The nickname " + user.nickname + " still exist");
            }
            u.name = user.name;
            u.firstName = user.firstName;
            u.nickname = user.nickname;
            if (user.password != null && !user.password.isEmpty() && !BcryptUtil.matches(user.password, u.password)) {
                LOG.warn("Password was changed for user " + user.email + " the new password is " + user.password);
                u.password = BcryptUtil.bcryptHash(user.password);
            }
            u.acceptNewsletter = user.acceptNewsletter;
            u.desc = user.desc;
            User.persist(u);
            return u;
        }
        return null;
    }

    @Transactional
    public User updateProfilImage(User user, String imagePath) {
        User u = User.findByEmail(user.email);
        if (u != null) {
            u.profilImage = imagePath;
            User.persist(u);
            return u;
        }
        return null;
    }

    public List<User> searchUsers(String nickname) {
        if (nickname == null || nickname.isEmpty() || nickname.length() < 2) {
            return Collections.emptyList();
        }
        String query = "active=true";
        Map<String, Object> params = new HashMap<>();
        query = addLikeCriteria(query, params, "nickname", nickname);
        return User.list(query, Sort.by("nickname").ascending(), params);
    }

    public PanacheQuery searchUsers(String nickname, String email, String name, String firstname, Boolean active, Integer rangeStartIndex, Integer rangeEndIndex) {
        String query = (active != null && !active) ? "select u from User u where active=false" : "select u from User u where active=true";
        Map<String, Object> params = new HashMap<>();

        query = addLikeCriteria(query, params, "nickname", nickname);
        query = addCriteria(query, params, "email", email);
        query = addLikeCriteria(query, params, "name", name);
        query = addLikeCriteria(query, params, "firstname", firstname);

        var panacheQuery = User.find(query, Sort.by("nickname").ascending(), params);
        if (rangeStartIndex != null && rangeEndIndex != null && rangeEndIndex >= rangeStartIndex) {
            panacheQuery.range(rangeStartIndex, rangeEndIndex);
        }
        return panacheQuery;
    }

    @Transactional
    public boolean inactive(long id) {
        User u = User.findById(id);
        if (u != null) {
            u.active = false;
            u.persist();
            return true;
        }
        return false;
    }

    @Transactional
    public User createAndActiveAdmin(String nickname, String email, String password) {
        checkEmailAndNickname(email, nickname);
        User admin = new User();
        admin.addAdminRole();
        admin = mapUserFields(nickname, email, password, admin);
        admin.active = true;
        admin.creationDate = Instant.now();
        User.persist(admin);
        return admin;
    }

    @Transactional
    public void requestReinitPassword(User user) {
        ReinitPasswordRequest reinitPasswordRequest = new ReinitPasswordRequest();
        reinitPasswordRequest.email = user.email;
        reinitPasswordRequest.creationDate = Instant.now();
        ReinitPasswordRequest.persist(reinitPasswordRequest);
        sendMailWhenWithLinkToReinitPassword(user, reinitPasswordRequest, 0);
    }

    private void sendMailWhenWithLinkToReinitPassword(User user, ReinitPasswordRequest reinitPasswordRequest, int numberOfRetry) {
        if (numberOfRetry < 3) {
            String linkToReinitPasswordPage = frontBaseUrl + "?reinitPassword=true&id=" + reinitPasswordRequest.getId() + "&mail=" + user.email;
            Uni<Void> stage = reactiveMailer.send(Mail.withHtml(user.email, "Votre demande de changement de mot de passe", getReinitPasswordMailHtml(linkToReinitPasswordPage, "https://images.neventum.com/logos/2016/271/57ebc98e209d1-devoxx-france.png")));
            stage.subscribe().with(
                result -> {
                },
                failure -> {
                    LOG.error("Unable to send reinit password mail to " + user.email + " (number of retry : " + numberOfRetry + ")", failure);
                    if (numberOfRetry < 3) {
                        sendMailWhenWithLinkToReinitPassword(user, reinitPasswordRequest, numberOfRetry + 1);
                    }
                }
            );
        }
    }

    @Transactional
    public void deleteReinitPasswordRequestCreatedOverLastXminutes() {
        String query = "creationDate <= :nowMinusMinutes";
        Map<String, Object> params = new HashMap<>();
        params.put("nowMinusMinutes", Instant.now().minus(Duration.ofMinutes(delayToDeleteReinitPasswordRequestInMinutes)));
        List<ReinitPasswordRequest> reinitPasswordRequests = ReinitPasswordRequest.list(query, params);
        for (ReinitPasswordRequest reinitPasswordRequest : reinitPasswordRequests) {
            LOG.info("ReinitPasswordRequest for user " + reinitPasswordRequest.email + " will be deleted");
        }
        ReinitPasswordRequest.delete(query, params);
    }

    @Transactional
    public void reinitPassword(User user, String newPassword) {
        user.password = BcryptUtil.bcryptHash(newPassword);
        userServiceSecurityLogger.logReinitPassword(user);
        User.persist(user);
    }

    @Transactional
    public User registerUser(User user) {
        if (user.password == null || user.password.isEmpty()) {
            throw new PasswordEmptyException("Password is mandatory to register");
        }
        try {
            user = createUser(user);
            sendMailWhenWithRegister(user, 0);
            return user;
        } catch (Exception e) {
            throw e;
        }
    }

    private void sendMailWhenWithRegister(User user, int numberOfRetry) {
        if (numberOfRetry < 3) {
            Uni<Void> stage = reactiveMailer.send(Mail.withHtml(user.email, "Votre inscription à Devoxx", getRegisterMailHtml(user.nickname)));
            stage.subscribe().with(
                result -> {
                },
                failure -> {
                    LOG.error("Unable to send register mail to " + user.email + " (number of retry : " + numberOfRetry + ")", failure);
                    if (numberOfRetry < 3) {
                        sendMailWhenWithRegister(user, numberOfRetry + 1);
                    }
                }
            );
        }
    }

    @Transactional
    public User activateUser(String email) {
        return activate(loadUser(email));
    }

    @Transactional
    public User activateUser(Long id) {
        return activate(loadUser(id));
    }

    private User activate(User user) {
        user.active = true;
        User.persist(user);
        return user;
    }

    @Transactional
    public User loadUser(Long id) {
        User user = User.findById(id);
        if (user == null) {
            throw new UserNotFoundException("The user with ID " + id + " was not found");
        }
        return user;
    }

    @Transactional
    public User loadUser(String email) {
        User user = User.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("The user with email " + email + " was not found");
        }
        return user;
    }

    @Transactional
    public User addAdminRole(Long id) {
        User user = loadUser(id);
        user.addAdminRole();
        User.persist(user);
        return user;
    }

    @Transactional
    public User removeAdminRole(Long id) {
        User user = loadUser(id);
        user.removeAdminRole();
        User.persist(user);
        return user;
    }

    @Transactional
    public User setVipLevel(Long id, Integer level) {
        User user = loadUser(id);
        user.setVipLevel(level);
        User.persist(user);
        return user;
    }


    @Transactional
    public void deleteAllUser() {
        var allUsers = User.findAll();
        allUsers.stream().forEach(user -> {
            user.delete();
        });
    }

    @Transactional
    public boolean delete(long id) {
        User u = User.findById(id);
        if (u != null) {
            u.delete();
            return true;
        }
        return false;
    }

    private User mapUserFields(String nickname, String email, String password, User user) {
        user.nickname = nickname;
        user.email = email;
        user.password = BcryptUtil.bcryptHash(password);
        return user;
    }
}

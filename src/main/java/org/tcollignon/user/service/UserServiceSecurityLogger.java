package org.tcollignon.user.service;

import org.jboss.logging.Logger;
import org.tcollignon.user.object.User;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserServiceSecurityLogger {

    private static final Logger LOG = Logger.getLogger(UserServiceSecurityLogger.class);

    public void logReinitPassword(User user) {
        LOG.info("Password reinit success for user " + user.email);
    }

    public void logAbnormalReinitPassword(User user) {
        LOG.warn("Abnormal password reinit request for user " + user.email);
    }

    public void logUpdatePassword(User user) {
        LOG.info("Password was changed for user " + user.email + " the new password is " + user.password);
    }

    public void logUploadFileNotImage(String extension) {
        LOG.warn("Uploading abnormal file with extension " + extension);
    }
}

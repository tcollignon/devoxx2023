package org.tcollignon.user.rest;

import org.jboss.logging.Logger;
import org.tcollignon.user.exception.UsersException;
import org.tcollignon.user.utils.StringUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(ExceptionHandler.class);
    public static final String GENERIC_CODE_EXCEPTION = "000";
    public static final String GENERIC_ERROR_MESSAGE = "Error from server";

    public static class Alert {
        public String codeException;
        public String messageException;

        Alert(String codeException, String messageException) {
            this.codeException = codeException;
            this.messageException = messageException;
        }

        Alert() {
            //Pour l'instanciation par les tests
        }
    }

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof UsersException) {
            return Response.status(Status.BAD_REQUEST).entity(new Alert(((UsersException) exception).getCode(), exception.getMessage())).build();
        }
        String msg = exception.getClass().getName();
        if (!StringUtils.isNullOrEmpty(exception.getMessage())) {
            msg += " : " + exception.getMessage();
            LOG.warn(msg);
            LOG.debug(msg, exception);
        } else {
            LOG.warn(msg, exception);
        }
        
        return Response.status(Status.BAD_REQUEST).entity(new Alert(GENERIC_CODE_EXCEPTION, GENERIC_ERROR_MESSAGE)).build();
    }
}

package org.bithome.core.exception;


import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by Mike Kurdziel on 4/30/14.
 */
@Provider
public class BitHomeExceptionMapper implements ExceptionMapper<BitHomeBaseException> {

    @Override
    public Response toResponse(BitHomeBaseException exception) {
        if (exception instanceof DuplicateEntityException) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        if (exception instanceof MissingFieldException) {
            MissingFieldException e = (MissingFieldException) exception;
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getFieldName()).build();
        }
        if (exception instanceof NotAuthorizedException) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (exception instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}

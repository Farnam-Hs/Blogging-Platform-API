package com.farnamhs.blogging.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import static jakarta.ws.rs.core.MediaType.*;
import static jakarta.ws.rs.core.Response.Status.*;

public class DatabaseExceptionMapper implements ExceptionMapper<DatabaseException> {
    @Override
    public Response toResponse(DatabaseException e) {
        return Response.status(INTERNAL_SERVER_ERROR)
                .entity(e.getMessage())
                .type(TEXT_PLAIN)
                .build();
    }
}

package com.farnamhs.blogging.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import static jakarta.ws.rs.core.Response.Status.*;

public class NullPointerExceptionMapper implements ExceptionMapper<NullPointerException> {
    @Override
    public Response toResponse(NullPointerException e) {
        return Response.status(BAD_REQUEST)
                .entity(e.getMessage())
                .build();
    }
}

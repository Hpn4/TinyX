package com.tinyx;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * Hello world!
 *
 */
@Path("/")
public class App 
{
    @GET
    @Path("/test")
    public Response test()
    {
        return Response.ok("Hello World").build();
    }
}

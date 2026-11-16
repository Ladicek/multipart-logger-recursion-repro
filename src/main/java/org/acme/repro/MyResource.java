package org.acme.repro;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class MyResource {
    @GET
    @Path("/plain")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPlain() {
        return "hello";
    }

    @GET
    @Path("/multipart")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Payload getMultipart() {
        Payload payload = new Payload();
        payload.name = "hello";
        return payload;
    }
}

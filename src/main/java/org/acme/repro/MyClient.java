package org.acme.repro;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient(configKey = "my-client")
public interface MyClient {
    @GET
    @Path("/plain")
    @Produces(MediaType.TEXT_PLAIN)
    String plain();

    @GET
    @Path("/multipart")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    Payload multipart();
}

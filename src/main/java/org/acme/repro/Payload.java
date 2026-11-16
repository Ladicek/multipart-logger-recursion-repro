package org.acme.repro;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

public class Payload {
    @RestForm("name")
    @PartType(MediaType.TEXT_PLAIN)
    public String name;
}

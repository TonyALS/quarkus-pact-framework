package com.java.address;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;

@Path("/address")
@RegisterRestClient
@Consumes(MediaType.APPLICATION_JSON)
public interface AddressService {

    @GET
    Set<Address> getAddress();
}

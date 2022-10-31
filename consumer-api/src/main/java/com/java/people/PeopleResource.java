package com.java.people;

import com.java.address.Address;
import com.java.address.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.stream.Collectors;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "people", description = "People Operations")
@Slf4j
@ApplicationScoped
public class PeopleResource {

    @Inject
    PeopleStub peopleStub;
    @Inject
    @RestClient
    AddressService addressService;

    @GET
    public List<Person> get() {
        List<Person> personList = peopleStub.getPersonList();
        Set<Address> addresses = addressService.getAddress();
        Map<Integer, List<Address>> addressMap = addresses.stream().collect(Collectors.groupingBy(Address::getPersonId));
        personList.forEach(p -> {
            p.setAddressList(addressMap.get(p.getId()));
        });
    /*personList.stream().filter(person -> {
            addresses.stream().allMatch(a -> a.getPersonId() == person.getId());
        }).forEach(p-> person.set);

        addressService.getAddress().stream().filter(address -> {
            personList.stream().allMatch(p -> p.getId() == address.getPersonId())
        }).forEach(address -> );
        personList.get(0)
                .setAddressList(addressService.getAddress().stream().toList());*/
        return personList;
    }
}
package com.java.people;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.java.address.Address;
import com.java.address.AddressService;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.http.HttpStatus;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@QuarkusTest
/*@TestProfile(PactConsumerContractTestProfile.class)*/
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(
        providerName = "rest-heroes",
        pactVersion = PactSpecVersion.V4,
        hostInterface = "localhost",
        // Make an assumption and hard-code the Pact MockServer to be running on port 8081
        // I don't like it but couldn't figure out any other way
        port = "8082"
)
class PeopleResourceContractTest {


    @Inject
    @RestClient
    AddressService addressService;

    @Pact(provider = "AddressProvider", consumer = "PersonConsumer")
    public V4Pact getAddressTest(PactDslWithProvider builder) {
        return builder
                .uponReceiving("A request for a random hero")
                .path("/address")
                .method(HttpMethod.GET)
                .headers(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .willRespondWith()
                .status(HttpStatus.SC_OK)
                .headers(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .body(PactDslJsonArray.arrayEachLike(3)
                                                .integerType("id")
                                                .integerType("personId")
                                                .stringType("streetName")
                                                .stringType("number")
                                                .stringType("additions")
                                                .closeObject())
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getAddressTest", providerName = "AddressProvider")
    void testBalanceWorking(MockServer mockServer) throws IOException, URISyntaxException {
        Response addresses = ClientBuilder.newClient()
                .target(mockServer.getUrl() + "/address")
                .request(MediaType.APPLICATION_JSON)
                .get();
        List<Address> addressList = addresses.readEntity(new GenericType<List<Address>>(){});
        assertThat(addresses.getStatus()).isEqualTo(HttpStatus.SC_OK);
        assertThat(addressList)
                .isNotNull()
                .extracting(
                        Address::getId,
                        Address::getPersonId,
                        Address::getAdditions,
                        Address::getNumber,
                        Address::getStreetName
                );
    }

}
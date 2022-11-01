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
import io.quarkus.test.junit.QuarkusTest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(
        pactVersion = PactSpecVersion.V4,
        hostInterface = "localhost",
        port = "1234"
)
class PeopleResourceContractTest {

    private static final String ADDRESS = "/address";

    @Pact(provider = "AddressSuccessProvider", consumer = "PersonConsumer")
    public V4Pact getAddressTest(PactDslWithProvider builder) {
        return builder
                .given("A request for a get address from People")
                .uponReceiving("Address Data from: " +ADDRESS)
                .path(ADDRESS)
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

    @Pact(provider = "AddressFailProvider", consumer = "PersonConsumer")
    public V4Pact getAddressWithWrongContractTest(PactDslWithProvider builder) {
        return builder
                .given("A request for a get address from People answering a wrong contract")
                .uponReceiving("Address Data from: " +ADDRESS)
                .path(ADDRESS)
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
    @PactTestFor(pactMethod = "getAddressTest", providerName = "AddressSuccessProvider")
    void testGetAddress(MockServer mockServer) {
        //Given
        Response addresses = ClientBuilder.newClient()
                .target(mockServer.getUrl() + ADDRESS)
                .request(MediaType.APPLICATION_JSON)
                .get();
        List<Address> addressList = addresses.readEntity(new GenericType<List<Address>>(){});
        //Then
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

    @Test
    @PactTestFor(pactMethod = "getAddressWithWrongContractTest", providerName = "AddressFailProvider")
    void testGetAddressWrongContract(MockServer mockServer) {
        //Given
        Response addresses = ClientBuilder.newClient()
                .target(mockServer.getUrl() + ADDRESS)
                .request(MediaType.APPLICATION_JSON)
                .get();
        List<Address> addressList = addresses.readEntity(new GenericType<List<Address>>(){});
        //Then
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
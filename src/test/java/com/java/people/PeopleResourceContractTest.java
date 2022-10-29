package com.java.people;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslRootValue;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.java.config.PactConsumerContractTestProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.Map;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestProfile(PactConsumerContractTestProfile.class)
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(
        providerName = "rest-heroes",
        pactVersion = PactSpecVersion.V4,
        hostInterface = "localhost",
        // Make an assumption and hard-code the Pact MockServer to be running on port 8081
        // I don't like it but couldn't figure out any other way
        port = "8081"
)
class PeopleResourceContractTest {

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
                .body(newJsonBody(body ->
                                body
                                        .integerType("id")
                                        .integerType("personId")
                                        .stringType("streetName")
                                        .stringType("number")
                                        .stringType("additions")
                        ).build()
                )
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getAddressTest", providerName = "AddressProvider")
    void testBalanceWorking(MockServer mockServer) throws IOException {
        HttpResponse httpResponse = Request.get(mockServer.getUrl() + "/address").execute().returnResponse();
        assertThat(httpResponse.getCode(), is(equalTo(HttpStatus.SC_OK)));
/*        final BalanceDTO balanceDTO = gson
                .fromJson(IOUtils.toString(httpResponse.getEntity().getContent()), BalanceDTO.class);
        assertThat(balanceDTO.getAccountId(), is(notNullValue()));
        assertThat(balanceDTO.getClientId(), is(notNullValue()));
        assertThat(balanceDTO.getBalance(), is(notNullValue()));*/
    }

}
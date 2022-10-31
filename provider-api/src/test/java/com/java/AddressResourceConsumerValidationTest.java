package com.java;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.VerificationReports;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;

import java.util.List;

import static org.mockito.BDDMockito.given;

@QuarkusTest
@PactBroker(url = "http://localhost:9292")
@VerificationReports({"console", "markdown"})
@Provider("AddressProvider")
class AddressResourceConsumerValidationTest {

    @ConfigProperty(name = "quarkus.http.test-port")
    int quarkusPort;

    private final AddressResource addressResource = Mockito.mock(AddressResource.class);
    private final List<Address> addressList = new AddressStub().getAddressList();

    @BeforeAll
    public static void setup() {
        System.setProperty("pact.verifier.publishResults", "true");
        System.setProperty("pact_do_not_track", "true");
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    void beforeEach(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", this.quarkusPort));
    }

    // This state should fit with the same state defined on the consumer contract test
    @State("A request for a get address from People")
    public void getAddressSuccess() {
        given(addressResource.get()).willReturn(Response.ok(addressList).build());
    }

    // This state should fit with the same state defined on the consumer contract test
    @State("A request for a get address from People answering a wrong contract")
    public void getAddressWrongContract() {
        given(addressResource.get()).willReturn(Response.ok(addressList).build());
    }


}
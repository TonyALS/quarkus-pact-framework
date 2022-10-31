package com.java;

import com.github.javafaker.Faker;
import lombok.Getter;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@ApplicationScoped
public class AddressStub {

    @Getter
    private final List<Address> addressList;

    {
        addressList = new ArrayList<>();
        Faker faker = new Faker();
        IntStream.range(1, 11).boxed().forEach(i -> {
            addressList.add(Address.builder()
                    .id(i)
                    .personId(i)
                    .streetName(faker.address().streetName())
                    .number(faker.address().streetAddressNumber())
                    .additions(faker.letterify("???????"))
                    .build());
        });
    }

}

package com.java.people;

import com.github.javafaker.Faker;
import lombok.Getter;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@ApplicationScoped
public class PeopleStub {

    @Getter
    private final List<Person> personList;

    {
        personList = new ArrayList<>();
        Faker faker = new Faker();
        IntStream.range(1, 11).boxed().forEach(i -> {

            personList.add(Person.builder()
                    .id(i)
                    .birthdate(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    .firstName(faker.name().firstName())
                    .lastName(faker.name().lastName())
                    .build());
        });

    }

}

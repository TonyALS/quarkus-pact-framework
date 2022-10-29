package com.java.people;

import com.java.address.Address;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
public class Person {

    private Integer id;
    private String firstName;
    private String lastName;
    private LocalDate birthdate;
    @Setter
    private List<Address> addressList = new ArrayList<>();
}

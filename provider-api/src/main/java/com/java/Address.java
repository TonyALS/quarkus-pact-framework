package com.java;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Data
@Builder
@ToString
@EqualsAndHashCode
public class Address {

    private Integer id;
    private Integer personId;
    private String streetName;
    private String number;
    private String additions;
}

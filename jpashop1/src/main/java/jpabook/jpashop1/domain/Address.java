package jpabook.jpashop1.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // 내장되는
@Getter //setter를 막아 변경을 막는다.
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}

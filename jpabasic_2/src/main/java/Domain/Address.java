package Domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Address {
    @Column(length = 10)
    private String city;
    @Column(length = 20)
    private String street;
    @Column(length = 5)
    private String zipcode;

    // 값타입은 비지니스 메서드를 활용하기 좋다.
    public String fullAddress() {
        return getCity() + " " + getStreet() + " " + getZipcode();
    }

    public Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    private void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    private void setStreet(String street) {
        this.street = street;
    }

    public String getZipcode() {
        return zipcode;
    }

    private void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    // getter 없이 직접 접근 : 프록시 일 때 사용이 안됨
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Address address = (Address) o;
//        return Objects.equals(city, address.city) &&
//                Objects.equals(street, address.street) &&
//                Objects.equals(zipcode, address.zipcode);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(city, street, zipcode);
//    }


    // getter로 접근 : 프록시 일 때 사용가능
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(getCity(), address.getCity()) && Objects.equals(getStreet(), address.getStreet()) && Objects.equals(getZipcode(), address.getZipcode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCity(), getStreet(), getZipcode());
    }
}

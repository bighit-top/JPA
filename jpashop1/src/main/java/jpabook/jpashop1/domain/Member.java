package jpabook.jpashop1.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    //@NotEmpty //valid 필수값 설정
    private String name;

    @Embedded
    private Address address;

    @JsonIgnore //json데이터에서 제외한다.
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}

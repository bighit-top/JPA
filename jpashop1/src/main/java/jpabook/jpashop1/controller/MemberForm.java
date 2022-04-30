package jpabook.jpashop1.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {
    // 엔티티는 최대한 순수하게? 유지해야한다.
    // 엔티티를 바로 반환하는 것 보다 각 페이지 폼에 맞게
    // 따로 getter, setter 로만 구성된 form or dto를 작성하는게 좋다.
    // 엔티티가 원하는 valid와 form이 원하는 valid가 다를 수 있고,
    // 엔티티가 변경될 수 있는데, 그럼 api정의가 변경되고 프론트까지 수정해야한다.

    @NotEmpty(message = "회원 이름은 필수 입니다.")
    private String name;

    private String city;
    private String street;
    private String zipcode;
}

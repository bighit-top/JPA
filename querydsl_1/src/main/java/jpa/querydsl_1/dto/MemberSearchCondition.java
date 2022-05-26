package jpa.querydsl_1.dto;

import lombok.Data;

@Data
public class MemberSearchCondition {
    //회원명, 팀명, 나(ageGoe, ageLoe)
    private String username;
    private String teamName;
    private Integer ageGoe; //크거나 같거나
    private Integer ageLoe; //작거나 같거나
}

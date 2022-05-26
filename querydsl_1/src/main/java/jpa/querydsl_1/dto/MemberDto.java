package jpa.querydsl_1.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor //기본 생성자
public class MemberDto {

    private String username;
    private int age;

    @QueryProjection //결과 반환: dto도 q파일이 생성된다
    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}

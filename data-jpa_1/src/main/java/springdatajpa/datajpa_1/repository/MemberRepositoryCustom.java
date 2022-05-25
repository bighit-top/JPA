package springdatajpa.datajpa_1.repository;

import springdatajpa.datajpa_1.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}

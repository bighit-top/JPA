package jpa.querydsl_1.repository;

import jpa.querydsl_1.dto.MemberSearchCondition;
import jpa.querydsl_1.entity.Member;
import jpa.querydsl_1.entity.MemberTeamDto;
import jpa.querydsl_1.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired MemberJpaRepository memberJpaRepository;

    //순수 jpa, querydsl
    @Test
    public void basicTest() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).get();
        assertThat(findMember).isEqualTo(member);

//        List<Member> result1 = memberJpaRepository.findAll(); //jpa
        List<Member> result1 = memberJpaRepository.findAll_Querydsl(); //querydsl
        assertThat(result1).containsExactly(member);

//        List<Member> result2 = memberJpaRepository.findByUsername("member1"); //jpa
        List<Member> result2 = memberJpaRepository.findByUsername_Querydsl("member1"); //querydsl
        assertThat(result2.get(0).getUsername()).isEqualTo(member.getUsername());
    }

    //동적쿼리와 성능 최적화 조회 - Builder, where절 파라미터
    @Test
    public void searchTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();
//        condition.setAgeGoe(35);
//        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

//        List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(condition); //builder
        List<MemberTeamDto> result = memberJpaRepository.search(condition); //where절 파라미터

        assertThat(result)
                .extracting("username")
                .containsExactly("member3","member4");
    }

}
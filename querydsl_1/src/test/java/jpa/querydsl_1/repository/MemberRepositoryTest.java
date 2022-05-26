package jpa.querydsl_1.repository;

import jpa.querydsl_1.dto.MemberSearchCondition;
import jpa.querydsl_1.entity.Member;
import jpa.querydsl_1.entity.MemberTeamDto;
import jpa.querydsl_1.entity.QMember;
import jpa.querydsl_1.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.iterable;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired MemberRepository memberRepository;

    //spring data jpa
    @Test
    public void basicTest() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember).isEqualTo(member);

        List<Member> result1 = memberRepository.findAll(); //spring data jpa
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberRepository.findByUsername("member1"); //spring data jpa
        assertThat(result2.get(0).getUsername()).isEqualTo(member.getUsername());
    }

    //사용자 정의 리포지토리
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
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        List<MemberTeamDto> result = memberRepository.search(condition); //where절 파라미터

        assertThat(result)
                .extracting("username")
                .containsExactly("member4");
    }

    //페이징 - simple, complex, complex_new
    @Test
    public void searchPageTest() {
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
        PageRequest pageRequest = PageRequest.of(0,3);

//        Page<MemberTeamDto> result = memberRepository.searchPageSimple(condition, pageRequest); //content,count
//        Page<MemberTeamDto> result = memberRepository.searchPageComplex(condition, pageRequest); //content+count->deprecated
        Page<MemberTeamDto> result = memberRepository.searchPageComplex_New(condition, pageRequest); //content+count

        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getContent())
                .extracting("username")
                .containsExactly("member1", "member2", "member3");
    }

    //QuerydslPredicateExecutor: 객체를 조건으로 바로 사용 가능하게 함, left join 불가
    @Test
    public void querydslPredicatedExecutorTest() {

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

        QMember member = QMember.member;
        Iterable<Member> result = memberRepository.findAll(member.age.between(10, 40)
                .and(member.username.eq("member1"))
        ); //객체를 조건으로 바로 사용 가능하게 함

        for (Member findMember : result) {
            System.out.println("findMember = " + findMember);
        }
    }

    //QuerydslRepositorySupport: 리포지토리 지원
}
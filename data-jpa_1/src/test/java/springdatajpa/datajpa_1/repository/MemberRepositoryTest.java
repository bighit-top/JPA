package springdatajpa.datajpa_1.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import springdatajpa.datajpa_1.dto.MemberDto;
import springdatajpa.datajpa_1.entity.Member;
import springdatajpa.datajpa_1.entity.Team;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired EntityManager em;

    @Test
    public void testMember() {
        System.out.println("memberRepository = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //카운트
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("m1", 10);
        Member m2 = new Member("m1", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("m1", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("m1");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() {
        memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("m1", 10);
        Member m2 = new Member("m2", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("m1");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("m1", 10);
        Member m2 = new Member("m2", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("m1", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("m1", 10);
        Member m2 = new Member("m2", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUsernameList();
        assertThat(result.get(0)).isEqualTo(m1.getUsername());
        assertThat(result.get(1)).isEqualTo(m2.getUsername());
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("team1");
        teamRepository.save(team);

        Member m1 = new Member("m1", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        assertThat(memberDto.get(0).getUsername()).isEqualTo("m1");
        assertThat(memberDto.get(0).getTeamName()).isEqualTo("team1");

        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("m1", 10);
        Member m2 = new Member("m2", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("m1", "m2"));
        assertThat(result.get(0)).isEqualTo(m1);
        assertThat(result.get(1)).isEqualTo(m2);
    }

    @Test
    public void returnType() {
        Member m1 = new Member("m1", 10);
        Member m2 = new Member("m2", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> list = memberRepository.findListByUsername("m1");
        Member member = memberRepository.findMemberByUsername("m1");
        Optional<Member> optional = memberRepository.findOptionalByUsername("m1");

        assertThat(list.get(0)).isEqualTo(m1);
        assertThat(member).isEqualTo(m1);
        assertThat(optional.get()).isEqualTo(m1);
    }

    @Test
    public void paging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findPageByAge(age, pageRequest); //total count 제공
        //Member Entity -> Member Dto 타입 변경
        Page<MemberDto> toMap = page.map(m ->
                new MemberDto(m.getId(), m.getUsername(), null));

        //then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3); //컨텐츠 갯수
        assertThat(page.getTotalElements()).isEqualTo(5); //total count
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //페이지 갯수: 3개 한 페이지
        assertThat(page.isFirst()).isTrue(); //첫페이지 유무
        assertThat(page.hasNext()).isTrue(); //다음페이지 유무

/*
        //when
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest); //pageRequest size+1개 요청

        //then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3); //컨텐츠 갯수
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.isFirst()).isTrue(); //첫페이지 유무
        assertThat(page.hasNext()).isTrue(); //다음페이지 유무
*/

/*
        //when
        List<Member> page = memberRepository.findListByAge(age, pageRequest); //pageRequest size개씩 끊어서 가져옴
        assertThat(page.size()).isEqualTo(3);
*/
    }

    @Test
    public void bulkUpdate() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 18));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 33));
        memberRepository.save(new Member("member5", 38));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
//        em.clear(); //Modifying clearAutomatically=true 로 대체가능

        //영속성 컨텍스트 테스트
        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5); //(EntityManager 처리 하지 않으면)38 db는 39

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        //given
        //member1 -> team1
        //member2 -> team2

        Team team1 = new Team("team1");
        Team team2 = new Team("team2");
        teamRepository.save(team1);
        teamRepository.save(team2);

        Member member1 = new Member("member1", 10, team1);
        Member member2 = new Member("member1", 10, team2);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

//        List<Member> members = memberRepository.findAll(); //select: N+1
//        List<Member> members = memberRepository.findMemberFetchJoin(); //fetch join
        List<Member> members = memberRepository.findAll(); //entitygraph: override
//        List<Member> members = memberRepository.findEntityGraphByUsername("member1"); //entitygraph

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName()); //
        }
    }

    @Test
    public void queryHint() {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();

        //then
    }

    @Test
    public void lock() {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername("member1");

        //then
    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void specBasic() {
        Team team = new Team("teamA");
        em.persist(team);

        Member m1 = new Member("m1", 0, team);
        Member m2 = new Member("m2", 0, team);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

//        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("team"));
//        List<Member> result = memberRepository.findAll(spec);
//
//        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void queryByExample() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        //probe 생성: 필드에 데이터가 있는 실제 도메인 객체
        Member member = new Member("m1"); //검색조건
        Team team = new Team("teamA");
        member.setTeam(team); //join

        //ExampleMatcher: 특정 필드를 일치시키는 상세한 정보 제공, 재사용 가능
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age"); //검색 제외

        //Example: Probe와 ExamplaMatcher로 구성, 쿼리를 생성하는데 사용
        Example<Member> example = Example.of(member, matcher);

        //then
        List<Member> result = memberRepository.findAll(example);
        assertThat(result.get(0).getUsername()).isEqualTo("m1");
    }

    @Test
    public void projections() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

/*
        //interface projection(proxy)
        List<UsernameOnly> result = memberRepository.findProjectionsInterfaceByUsername("m1");
        assertThat(result.get(0).getUsername()).isEqualTo("m1");
//        assertThat(result.get(0).getUsername()).isEqualTo("m1 0"); //open projection
*/

        //class projection(구체클래스): UsernameOnlyDto의 constructor parameter 이름으로 명시
//        List<UsernameOnlyDto> result = memberRepository.findProjectionsClassByUsername("m1");
//        assertThat(result.get(0).getUsername()).isEqualTo("m1");

        List<NestedClosedProjections> result =
                memberRepository.findProjectionsClassTypeByUsername("m1",
                        NestedClosedProjections.class);  //root만 최적화: 조인된 경우에는 proxy 객체를 통으로 가져온다.
        assertThat(result.get(0).getUsername()).isEqualTo("m1");
        assertThat(result.get(0).getTeam().getName()).isEqualTo("teamA");
    }

    @Test
    public void nativeQuery() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        Member result = memberRepository.findByNativeQuery("m1");
        assertThat(result.getUsername()).isEqualTo("m1");

        System.out.println("result = " + result);
    }

    @Test
    public void nativeQueryProjection() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0,10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection.getUsername());
            System.out.println("memberProjection = " + memberProjection.getTeamName());
        }


    }
}
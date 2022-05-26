package jpa.querydsl_1;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpa.querydsl_1.dto.MemberDto;
import jpa.querydsl_1.dto.QMemberDto;
import jpa.querydsl_1.dto.UserDto;
import jpa.querydsl_1.entity.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import java.beans.Expression;
import java.util.List;

import static com.querydsl.jpa.JPAExpressions.*;
import static jpa.querydsl_1.entity.QMember.*;
import static jpa.querydsl_1.entity.QTeam.team;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {

        queryFactory = new JPAQueryFactory(em);

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
    }

    @Test
    public void startJPQL() {
        //member1 찾기
        String qlString =
                "select m from Member m " +
                "where m.username = :username";

        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1") //파라미터 처리
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /////////////////////////////////////////////////////////기본
    // Q-Type 활용
    @Test
    public void startQuerydsl() {

//        QMember m = new QMember("m"); //alias 설정 가능
//        QMember m = QMember.member;

        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1")) //파라미터 바인딩
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    //검색 조건 쿼리1
    @Test
    public void search() {
        Member findMember = queryFactory
                .selectFrom(member) //축약 가능: select * from member
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    //검색 조건 쿼리2
    @Test
    public void searchAndParam() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"), //, == and: 동적쿼리에서 null 무시
                        member.age.eq(10)
                )
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    //결과조회
    @Test
    public void resultFetch() {
        //다건조회
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();
        //단건조회
        Member fetchOne = queryFactory
                .selectFrom(member)
                .fetchOne();
        //첫한건조회: limit(1).fetchOne() == fetchFirst()
        Member fetchFirst = queryFactory
                .selectFrom(member)
                .fetchFirst();
        //페이징: deprecated -> 복잡한 쿼리에서 종종 결과가 다름
        QueryResults<Member> fetchResults = queryFactory
                .selectFrom(member)
                .fetchResults();
        fetchResults.getTotal();
        List<Member> content = fetchResults.getResults();

        //카운트: deprecated -> 복잡한 쿼리에서 종종 결과가 다름
        long count = queryFactory
                .selectFrom(member)
                .fetchCount();
    }

    //정렬
    /**
     * 순서
     * 1. 회원 나이 내림차순: desc
     * 2. 회원 이름 올림차순: asc
     * 단, 2에서 회원 이름이 없으면 마지막에 출력: null last
     */
    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    //페이징1
    @Test
    public void paging1() {
        List<Member> paging = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();

        assertThat(paging.size()).isEqualTo(2);
    }

    //페이징2
    @Test
    public void paging2() {
        QueryResults<Member> paging = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults(); //

        assertThat(paging.getTotal()).isEqualTo(4);
        assertThat(paging.getLimit()).isEqualTo(2);
        assertThat(paging.getOffset()).isEqualTo(1);
        assertThat(paging.getResults().size()).isEqualTo(2);
    }

    //집합
    @Test
    public void aggregation() {
        List<Tuple> result = queryFactory //Tuple: 여러 datatype 조회 or dto 조회
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    //집합 - 그룹
    // 팀 이름과 각 팁의 평균 연령
    @Test
    public void group() throws Exception {
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);
        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    //조인 - 기본
    // 팀A에 소속된 모든 회원
    @Test
    public void join() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team) //join,inner,left,right etc
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2");
    }

    //조인 - 기본: 세타조인
    // 회원의 이름이 팀 이름과 같은 회원 조회
    @Test
    public void theta_join() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Member> result = queryFactory
                .select(member)
                .from(member, team) //left X
                .where(member.username.eq(team.name))
                .fetch();
        assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

    //조인 - on절
    // 회원과 팀을 조인하면서 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
    // JPQL: select m, t from member m left join m.team t on t.name = 'teamA'
    @Test
    public void join_on_filtering() {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA"))
                .fetch();
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    //조인 - on절: 연관관계 없는 엔티티의 외부조인
    // 회원의 이름이 팀 이름과 같은 대상 외부조인
    @Test
    public void join_on_no_relation() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @PersistenceUnit
    EntityManagerFactory emf;

    //조인 - 페치조인 사용X
    @Test
    public void fetchJoinNo() {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치조인 미적용").isFalse();

    }

    //조인 - 페치조인 사용O
    @Test
    public void fetchJoinUse() {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치조인 적용").isTrue();
    }

    //서브쿼리 - where절
    // 나이가 가장 많은 회원 조회
    @Test
    public void subQuery() {
        QMember memberSub = new QMember("memberSub"); //subquery alias 필요

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();
        assertThat(result)
                .extracting("age")
                .containsExactly(40);
    }

    //서브쿼리 - where절
    // 나이가 평균 이상인 회원 조회
    @Test
    public void subQueryGoe() {
        QMember memberSub = new QMember("memberSub"); //subquery alias 필요

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        select(memberSub.age.avg()) //평균 이상
                                .from(memberSub)
                ))
                .fetch();
        assertThat(result)
                .extracting("age")
                .containsExactly(30, 40);
    }

    //서브쿼리 - where절
    // 나이가 10살을 초과하는 회원 조회
    @Test
    public void subQueryIn() {
        QMember memberSub = new QMember("memberSub"); //subquery alias 필요

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10)) //10살 초과
                ))
                .fetch();
        assertThat(result)
                .extracting("age")
                .containsExactly(20, 30, 40);
    }

    //서브쿼리 - select절
    @Test
    public void selectSubQuery() {
        QMember memberSub = new QMember("memberSub"); //subquery alias

        List<Tuple> result = queryFactory
                .select(member.username,
                        select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    //case - 간단한 경우
    @Test
    public void basicCase() {
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    //case - 복잡한 경우
    @Test
    public void complexCase() {
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    //상수
    @Test
    public void constant() {
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    //문자 더하기
    @Test
    public void concat() {
        //username_age
        List<String> result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }


    /////////////////////////////////////////////////////////중급
    //프로젝션과 결과 반환 - 기본1: datatype 하나
    @Test
    public void simpleProjection() {
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    //프로젝션과 결과 반환 - 기본2: tuple datatype 여러가지
    @Test
    public void tupleProjection() {
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            System.out.println("username = " + username);
            System.out.println("age = " + age);
        }
    }

    //프로젝션과 결과 반환 - DTO조회: jpql
    @Test
    public void findDtoByJPQL() {
        List<MemberDto> result = em.createQuery("select new jpa.querydsl_1.dto.MemberDto(m.username, m.age) " +
                        "from Member m", MemberDto.class)
                .getResultList();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    //프로젝션과 결과 반환 - DTO조회1: Querydsl property setter 접근방법
    @Test
    public void findDtoBySetter() {
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    //프로젝션과 결과 반환 - DTO조회2: Querydsl field 접근방법
    @Test
    public void findDtoByField() {
        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username, //MemberDto의 필드와 동일해야한다.
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    //프로젝션과 결과 반환 - DTO조회2: Querydsl field 접근방법 별칭이 필요할 때
    // 필드 별칭이 필요할 때
    // 서브쿼리 별칭이 필요할 때
    @Test
    public void findUserDto() {
        QMember memberSub = new QMember("memberSub");
        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                        member.username.as("name"), //필드명이 맞지 않으면 null, 필드명을 별칭으로
                        ExpressionUtils.as(JPAExpressions //서브쿼리 별칭
                                .select(memberSub.age.max())
                                .from(memberSub), "age")
                ))
                .from(member)
                .fetch();

        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
    }

    //프로젝션과 결과 반환 - DTO조회3: Querydsl constuctor 접근방법
    @Test
    public void findDtoByConstructor() {
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.username, //타입이 맞아야함
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    //프로젝션과 결과 반환 - @queryProjection
    @Test
    public void findDtoByQueryProjection() {
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username, member.age)) //생성자 타입에 맞춤
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    //동적쿼리1 - BooleanBuilder
    @Test
    public void dynamicQuery_BooleanBuilder() {
        String usernameParam = "member1";
        Integer ageParam = null;

        List<Member> result = searchMember1(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);

    }

    private List<Member> searchMember1(String usernameCond, Integer ageCond) {
        BooleanBuilder builder = new BooleanBuilder();

        if (usernameCond != null) {
            builder.and(member.username.eq(usernameCond));
        }

        if (ageCond != null) {
            builder.and(member.age.eq(ageCond));
        }

        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }

    //동적쿼리2 - Where 다중 파라미터 사용: 쉽게 조립할 수 있음
    @Test
    public void dynamicQuery_WhereParam() {
        String usernameParam = "member1";
        Integer ageParam = null;

        List<Member> result = searchMember2(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember2(String usernameCond, Integer ageCond) {

        return queryFactory
                .selectFrom(member)
                .where(usernameEq(usernameCond), ageEq(ageCond)) //null 무시.
//                .where(allEq(usernameCond, ageCond))
                .fetch();
    }

    private BooleanExpression usernameEq(String usernameCond) {
        if (usernameCond == null) {
            return null;
        }
        return member.username.eq(usernameCond);
    }

    private BooleanExpression ageEq(Integer ageCond) {
        if (ageCond == null) {
            return null;
        }
        return member.age.eq(ageCond);
    }

    private BooleanExpression allEq(String usernameCond, Integer ageCond) {
        return usernameEq(usernameCond).and(ageEq(ageCond));
    }

    //수정 벌크 연산1 : DB와 영속성컨텍스트 값이 달라짐 주의.
    @Test @Commit
    public void bulkUpdate() {
        //member1 = 10 -> 비회원
        //member2 = 20 -> 비회원
        //member3 = 30 -> 유지
        //member4 = 40 -> 유지

        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();

        //영속성컨텍스트 값 지움
        em.flush();
        em.clear();

        List<Member> result = queryFactory
                .selectFrom(member)
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    //수정 벌크 연산2 : 계산
    @Test
    public void bulkAdd() {
        long count = queryFactory
                .update(member)
//                .set(member.age, member.age.add(1)) //+1
                .set(member.age, member.age.multiply(2)) //*2
                .execute();
    }

    //삭제 벌크연산
    @Test
    public void bulkDelete() {
        long count = queryFactory
                .delete(member)
                .where(member.age.gt(18)) //18세 이상
                .execute();
    }

    //SQL function 호출하기1
    // member -> M 바꿔서 출력하기
    @Test
    public void sqlFunction1() {
        List<String> result = queryFactory
                .select(Expressions.stringTemplate("function('replace', {0}, {1}, {2})",
                        member.username, "member", "M"))
                .from(member)
                .fetch();
    }

    //SQL function 호출하기1
    // username -> 소문자로 바꿔서 출력하기
    @Test
    public void sqlFunction2() {
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
//                .where(member.username.eq(Expressions.stringTemplate("function('lower',{0})",
//                        member.username)))
                .where(member.username.eq(member.username.lower())) //db끼리 공통되는 부분은 내장 기능 제공
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
}
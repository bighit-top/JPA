package jpql;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            //JPQL
/*
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(20);
            em.persist(member);

            // 반환타입
            // TypedQuery : 반환타입이 명확할 때
            // Query : 반환타입이 명확하지 않을 때
            TypedQuery<Member> query1 = em.createQuery("select m from Member as m", Member.class);
            TypedQuery<String> query2 = em.createQuery("select m.username from Member as m", String.class);
            Query query3 = em.createQuery("select m.username, m.age from Member as m");

            // 결과조회 : 여러개
            // getResultList() : 결과가 하나 이상일 때. 리스트 반환. 결과가 없으면 빈 리스트 반환. 널체크됨.
            List<Member> resultList = query1.getResultList();
            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }

            // 결과조회 : 하나
            // getSingleResult() : 결과가 반드시 하나. 없으면 NoResultException, 둘이상이면 NonUniqueResultException
            TypedQuery<Member> query4 = em.createQuery("select m from Member as m where m.id = 10", Member.class);
            Member singleResult = query4.getSingleResult();
            System.out.println("singleResult = " + singleResult);

            // 파라미터 처리
            TypedQuery<Member> query5 = em.createQuery("select m from Member m where m.username = :username", Member.class);
            query5.setParameter("username", "member1");
            Member result1 = query5.getSingleResult();
            System.out.println("result = " + result1.getUsername());
            // 위치기반 파라미터 - 쓰지말것.
            TypedQuery<Member> query6 = em.createQuery("select m from Member m where m.username = ?1", Member.class);
            query6.setParameter(1, "member1");

            // 메서드 체이닝
            Member result2 = em.createQuery("select m from Member m where m.username = :username", Member.class)
                    .setParameter("username", "member1")
                    .getSingleResult();
            System.out.println("result = " + result2.getUsername());
*/

            //프로젝션: 영속성컨텍스트에서 관리된다.
/*
            // 엔티티, 임베디드타입, 스칼라 타입
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(20);
            em.persist(member);

            em.flush();
            em.clear();

            // 엔티티
            List<Member> result = em.createQuery("select m from Member m", Member.class)
                    .getResultList();
            Member findMember = result.get(0);
            findMember.setAge(10);
            //
//            List<Team> result2 = em.createQuery("select m.team from Member m", Team.class)
//                    .getResultList();
            List<Team> result2 = em.createQuery("select t from Member m join Team t", Team.class)
                    .getResultList(); //조인은 가급적 명시적으로 하는 것이 좋다.

            // 임베디드 타입
            em.createQuery("select o.address from Order o", Address.class)
                            .getResultList();

            // 스칼라 타입
            em.createQuery("select m.username from Member m", String.class)
                            .getResultList();
            em.createQuery("select distinct m.username, m.age from Member m")
                            .getResultList();
*/
            //조회
/*
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(20);
            em.persist(member);

            em.flush();
            em.clear();

            // 반환타입이 다를 때, 타입케스팅
            // List -> Object[]
            List resultList = em.createQuery("select m.username, m.age from Member m")
                    .getResultList();
            Object o = resultList.get(0);
            Object[] result = (Object[]) o;
            System.out.println("result = " + result[0]);
            System.out.println("result = " + result[1]);

            List<Object[]> resultList2 = em.createQuery("select m.username, m.age from Member m")
                    .getResultList();
            Object[] result2 = resultList2.get(0);
            System.out.println("result2 = " + result2[0]);
            System.out.println("result2 = " + result2[1]);

            // new로 반환
            List<MemberDTO> resultList3 = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();
            MemberDTO memberDTO = resultList3.get(0);
            System.out.println("memberDTO = " + memberDTO.getUsername());
            System.out.println("memberDTO = " + memberDTO.getAge());
*/
            //페이징 API
/*
            for (int i = 0; i < 100; i++) {
                Member member = new Member();
                member.setUsername("member"+i);
                member.setAge(i);
                em.persist(member);
            }

            em.flush();
            em.clear();

            List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(1) //1부터
                    .setMaxResults(10) //10까지
                    .getResultList();
            System.out.println("result.size = " + result.size());
            for (Member member1 : result) {
                System.out.println("member1 = " + member1);
            }
*/

            //조인: 내부조인, 외부조인, 세타조인
/*
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("teamA");
            member.setAge(10);

            member.setTeam(team);

            em.persist(member);

            em.flush();
            em.clear();

//            String query = "select m from Member m join m.team t"; // (inner) join
//            String query = "select m from Member m left join m.team t"; // left (outer) join
//            String query = "select m from Member m, Team t where m.username = t.name"; // seta join
//            String query = "select m from Member m left join m.team t on t.name = 'teamA'"; // on절(연관관계O)
            String query = "select m from Member m left join Team t on m.username = t.name"; // on절(연관관계X)
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();
            System.out.println("result = " + result.size());
*/

            //서브쿼리 : exsist, ALL, ANY(=SOME), in
            // JPA는 WHERE, HAVING절에서만 서브 쿼리 사용 가능
            // SELECT절도 가능(hibernate에서 지원)
//            String query = "select (select avg(m1.age) from Member m1) as avgAge from Member m left join Team t on m.username = t.name";
            // FROM절의 서브 쿼리는 현재 JPQL에서 불가능 : 조인으로 풀 수 있으면 풀어서 해결 or 쿼리2번하고 조립 or nativeQuery

            //타입 표현과 기타식
/*
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("teamA");
            member.setAge(10);
            member.setType(MemberType.ADMIN);

            member.setTeam(team);

            em.persist(member);

            em.flush();
            em.clear();

            // 문자(''), 숫자(10L, 10D, 10F), Boolean(true, FALSE), ENUM, 엔티티 타입
            // enum은 패키지를 포함한 경로 모두를 넣어 검색해야한다.
//            String query = "select m.username, 'HELLO', true from Member m " +
//                    "where m.type = jpql.MemberType.USER";
//            List<Object[]> result = em.createQuery(query)
//                    .getResultList();

            String query = "select m.username, 'HELLO', true from Member m " +
                    "where m.type = :userType";
            List<Object[]> result = em.createQuery(query)
                    .setParameter("userType", MemberType.ADMIN)
                    .getResultList();

            for (Object[] objects : result) {
                System.out.println("objects = " + objects[0]);
                System.out.println("objects = " + objects[1]);
                System.out.println("objects = " + objects[2]);
            }

            // 엔티티 - 상속관계에서.
            // String query = "select i from Item i where type(i) = Book";

            // 기타 : exists, in, and, or, not, 연산자, beetween, like, is null 모두 가능
*/

            //조건식
/*
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("관리자");
            member.setAge(10);
            member.setType(MemberType.ADMIN);

            member.setTeam(team);

            em.persist(member);

            em.flush();
            em.clear();

            // CASE식
//            String query =
//                    "select " +
//                            "case when m.age <= 20 then '학생요금' " +
//                            "     when m.age >= 60 then '경로요금' " +
//                            "     else '일반요금' " +
//                            "end " +
//                    "from Member m";
//            List<String> result = em.createQuery(query, String.class)
//                    .getResultList();
//
//            for (String s : result) {
//                System.out.println("s = " + s);
//            }

            // coalesce : 하나씩 조회해서 null이 아니면 반환
//            String query =
//                    "select coalesce(m.username, '이름 없는 회원') as username " +
//                            "from Member m";
            // nullif : 두 값이 같으면 null 반환, 다르면 첫번째 값 반환
            String query = "select nullif(m.username, '관리자') as useranme " +
                    "from Member m";
            List<String> result = em.createQuery(query, String.class)
                    .getResultList();

            for (String s : result) {
                System.out.println("s = " + s);
            }
*/
            //JPQL 기본함수 : concat, substring, trim, lower, upper,
            // length, locate, abs, sqrt, mod, size, index(JPA용도)
/*
            Member member1 = new Member();
            member1.setUsername("관리자1");
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("관리자2");
            em.persist(member2);

            em.flush();
            em.clear();

//            String query = "select concat('a','b') from Member m"; //문자붙이기
//            String query = "select concat('a' || 'b') from Member m"; //문자붙이기(hibernate)
//            String query = "select substring(m.username, 2, 3) from Member m"; //문자자르기
//            String query = "select locate('de','abcdef') from Member m"; //문자위치
//            String query = "select size(t.members) from Team t"; //컬렉션크기
//            List<Integer> result = em.createQuery(query, Integer.class)
//                    .getResultList();
//            for (Integer s : result) {
//                System.out.println("s = " + s);
//            }
            //사용자 정의 함수 : DB방언 상속받아서 사용 -> persistence.xml 세팅도 변경해주어야함
            // 보통 hibernate구현체 쓰면 각 db함수 registerfunction이 등록되어있음
            String query = "select function('croup_concat', m.username) from Member m";
            List<String> result = em.createQuery(query, String.class)
                    .getResultList();
            for (String s : result) {
                System.out.println("s = " + s);
            }
*/
            //경로 표현식
/*
            Team team = new Team();
            em.persist(team);

            Member member1 = new Member();
            member1.setUsername("관리자1");
            member1.setTeam(team);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("관리자2");
            member2.setTeam(team);
            em.persist(member2);

            em.flush();
            em.clear();

            // 상태 필드 : 경로 탐색 끝
//            String query = "select m.username from Member m";
            // 단일 값 연관 경로 : 묵시적 내부 조인(inner join) 발생, 탐색 가능
            // -> 쿼리 튜닝이 어려워 실무에서 묵시적 조인 자제
            // 묵시적 조인은 항상 내부조인
//            String query = "select m.team from Member m";
//            List<Team> result = em.createQuery(query, Team.class)
//                    .getResultList();
//            for (Team s : result) {
//                System.out.println("s = " + s);
//            }
            // 컬렉션 값 연관 경로 : 묵시적 내부 조인 발생, 탐색 끝.
            // from절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능 -> 실무에서 명시적 조인을 써라.
//            String query = "select t.members from Team t";
//            Collection result = em.createQuery(query, Collection.class)
//                    .getResultList();
//            for (Object o : result) {
//                System.out.println("o = " + o);
//            }
//            String query = "select t.members.size from Team t";
//            Integer result = em.createQuery(query, Integer.class)
//                    .getSingleResult();
//            System.out.println("result = " + result);
            String query = "select m.team.name from Team t join t.members m"; //별칭
            List<Collection> result = em.createQuery(query, Collection.class)
                    .getResultList();
            System.out.println("result = " + result);
*/

            //완전 중요한 페치조인. 완전하게 이해할것. 성능과 튜닝에 지대한 영항을 줌.
            //페치 조인 : 연관 엔티티나 컬렉션을 SQL 한 번에 함께 조회하는 기능.
            /*// sql의 조인 종류가 아님. jpql에서 성능 최적화를 위해 제공하는 기능
            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);

            em.flush();
            em.clear();

            // 다대일
            // 기존의 지연로딩 : N+1 문제 발생
//            String query = "select m from Member m";
//            List<Member> result = em.createQuery(query, Member.class)
//                    .getResultList();
//            for (Member member : result) {
//                System.out.println("member = " + member.getUsername() +
//                        ", " + member.getTeam().getName());
//                //회원1, 팀A : SQL - 지연로딩(프록시)
//                //회원2, 팀A : 1차캐시
//                //회원3, 팀B : SQL - 지연로딩(프록시)
//            }

            // join fetch : N+1 문제 해결. 한번의 쿼리로 끝냄. LAZY설정해도 fetch가 우선이다.
//            String query = "select m from Member m join fetch m.team";
//            List<Member> result = em.createQuery(query, Member.class)
//                    .getResultList();
//            for (Member member : result) {
//                System.out.println("member = " + member.getUsername() +
//                        ", " + member.getTeam().getName());
//                //회원1,회원2,회원3
//            }

            // 일대다
            // 컬렉션 페치 조인 : 데이터가 더 많이 나올수도 있다.(중복)
            // JPA는 데이터에서 결과가 나오는 만큼 돌려줘야함. team에 소속된 member 모두가 각 하나의 결과
//            String query = "select t from Team t join fetch t.members";
//            List<Team> result = em.createQuery(query, Team.class)
//                    .getResultList();
//            for (Team team : result) {
//                System.out.println("team = " + team.getName() +
//                        " | member = " + team.getMember().size());
//                for (Member member : team.getMember()) {
//                    System.out.println("-> member = " + member.getUsername());
//                }
//            }
            // 페치조인과 distinct
            // JPQL의 distinct는 2가지 기능 제공
            // sql에 distinct를 추가 + 애플리케이션에서 엔티티 중복 제거
            // 데이터가 완전하게 똑같아야 중복을 제거한다. 그래서 JPA entity에서 같은 식별자를 가진 엔티티를 한번 더 제거
            // team에 소속된 member 하나의 결과
//            String query = "select distinct t from Team t join fetch t.members";
//            List<Team> result = em.createQuery(query, Team.class)
//                    .getResultList();
//            for (Team team : result) {
//                System.out.println("team = " + team.getName() +
//                        " | member = " + team.getMember().size());
//                for (Member member : team.getMember()) {
//                    System.out.println("-> member = " + member.getUsername());
//                }
//            }

            // 페치 조인(즉시로딩)과 일반 조인(지연로딩)의 차이 : 일반 조인 실행시 연관된 엔티티를 함께 조회하지 않음
            // 일반조인도 데이터 뻥튀기됨, 쿼리 여러번 나감.
//            String query = "select t from Team t join t.members m";
//            String query = "select t from Team t join fetch t.members m"; //
//            List<Team> result = em.createQuery(query, Team.class)
//                    .getResultList();
//            System.out.println("result.size() = " + result.size());
//            for (Team team : result) {
//                System.out.println("team = " + team.getName() +
//                        " | member = " + team.getMember().size());
//                for (Member member : team.getMember()) {
//                    System.out.println("-> member = " + member.getUsername());
//                }
//            }

            // 페치 조인 특징, 한계
            // 별칭 주면 안됨. 연관된 것들 다 가져오는게 페치조인이기 때문. 정합성 이슈 발생.
//            String query = "select t from Team t join fetch t.members as m where m.username";
            // 둘 이상의 컬렉션은 페치 조인 할 수 없다. 다대다.
            // 컬렉션을 페치 조인하면 페이징api를 사용할 수 없다.
//            String query = "select t from Team t join fetch t.members m"; //OneToMany
            //      뒤집어서 사용하거나. onetomany->manytoone
//            String query = "select m from Member m join fetch m.team t"; //ManyToOne
            //      지연로딩(LAZY) 하도록 fetch join을 빼버리고,
            //      @BatchSize(size=100) 배치사이즈 옵션을 준다. perisisence.xml 설정가능
            String query = "select t from Team t";

            List<Team> result = em.createQuery(query, Team.class)
                    .setFirstResult(0)
                    .setMaxResults(2)
                    .getResultList();
            System.out.println("result.size() = " + result.size());
            for (Team team : result) {
                System.out.println("team = " + team.getName() +
                        " | member = " + team.getMember().size());
                for (Member member : team.getMember()) {
                    System.out.println("-> member = " + member.getUsername());
                }
            }
*/
            //엔티티 접근 : 엔티티 직접 사용, 기본키값 사용, 외래키 값 사용
/*
            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            em.flush();
            em.clear();

            // 엔티티 사용
//            String query = "select m from Member m where m = :member";
//            Member result = em.createQuery(query, Member.class)
//                    .setParameter("member", member1)
//                    .getSingleResult();
//            System.out.println("result = " + result);

            // 기본 키 값 사용
//            String query = "select m from Member m where m.id = :memberId";
//            Member result = em.createQuery(query, Member.class)
//                    .setParameter("memberId", member1.getId())
//                    .getSingleResult();
//            System.out.println("result = " + result);

            // 외래키 값 사용
            String query = "select m from Member m where m.team = :team";
            List<Member> result = em.createQuery(query, Member.class)
                    .setParameter("team", teamA)
                    .getResultList();
            for (Member member : result) {
                System.out.println("member = " + member);
            }
*/

            //named쿼리 : 미리 정의해서 이름을 부여해두고 사용하는 JPQL
            // 정적쿼리만 가능, 어노테이션,XML에 정의, 애플리케이션 로딩 시점에 초기화 후 재사용
            // 애플리케이션 로딩 시점에 쿼리를 검증
/*
            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            em.flush();
            em.clear();

            List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("username", "회원1")
                    .getResultList();
            for (Member member : resultList) {
                System.out.println("member = " + member);
            }
*/

            //벌크연산 : sql의 update, delete
            // 주의 : 벌크연산은 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리. 데이터 정합성이 맞지 않을 수 있다.
            // 벌크 연산을 먼저 실행 하거나,
            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setAge(0);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setAge(0);
            em.persist(member2);

//            em.flush();
//            em.clear();

            // 벌크 연산을 먼저 실행 후 영속성 컨텍스트 초기화 해서 해결
            // flush() 자동 호출 (commit, query, flush)
            int resultCount = em.createQuery("update Member m set m.age = 20")
                    .executeUpdate();
            System.out.println("resultCount = " + resultCount);

            em.clear(); // clear 하지 않으면 DB는 반영되어있고, 영속성컨텍스트에는 반영되지 않은 상태가 된다.

            Member findMember = em.find(Member.class, member1.getId());
            System.out.println("findMember = " + findMember.getAge());


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();

    }
}

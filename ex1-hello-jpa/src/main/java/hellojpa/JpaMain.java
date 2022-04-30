package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Arrays;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        //JPA 팩토리를 얻는다
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        //JPA 관리자를 생성한다.
        EntityManager em = emf.createEntityManager();
        //DB커넥션을 얻는다.
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        //code
        try {
            /////////////////////////////////////CRUD
            //Create
/*
            Member member = new Member();
            member.setId(2L);
            member.setName("HelloB");

            //DB에 적용한다.
            em.persist(member);
            tx.commit();
*/
            //Read
/*
            Member findMember = em.find(Member.class, 1L);
            System.out.println("findMember.id = " + findMember.getId());
            System.out.println("findMember.name = " + findMember.getName());
*/
            //Delete
/*
            Member findMember = em.find(Member.class, 1L);
            em.remove(findMember);
            tx.commit();
*/
            //Update
/*
            Member findMember = em.find(Member.class, 1L);
            findMember.setName("HelloJPA");
            //em.persist(findMember); 저장하지 않아도 JPA가 알아서 업데이트한다.
            tx.commit();
*/
            //Read - JPQL : 객체지향 쿼리 (JPQL은 엔티티 객체 대상 쿼리이고 SQL은 데이터베이스 테이블 대상 쿼리)
/*
            List<Member> result = em.createQuery("select m from Member as m", Member.class)
                    .setFirstResult(3) // 페이징 3부터
                    .setMaxResults(10) // 10까지
                    .getResultList();
*/

            /////////////////////////////////////영속성 관리 - 내부 동작 방식
            //비영속
//            Member member = new Member();
//            member.setId(101L);
//            member.setName("HelloJPA");
            //영속
//            System.out.println("### Before ###");
//            em.persist(member);
//            System.out.println("### After ###");
            //준영속상태, 영속성 분리
//            em.detach(member);
            //삭제
//            em.remove(member);

            //////////////////////////////////////영속성 컨텍스트
            //영속
            //1차캐시에서 값을 먼저 조회해서 있으면 가져온다
/*
            Member findMember = em.find(Member.class, 101L);

            System.out.println("findMember.getId() = " + findMember.getId());
            System.out.println("findMember.getName() = " + findMember.getName());
*/

            //1차캐시로 영속 엔티티의 동일성을 보장한다.
/*
            Member findMember1 = em.find(Member.class, 101L);
            Member findMember2 = em.find(Member.class, 101L);
            System.out.println("result = " + (findMember1 == findMember2)); // true
*/

            //transaction.commit() 해야 JPA 내부에 쌓고 있던 코드들을 DB에 반영한다.
/*
            Member member1 = new Member(150L, "A");
            Member member2 = new Member(160L, "B");

            em.persist(member1);
            em.persist(member2);

            System.out.println("=====================");

            tx.commit(); //transaction.commit() 해야 JPA 내부에 쌓고 있던 코드들을 DB에 반영한다.
*/

            //Dirty Checking - 엔티티 수정 - 변경감지 : set만 하면 업데이트됨
/*
            Member member = em.find(Member.class, 150L);
            member.setName("ZZZZZ");
            tx.commit();
*/

            //////////////////////////////////////플러시
            //플러시 : 영속성 컨텍스트의 변경내용을 데이터베이스에 반영
            // 영속성 컨텍스트를 비우지 않음, 영속성 컨텍스트의 변경내용을 데이터베이스에 동기화 하는 것
            // 트랜잭션이라는 작업 단위가 중요 -> 커밋 직전에만 동기화 하면 됨
            //플러시 발생 : 변경감지, 수정된 엔티티 쓰기 지연 SQL 저장소에 등록,
            // 쓰기 지연 SQL 저장소의 쿼리를 데이터베이스에 전송(등록,수정,삭제 쿼리)
            //영속성 컨텍스트를 플러시 하는 방법
            // em.flush() - 직접 호출, 트랜잭션 커밋 - 플러시 자동 호출, JPQL - 플러시 자동 호출
/*
            Member member = new Member(200L, "member200");
            em.persist(member);

            em.flush(); //커밋 전 반영 - 1차 캐시는 유지된다.

            System.out.println("=====================");
            tx.commit();
*/

            //////////////////////////////////////준영속 삭태
            //준영속 상태 : 영속 -> 준영속. 영속상태였다가 준영속상태가 됨
/*
            Member member = em.find(Member.class, 150L);
            member.setName("AAA");

            em.detach(member); //특정 엔티티만 준영속 상태로 전환
//            em.clear(); //영속성 컨텍스트를 완전히 초기화
//            em.close(); //영속성 컨텍스트를 종료

            tx.commit();
*/


            /////////////////////////////////////엔티티 매핑
            //@GeneratedValue(strategy = GenerationType.IDENTITY) 일때,
            // PK가 필요하기 때문에 JPA가 commit전에 insert한다.
            Member member = new Member();

            member.setUsername("A");
            member.setRoleType(RoleType.ADMIN);

            System.out.println("=================");
            em.persist(member);
            System.out.println("member.id = " + member.getId());
            System.out.println("=================");

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
        } finally {
            //JPA 관리자를 종료한다. - 필수
            em.close();
        }
        //JPA 팩토리를 종료한다.
        emf.close();

    }
}

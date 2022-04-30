package jpabook.jpashop;

import jpabook.jpashop.domain2.Member;
import jpabook.jpashop.domain2.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            //foreign key(외래키)를 직접 다룸. 객체지향스럽지 않음. 기존처럼 테이블에 맞추는 방법.
/*
            //입력
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setTeamId(team.getId()); //fk
            em.persist(member);

            //조회
            Member findMember = em.find(Member.class, member.getId());
            Long findTeamId = findMember.getTeamId();
            Team findTeam = em.find(Team.class, findTeamId);
*/

            //JPA의 객체지향스러운 일대다 맵핑 : 단방향
/*
            //입력
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setTeam(team); // orm
            em.persist(member);

//            //영속성 먼저 처리하고 싶으면.
//            em.flush();
//            em.clear();

            //조회
            Member findMember = em.find(Member.class, member.getId());
            Team findTeam = findMember.getTeam();
            System.out.println("findTeam.getName() = " + findTeam.getName());

            //수정
            Team newTeam = em.find(Team.class, 100L);
            findMember.setTeam(newTeam);
*/

            //JPA의 객체지향스러운 다대일 맵핑 : 양방향
/*
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setTeam(team); // orm
            em.persist(member);

            em.flush();
            em.clear();

            Member findMember = em.find(Member.class, member.getId());
            List<Member> members = findMember.getTeam().getMembers();

            for (Member m : members) {
                System.out.println("m.getUsername() = " + m.getUsername());
            }
*/

            //양방향 매핑시 가장 많이 하는 실수 : 연관관계의 주인에 값을 입력하지 않음
/*

//            Member member = new Member();
//            member.setUsername("member1");
//            em.persist(member);
//
//            Team team = new Team();
//            team.setName("TeamA");
//            team.getMembers().add(member);
//            em.persist(team);
//
//            em.flush();
//            em.clear();
            // -> Member 테이블에 team_id가 안들어간다.
            // 수정 후
            Team team = new Team();
            team.setName("TeamA");
//            team.getMembers().add(member);
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.changeTeam(team); // 연관관계 주인에 값을 꼭 넣어주어야 한다.
            em.persist(member);

//            team.addMember(member); // 양방향 세팅에서 한쪽으로 정해서 하나만 써야한다. 때에 따라 맞는 방향에 쓴다.
//            team.getMembers().add(member); // 연관관계 편의 메서드로 대체 : changeTeam() 하면서 처리

            em.flush();
            em.clear();

            Team findTeam = em.find(Team.class, team.getId());
            List<Member> members = findTeam.getMembers();

            for (Member m : members) {
                System.out.println("m.getUsername() = " + m.getUsername());
            }
*/
            //양방향 매핑시 가장 많이 하는 실수 : 양방향 맵핑시 무한 루프 조심. 예:toString(),lombok,JSON 생성 라이브러리
            //컨트롤러에는 엔티티로 반환하지 마라. 무한루프에 빠지거나, 엔티티가 변경될 경우 API스팩이 변경되고 API를 가져다 쓴느 입장에서는 재작업이 필요하다.
            //따라서 간단하게 값만 있는 DTO로 반환하는게 좋다.

            //단방향 맵핑으로 끝내라. 양방향 맵핑은 되도록 필요할 때 추가한다.
            // 객체입장에서 양방향은 별로 이득이 안되?. 모든 것을 객체로 해소할수는 없다.
            // 실무에서 JPQL로 처리해야 하는 경우가 무수히 많다.

            //양방향과 단방향 : 둘다 크게 상관없음. 그래도 단방향 세팅을 잘해서 양방향은 자제?하도록.
/*
            // 실무에서 양방향은 비지니스상 한번에 필요한 경우나 JPQL을 단순하게 짜고 싶을 때 사용한다.
            //양방향
//            Order order = new Order();
//            order.addOrderItem(new OrderItem());
            //단방향
            Order order = new Order();
            em.persist(order);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            em.persist(orderItem);
*/

            //일대다 : fk를 가지고 있는 쪽을 업데이트 해줘야 하기 때문에 따로 조회해야함.
            // 쿼리가 여러번 나간다. 성능상 조금 손해. 실무상 예상할 수 없는 업데이트가 발생할수도 있어서 비추천.
            Member member = new Member();
            member.setUsername("member1");

            em.persist(member);

            Team team = new Team();
            team.setName("teamA");
            team.getMembers().add(member);

            em.persist(team);

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();

    }

}

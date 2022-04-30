import Domain.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            //상속관계
/*
            //입력
            Movie movie = new Movie();
            movie.setDirector("A");
            movie.setActor("B");
            movie.setName("바람과함께사라지다");
            movie.setPrice(10000);

            em.persist(movie);

            em.flush();
            em.clear();

            //조회
            Movie findMovie = em.find(Movie.class, movie.getId());
            System.out.println("findMovie = " + findMovie);
*/

            //mappedSuperClass
/*
            Member member = new Member();
            member.setName("A");
            member.setCreatedBy("kim");
            member.setCreatedDate(LocalDateTime.now());

            em.persist(member);

            em.flush();
            em.clear();
*/
/*
            Book book = new Book();
            book.setAuthor("김영한");
            book.setIsbn("12345");

            em.persist(book);
*/

            //프록시
//            Member member = em.find(Member.class, 1L);
//            printmember(member);
//            printMemberAndTeam(member);

/*
            //em.getReference() : 값이 없으면 프록시가 영속성 컨텍스트에 요청해서 엔티티를 초기화시킴. 엔티티가 변하지는 않음.
            Member member = new Member();
            member.setUserName("hello");
            em.persist(member);

            em.flush();
            em.clear();

//            Member findMember = em.find(Member.class, member.getId());
            Member findMember = em.getReference(Member.class, member.getId());
//            System.out.println("findMember = " + findMember.getClass());
            System.out.println("findMember.getId() = " + findMember.getId());
            System.out.println("findMember.getUserName() = " + findMember.getUserName());
            System.out.println("findMember.getUserName() = " + findMember. getUserName());
*/

            // == 비교X, instanceof 비교O
            // em.find(): == true, em.getReference(): == false
/*
            Member member1 = new Member();
            member1.setUserName("member1");
            em.persist(member1);

            Member member2 = new Member();
            member2.setUserName("member2");
            em.persist(member2);

            em.flush();
            em.clear();

            Member m1 = em.find(Member.class, member1.getId());
//            Member m2 = em.find(Member.class, member2.getId()); //true
            Member m2 = em.getReference(Member.class, member2.getId()); //false
            System.out.println("m1 == m2: " + (m1.getClass() == m2.getClass()));

            // 실무에서 find로 넘어오는지 getReference(프록시)로 넘어오는지 모르기 때문에
            // 타입비교시 반드시 instace of로 비교해야한다.
            System.out.println("m1 == m2: " + (m1 instanceof Member)); //true
            System.out.println("m1 == m2: " + (m2 instanceof Member)); //true
*/

            // 영속성 컨텍스트에 찾는 엔티티가 이미 있으면,
            // em.getReference()를 호출해도 실제 엔티티를 반환한다.
            // 반대로 em.find()를 호출해도 동일하다.
            // em.getReference() == em.find()
/*
            Member member1 = new Member();
            member1.setUserName("member1");
            em.persist(member1);

            em.flush();
            em.clear();

            Member m1 = em.find(Member.class, member1.getId());
            System.out.println("m1 = " + m1.getClass());

            Member reference = em.getReference(Member.class, member1.getId());
            System.out.println("reference = " + reference.getClass());

            System.out.println("m1 == reference: " + (m1 == reference)); //true

            //
            Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("refMember = " + refMember.getClass());

            Member findMember = em.find(Member.class, member1.getId());
            System.out.println("reference = " + findMember.getClass());

            System.out.println("refMember == reference: " + (refMember == findMember)); //true
*/
            // 영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일 때, 프록시를 초기화하면 오류 발생
/*
            Member member1 = new Member();
            member1.setUserName("member1");
            em.persist(member1);

            em.flush();
            em.clear();

            Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("refMember = " + refMember.getClass()); //Proxy

            // 영속을 끊으면
//            em.detach(refMember);
//            em.clear();

            // 영속성 상태 확인
            System.out.println("isLoaded = " + emf.getPersistenceUnitUtil().isLoaded(refMember));

            Hibernate.initialize(refMember); //초기화 (준영속상태에서 오류 : LazyInitializationException)
            System.out.println("refMember = " + refMember.getUserName());

            // 영속성 상태 확인
            System.out.println("isLoaded = " + emf.getPersistenceUnitUtil().isLoaded(refMember));
*/

            // 지연로딩(Domain.team (fetch = FetchType.LAZY)) : 프록시로 조회해두고, 실제 사용시점에 초기화한다. 쿼리2번
            // 즉시로딩(Domain.team (fetch = FetchType.EAGER)) : 함께 조회. 쿼리 조인해서 1번. 실무에서 쓰면안됨(조인테이블이 다 나옴)
/*
            Team team = new Team();
            team.setName("newTeam");
            em.persist(team);

            Member member1 = new Member();
            member1.setUserName("member1");
            member1.setTeam(team);
            em.persist(member1);

            em.flush();
            em.clear();

            Member m = em.find(Member.class, member1.getId());
            System.out.println("m = " + m.getTeam().getClass()); //프록시

            System.out.println("#################");
            System.out.println("Team = " + m.getTeam().getName());; //실제 사용시점(LAZY), 이미 가져온 엔티티값(EAGER)
            System.out.println("#################");
*/

            // 즉시로딩 : JPQL N+1 문제
            // SQL : select * from Member
            // SQL : select * from Team where team_id = xx
/*
            Team team = new Team();
            team.setName("newTeam");
            em.persist(team);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUserName("member1");
            member1.setTeam(team);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUserName("member2");
            member2.setTeam(teamB);
            em.persist(member2);

            em.flush();
            em.clear();

            // 즉시로딩X, 지연로딩O+join 대안 = "select m from Member m fetch join", other
            List<Member> members = em.createQuery("select m from Member m", Member.class)
                    .getResultList();
*/

            // cascade : 영속성 전이 - em.persist()를 여러번 할 필요없이 연관된 엔티티도 저장함 : 부모가 하나일때만 사용.(완전히 하나의 엔티티에 종속될 때)
/*
            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            em.persist(parent);
//            em.persist(child1);
//            em.persist(child2);

            em.flush();
            em.clear();

            // orphanRemoval : 고아객체 삭제 - 참조하는 곳이 하나일때 사용해야함.(@OneToOne, @OneToMany)
            Parent findParent = em.find(Parent.class, parent.getId());
//            findParent.getChildList().remove(0);
            em.remove(findParent); //findParent delete

            // cascade+orphanRemoval을 같이 사용하면 자식의 생명주기를 부모가 관리할 수 있다.
            // 결론적으로 dao, repository가 필요없음
*/


            //임베디드 타입 : @Embedded, @Embeddable(둘 중 하나만 써도 됨)
/*
            Member member = new Member();
            member.setUserName("hello");
            member.setHomeAddress(new Address("city", "street", "zipcode"));
            member.setWorkPeriod(new Period());

            em.persist(member);
*/

            //값타입 공유 참조 : 임베디드 타입 같은 값 타입(객체타)을 여러 엔티티에서 공유하면 같이 변경되어 위험 - side effect
            // 값 타입은 불변객체(immutable object)로 설계해야한다.
            // constructor만 사용. setter를 많들지 않거나 private로 생성
/*
            Address address = new Address("city", "street", "10000");

            Member member = new Member();
            member.setUserName("member1");
            member.setHomeAddress(address);
            em.persist(member);

//            Member member2 = new Member();
//            member2.setUserName("member2");
//            member2.setHomeAddress(address);
//            em.persist(member2);

            // side effect 발생 : member1, member2 둘 다 바뀜
//            member.getHomeAddress().setCity("newCity");

            // 대신 값(인스턴스)를 복사해서 사용
            Address copyAddress = new Address(address.getCity(), address.getStreet(), address.getZipcode());

            Member member2 = new Member();
            member2.setUserName("member2");
            member2.setHomeAddress(copyAddress);
            em.persist(member2);

            member.getHomeAddress().setCity("newCity"); //member2만 변경됨
*/

            //값타입의 비교
/*
            int a = 10;
            int b = 10;

            System.out.println("a == b: " + (a == b)); //true

            Address address1 = new Address("city", "street", "10000");
            Address address2 = new Address("city", "street", "10000");

            //false: 동일성비교
            System.out.println("adress1 == adress2: " + (address1 == address2));
            //false: 동등성비교(override전), true: 동등성비교(override후)
            System.out.println("adress1 equals adress2: " + address1.equals(address2));

            // 동일성(identtiy)비교 : 인스턴스의 참조 값을 비교, == 사용
            // 동등성(equivalence)비교 : 인스턴스의 값을 비교, equals() 사용
            // 값타입은 a.equals(b)를 사용해야함
            // equals는 기본적으로 == 비교이기 때문에 Address에 Override(재정의)해서 사용해야한다.
*/
            //값 타입 컬렉션
/*
            Member member = new Member();
            member.setUserName("member1");
            member.setHomeAddress(new Address("homeCity", "street", "zipcode"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족발");
            member.getFavoriteFoods().add("피자");

            member.getAddressHistory().add(new Address("old1", "street", "10000"));
            member.getAddressHistory().add(new Address("old2", "street", "10000"));

            em.persist(member); //라이프사이클이 member 생명주기와 함께하기 때문에 persist 한번으로 다 들어간다.

            em.flush();
            em.clear();

            // 값 타입 컬렉션은 기본 지연로딩이다.
            System.out.println("###### start ######");
            Member findMember = em.find(Member.class, member.getId());

            List<Address> addressHistory = findMember.getAddressHistory();
            for (Address address : addressHistory) {
                System.out.println("adress.getCity() = " + address.getCity());
            }

            // 값 타입 수정 : 인스턴스를 새로 갈아끼워야 한다. homeCity->newCity
            Address a = findMember.getHomeAddress();
            findMember.setHomeAddress(new Address("newCity", a.getStreet(), a.getZipcode()));

            // 값 타입 컬렉션<String> 업데이트 치킨->한식
            findMember.getFavoriteFoods().remove("치킨");
            findMember.getFavoriteFoods().add("한식");

            // 값 타입 컬렉션 업데이트 object
            // equals()와 hash()코드가 재정의 되어있지 않으면 맞는 값을 일일히 찾아서 삭제 후 바꿔야함
            // 즉 엔티티와 다르게 식별자 개념이 없다.
            // 값 타입 컬렉션에 변경사항이 발생하면 주인엔티티와 연관된 모든 데이터를 삭제하고,
            // 값 타입 컬렉션에 있는 현재 값을 모두 다시 저장한다.
            // 실무에서는 상황에 따라 값 타입 컬렉션 대신 일대다 관계를 고려하는게 낫다.
            // Address -> AddressEntity
            Member member = new Member();
            member.setUserName("member1");
            member.setHomeAddress(new Address("homeCity", "street", "zipcode"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족발");
            member.getFavoriteFoods().add("피자");

            member.getAddressHistory().add(new AddressEntity("old1", "street", "10000"));
            member.getAddressHistory().add(new AddressEntity("old2", "street", "10000"));

            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("###### start ######");
            Member findMember = em.find(Member.class, member.getId());
*/
            //JPA는 JPQL, Criteria, QueryDSL,
            // NativeQuery, jdbc, SpringJdbcTemplate, ibatis, mybatis 모두 지원한다.
            // JPQL
/*
            List<Member> resultList = em.createQuery("select m from Member as m where m.userName like '%kim%'",
                            Member.class
            ).getResultList();

            for (Member member : resultList) {
                System.out.println("member = " + member);
            }
*/
            
            // Criteria
            // 컴파일 시점 오타 등 쿼리 오류를 발견할 수 있음
            // 동적쿼리 작성이 어렵고 쿼리를 보기 어려워 운영과 유지보수가 힘들다. 실무에서 잘 사용 X -> QueryDSL 추천
/*
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Member> query = cb.createQuery(Member.class);

            Root<Member> m = query.from(Member.class);
            CriteriaQuery<Member> cq = query.select(m);

            String username = "test";
            if (username != null) {
                cq.where(cb.equal(m.get("username"), username));
            }
            List<Member> resultList = em.createQuery(cq).getResultList();
*/

            // QueryDSL
            // 자바코드로 JPQL작성 가능, 동적쿼리 작성 편함, 컴파일 시점 문법 오류 찾음. 실무 사용 권장
            // JPQL을 잘 사용하면 금방 배운다.

            // NativeQuery
/*
            em.createNativeQuery("select member_id, city, street, zipcode, username from member")
                    .getResultList();
*/

            // JPA가 아닌 경우 영속성 컨텍스트 관리가 다르게 작동한다.
            // flush() -> commit 할 때, query 날아갈 때
/*
            Member member = new Member();
            member.setUserName("member1");
            em.persist(member);
            // query를 날리기 때문에 여기서 flush() 가 날아간다. 반대로 안날아갈 땐 em.flush() 하고 처리하면 된다.
            List<Member> resultList = em.createNativeQuery("select member_id, city, street, zipcode, username from member", Member.class)
                    .getResultList();

            for (Member m : resultList) {
                System.out.println("m = " + m);
            }
*/

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    private static void logic(Member m1, Member m2) {

    }

/*
    private static void printmember(Member member) {
        System.out.println("member = " + member.getUserName());
    }

    private static void printMemberAndTeam(Member member) {
        String userName = member.getUserName();
        System.out.println("userName = " + userName);

        Team team = member.getTeam();
        System.out.println("team.getName() = " + team.getName());

    }
*/
}

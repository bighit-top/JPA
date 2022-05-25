package springdatajpa.datajpa_1.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import springdatajpa.datajpa_1.dto.MemberDto;
import springdatajpa.datajpa_1.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.NamedEntityGraph;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {//, JpaSpecificationExecutor<Member> {

    //메서드 이름으로 쿼리 생성
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();

    //NamedQuery
//    @Query(name = "Member.findByUsername")
    // 생략 가능 : 관례상 엔티티.메소드명(Member.findByUsername)을 먼저 찾고, 없으면 메서드 이름으로 쿼리 생성
    List<Member> findByUsername(@Param("username") String username);

    //Query: 쿼리 정의
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    //Query: 값 조회
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    //Query: DTO 조회 - constructor 필요
    @Query("select new springdatajpa.datajpa_1.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    //파라미터 바인딩
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    //반환타입
    List<Member> findListByUsername(String username); //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); //단건 Optional

    //페이징과 정렬
//    @Query(value = "select m from Member m left join m.team t",
//            countQuery = "select count(m.username) from Member m") //조회쿼리와 카운트쿼리 분리가 가능하다. 성능향상.
    Page<Member> findPageByAge(int age, Pageable pageable);
    Slice<Member> findSliceByAge(int age, Pageable pageable);
    List<Member> findListByAge(int age, Pageable pageable);

    //벌크 수정 쿼리
    @Modifying(clearAutomatically = true) //update 처리, clearAutomatically 영속성 컨텍스트 클리어
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    //fetch join
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    //entitygraph
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //entitygraph : jpql+entitygraph
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //entitygraph : 메서드 이름으로 쿼리 생성+entitygraph
    @EntityGraph(attributePaths = {"team"})
//    @EntityGraph("Member.all") //NamedEntityGraph
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    //hint: hibernate
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    //lock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    //interface projection(proxy)
    List<UsernameOnly> findProjectionsInterfaceByUsername(@Param("username") String username);

    //class projection(구체클래스)
    List<UsernameOnlyDto> findProjectionsClassByUsername(@Param("username") String username);
    <T> List<T> findProjectionsClassTypeByUsername(@Param("username") String username, Class<T> type);

    //native query
    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    //projection + native query
    @Query(value = "select m.member_id as id, m.username, t.name as teamName " +
            "from Member m left join team t",
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}

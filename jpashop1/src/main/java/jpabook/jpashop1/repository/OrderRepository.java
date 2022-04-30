package jpabook.jpashop1.repository;

import jpabook.jpashop1.domain.Member;
import jpabook.jpashop1.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    //동적쿼리1 - JPQL
    // 쿼리 작성이 어렵다. 띄어쓰기 하나에도 오류가 발생한다.
    public List<Order> findAllByString(OrderSearch orderSearch) {

        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class) .setMaxResults(100); //최대 100건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    //동적쿼리2 - Criteria
    // 자바로 쿼리 작성 가능
    // 쿼리를 가늠하기 어려워 운영과 유지보수가 어렵다.
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);

        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));

        return em.createQuery(cq)
                .setMaxResults(100) //최대 100건
                .getResultList();
    }
    //동적쿼리 기타 - QueryDSL이 좋다.


    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member" +
                                " join fetch o.delivery d", Order.class)
//                "select o from Order o", Order.class) //default_batch_fetch_size 설정으로 member, delivery도 인쿼리 횟수가 줄어든다. fetch join만큼은 아니다.
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Order> findAllWithItem() {
        //distinct 추가 : 중복제거(DB에서는 동일한 값이 아니라 중복제거가 되지 않지만,
        // JPA에서 자체적으로 order의 엔티티 키값을 비교해서 같은 값으면 중복을 제거한다.)
        // 일대다에서 페이징이 안됨(되지만 안됨)
        // : 조회(item기준으로)된 모든 데이터(10만건이면 10만건)를 모두 메모리에 올려서 페이징처리. 메모리가 뻗는다.
        // 컬렉션 fetch join은 하나만 됨. 데이터 정합성 문제 발생한다.
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class)
//                .setFirstResult(1)
//                .setMaxResults(100)
                .getResultList();
    }
}

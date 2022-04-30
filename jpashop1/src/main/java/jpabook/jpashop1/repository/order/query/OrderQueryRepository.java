package jpabook.jpashop1.repository.order.query;

import jpabook.jpashop1.domain.Address;
import jpabook.jpashop1.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders(); //XToOne 먼저 세팅 - 1번 = N개

        //XToMany 컬렉션 반복 돌려 조회 후 세팅
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); //조회 - N번
            o.setOrderItems(orderItems); //세팅
        });
        return result;
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders(); //세팅 - 1번 = N개
        //OrderQueryDto리스트 -> Long리스트로 변환
//        List<Long> orderIds = toOrderIds(result);
        //조회 1번 : sql in 사용
//        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));

        // result에 다시 세팅
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new jpabook.jpashop1.repository.order.query.OrderItemQueryDto" +
                "(" +
                " oi.order.id, i.name, oi.orderPrice, oi.count" +
                ")" +
                " from OrderItem oi" +
                " join oi.item i" +
                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        //메모리에서 데이터 구조 변경
        // OrderItemQueryDto리스트 -> orderID기준 map으로 변경
//        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
//                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));

        return orderItemMap;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
        return orderIds;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select new jpabook.jpashop1.repository.order.query.OrderItemQueryDto" +
                        "(" +
                        " oi.order.id, i.name, oi.orderPrice, oi.count" +
                        ")" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery("select" +
                        " new jpabook.jpashop1.repository.order.query.OrderQueryDto" +
                        "(" +
                        "o.id, m.name, o.orderDate, o.status, d.address" +
                        ")" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery("select new jpabook.jpashop1.repository.order.query.OrderFlatDto" +
                        "(" +
                        "o.id, m.name, o.orderDate, o.status, d.address," +
                        " i.name, oi.orderPrice, oi.count" +
                        ")" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d" +
                " join o.orderItems oi" +
                " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
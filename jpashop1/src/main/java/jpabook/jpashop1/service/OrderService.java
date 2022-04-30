package jpabook.jpashop1.service;

import jpabook.jpashop1.domain.Delivery;
import jpabook.jpashop1.domain.Member;
import jpabook.jpashop1.domain.Order;
import jpabook.jpashop1.domain.OrderItem;
import jpabook.jpashop1.domain.item.Item;
import jpabook.jpashop1.repository.ItemRepository;
import jpabook.jpashop1.repository.MemberRepository;
import jpabook.jpashop1.repository.OrderRepository;
import jpabook.jpashop1.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //JPA트랜잭션 관리. readOnly 조회만
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional //수정 가능
    public Long order(Long memberId, Long itemId, int count) {
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성 - cascade=ALL
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성 - cascade=ALL
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order); // order 처리할 때 cascade=ALL 같이 처리함(라이프사이클이 같을때. 참조가 하나일 때만 사용하도록)
        return order.getId();
    }

    /**
     * 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
    }


    /**
     * 검색
     */
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }
}

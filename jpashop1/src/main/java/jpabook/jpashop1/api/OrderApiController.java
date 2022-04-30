package jpabook.jpashop1.api;

import jpabook.jpashop1.domain.Address;
import jpabook.jpashop1.domain.Order;
import jpabook.jpashop1.domain.OrderItem;
import jpabook.jpashop1.domain.OrderStatus;
import jpabook.jpashop1.repository.OrderRepository;
import jpabook.jpashop1.repository.OrderSearch;
import jpabook.jpashop1.repository.order.query.OrderFlatDto;
import jpabook.jpashop1.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop1.repository.order.query.OrderQueryDto;
import jpabook.jpashop1.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

/**
 * XToMany(OneToMany, ManyToMany) 컬렉션 관계 매핑과 성능 최적화
 * Order
 * * Order -> Member : ManyToOne
 * * Order -> Delivery : OneToOne
 * * * Order -> Items : OneToMany 컬렉션
 */
@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        //lazy 지연로딩 강제 초기화
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            //강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();
//            for (OrderItem orderItem : orderItems) {
//                orderItem.getItem().getName();
//            }
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2() {
//        List<Order> order = orderRepository.findAllByString(new OrderSearch());
//        return order.stream()
//                .map(o -> new OrderDto(o))
//                .collect(Collectors.toList());
        return orderRepository.findAllByString(new OrderSearch()).stream()
                .map(OrderDto::new)
                .collect(toList());
    }

    //distinct fetch join: 페이징X
    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3() {
        //중복된 값은 레퍼런스까지 동일함.
//        List<Order> orders = orderRepository.findAllWithItem();
//        for (Order order : orders) {
//            System.out.println("order ref= " + order +" id= " + order.getId());
//        }
        return orderRepository.findAllWithItem().stream()
                .map(OrderDto::new)
                .collect(toList());
    }

    //@Batchsize
    // XToOne 관계는 fetch join 여러번 가능
    // XToMany 관계는 lazy 지연로딩 설정 후 Batchsize를 설정을 추가한다.
    // : 쿼리를 fetch join이 가능한 부분과 지연로딩할 부분을 나누어 두번 처리한다.
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> orderV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit); // XToOne : fetch join
        List<OrderDto> result = orders.stream() //XToMany : lazy 영속성컨텍스트에 있는 경우 조회하지 않는다.
                .map(OrderDto::new)
                .collect(toList());
        return result;
    }

    //DTO 직접 조회 : JPA에서 DTO를 직접 조회
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> orderV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    //DTO 직접 조회 : 컬렉션 조회 최적화 in절 사용해서 메모리에 미리 조회 후 최적화
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> orderV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    //DTO 직접 조회 : 플랫 데이터 최적화. join결과를 그대로 조회 후 애플리케이션에서 원하는 구조로 직접 변환
    //쿼리1번 : 애플리케이션 추가작업이 크다.
    //order기준으로 페이징이 안됨. 데이터 크기에 따라 V5가 더 나을수도.
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> orderV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        //데이터 구조 변경 및 중복 제거(group by): OrderFlatDto -> OrderFlatDto
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }


    //api 스펙 정의
    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        //OrderItem은 컬렉션 엔티티. dto로 변경.
//        private List<OrderItem> orderItems;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //lazy 초기화 : 영속성컨텍스트에 있는 경우 조회하지 않는다.
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //lazy 초기화
            //엔티티
//            order.getOrderItems().stream().forEach(o -> o.getItem().getName());
//            orderItems = order.getOrderItems();
            //엔티티->dto
            orderItems = order.getOrderItems().stream()
                    .map(OrderItemDto::new)
                    .collect(toList());
        }
    }

    @Getter
    static class OrderItemDto {

        private String itemName; //상품명
        private int orderPrice; //주문가격
        private int count; //주문수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName(); //lazy 초기화 : 영속성컨텍스트에 있는 경우 조회하지 않는다.
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

}

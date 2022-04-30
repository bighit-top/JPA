package jpabook.jpashop1.api;

import jpabook.jpashop1.domain.Address;
import jpabook.jpashop1.domain.Order;
import jpabook.jpashop1.domain.OrderStatus;
import jpabook.jpashop1.repository.OrderRepository;
import jpabook.jpashop1.repository.OrderSearch;
import jpabook.jpashop1.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop1.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * XToOne(ManyToOne, OneToOne) 관계 매핑과 성능 최적화
 * Order
 * * Order -> Member : ManyToOne
 * * Order -> Delivery : OneToOne
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        //Order - member, Member - orders 사이에서 무한루프에 빠짐
        // 양방향 관계에서 한쪽에 @JsonIgnore 설정 필요
        // 설정 이후에도 LAZY(지연로딩)으로 null proxy 오류가 발생하여
        // hibernate5module bean 설정과 강제로딩 옵션 필요 : 강제로딩옵션은 실무사용에 특히 주의
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        // 해결방법은 for문으로 강제 lazy로딩 구문을 만든다.
        for (Order order : all) {
            // lazy 강제 초기화 : 강제로 쿼리를 날려 데이터를 가져옴
            order.getMember().getName();
            order.getDelivery().getAddress();
        }
        // lazy지연로딩을 피하기 위해 eager로딩을 하지 마라.: 성능 최적화가 힘듬.
        return all;
    }

    //엔티티를 직접 사용하지 않는다.
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2() { //실무에서는 list로 반환 말고 감쌀것. []X -> {}O
//        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
//        List<SimpleOrderDto> result = orders.stream()
//                .map(o -> new SimpleOrderDto(o))
//                .collect(Collectors.toList());
//        return result;

        //lazy 지연로딩 문제 있음 : 매번 order, member, delivery 세개 테이블을 조회함 -> 성능X
        // Order 2개
        // N+1 -> 1 + (회원 + 배송) : 최악의 경우. (1차캐시에 있는 데이터는 조회하지 않음.)
        return orderRepository.findAllByString(new OrderSearch()).stream() // order 1번 조회 결과 2개
                .map(SimpleOrderDto::new) // order 결과 2개에 대한 반복 : 회원1 배송1
                .collect(toList());
    }

    //lazy 지연로딩에 대한 해결 : fetch join 1번의 쿼리만 나감.
    // 엔티티를 거치는 방법 : 엔티티 -> dto = 쿼리에서 모든 컬럼을 조회한다.
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3() {
        return orderRepository.findAllWithMemberDelivery().stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
    }

    // DTO로 바로 조회(dto에 명시한 컬럼만 조회한다.)
    // network를 덜 씀. v3 대비 조금 성능이 더 나음. 별 차이 안남.
    // 엔티티가 아닌 dto로 조회하기 때문에 데이터 변경이 불가능하다.
    // dto에 사용할 컬럼을 정의하기 때문에 재사용성이 좋지 않다. 코드가 지저분하다.
    // api에 정의한 스펙을 repository에 코드를 구성하는 것도 좋지 않다.(논리적으로)
    // 차라리 따로 패키지를 구성해 빼는 것이 좋다. (repository.order.simplequery) : OrderRepository는 가능한 순수하게 유지한다.
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }


    @Data
    static class SimpleOrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //lazy 초기화 : 영속성컨텍스트에 있는 경우 조회하지 않는다.
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //lazy 초기화
        }
    }
}

package jpabook.jpashop1.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop1.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

//@BatchSize(size = 100) // XToOne 관계에서의 배치 사이즈 세팅
@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //생성자(new)를 통한 객체 생성을 PROTECTED 레벨로 막는다.
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //LAZY 지연로딩
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;

    private int count;

    //@NoArgsConstructor 처리
//    protected OrderItem() {
//    }

    //생성 메서드 - new 생성 대신 사용. 무분별하게 new로 생성하게 되면 나중에 유지보수가 어렵다.
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    //비즈니스 로직
    public void cancel() {
        getItem().addStock(count);
    }

    //조회 로직
    /**
     * 전체 주문 가격 조회
     * @return
     */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}

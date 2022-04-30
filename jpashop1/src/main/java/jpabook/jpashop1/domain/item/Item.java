package jpabook.jpashop1.domain.item;

import jpabook.jpashop1.domain.Category;
import jpabook.jpashop1.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //상속관계는 여러 전략 중 단일테이블 전략을 사용한다.
@DiscriminatorColumn(name = "dtype") //컬럼명 dtype으로 상속관계 테이블을 관리한다.
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //비지니스 로직
    /**
     * 재고 증가
     * @param stockQuantity
     */
    public void addStock(int stockQuantity) {
        this.stockQuantity += stockQuantity;
    }

    /**
     * 재고 감소
     * @param quantity
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}

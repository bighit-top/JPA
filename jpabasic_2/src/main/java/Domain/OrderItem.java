package Domain;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    private int orderPrice;
    private int count;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Orders order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
}

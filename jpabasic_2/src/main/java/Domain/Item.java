package Domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
//@Inheritance(strategy = InheritanceType.JOINED) //조인 테이블 형태
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //단일 테이블 형태
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) //ITEM 테이블이 없는 각개 테이블 형태
//@DiscriminatorColumn(name = "DTYPE")
public abstract class Item extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();
}

package Domain;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

//    private String city;
//    private String street;
//    private String zipcode;

    @Embedded
    private Address address;

    private DeliveryStatus deliveryStatus;

    @OneToOne(mappedBy = "delivery", fetch = LAZY)
    private Orders order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }
}

package jpabook.jpashop1.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("M") //dtype에 M으로 들어가게 한다.
@Getter @Setter
public class Movie extends Item {

    private String director;
    private String actor;
}

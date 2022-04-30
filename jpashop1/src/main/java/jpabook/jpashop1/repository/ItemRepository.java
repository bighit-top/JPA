package jpabook.jpashop1.repository;

import jpabook.jpashop1.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) {
            em.persist(item); //insert
        } else {
            em.merge(item); //update - 병합방식
            // id식별자로 1차 캐시에 있는 내용에(없으면 DB조회)
            // 변경된 파라미터의 값으로 다 덮어쓰기함
            //   null 등 바뀌어서는 안되는 값까지 모두 변경됨. 실무에서 변경 필요한 값만 바뀌도록 항상 변경감지 쓰도록.
            //   setter 자제하고 생성 메서드 구현하여 사용하도록.
            // 참고: ItemService.updateItemEx
            // Item merge = em.merge(item); // merge 가 영속성 컨텍스트 관리 대상. item은 파라미터 자체로 영속X
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }

}

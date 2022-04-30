package jpabook.jpashop1.service;

import jpabook.jpashop1.domain.item.Book;
import jpabook.jpashop1.domain.item.Item;
import jpabook.jpashop1.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    //병합(merge)의 동작방식의 예. 예는 변경감지로 구현됨.
    @Transactional
    public Item updateItemEx(Long itemId, Book bookParam) {

        //1차캐시에 없으면 식별자로 DB에서 조회해온다.
        Item findItem = itemRepository.findOne(itemId); //영속상태: DB접속 후 값을 찾아왔기 때문

        //식별자 itemId로 조회해 온 엔티티의 모든 것을 넘어온 bookParam으로 덮어쓴다.
        findItem.setId(bookParam.getId());
        findItem.setName(bookParam.getName());
        findItem.setPrice(bookParam.getPrice());
        findItem.setStockQuantity(bookParam.getStockQuantity());
        findItem.setCategories(new ArrayList<>());

//        itemRepository.save(findItem);// 영속 상태에서는 따로 저장할 필요없다.

        return findItem;
    }

/*
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId);
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
    }
*/
    @Transactional
    public void updateItem(Long itemId, UpdateItemDto itemDto) {
        Item findItem = itemRepository.findOne(itemId);
        // set 자제해라 - 이렇게 여기저기 set으로 짜면 변경감지되서 찾기 어렵다.
        findItem.setName(itemDto.getName());
        findItem.setPrice(itemDto.getPrice());
        findItem.setStockQuantity(itemDto.getStockQuantity());
        // 가급적 메서드로 한번에 찾기 쉽게 구현하자.
//        findItem.change();
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

}

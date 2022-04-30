package jpabook.jpashop1.service;

import jpabook.jpashop1.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest { //변경감지와 병합 : 슈퍼중요

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception {
        Book book = em.find(Book.class, 1L);

        //Transaction
        book.setName("book");

        //dirty checking == 변경감지 : update 날리지 않아도 JPA가 감지해서 처리함.

        //준영속상태 : db에 다녀온 후 영속상태에서 벗어난, JPA가 관리하지 않는 상태
        // new로 생성하더라도 한번 db에 다녀오면 준영속상태에 있다.
        // 준영속 엔티티를 수정하는 2가지 방법 : 변경감지, 병합(merge())

    }
}

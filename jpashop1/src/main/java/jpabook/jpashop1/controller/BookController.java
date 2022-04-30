package jpabook.jpashop1.controller;

import jpabook.jpashop1.domain.item.Book;
import jpabook.jpashop1.domain.item.Item;
import jpabook.jpashop1.service.ItemService;
import jpabook.jpashop1.service.UpdateItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {

        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        //Order에서 처럼 생성메서드륾 만들어 set을 제거하는게 좋은 설계다.

        itemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form) {

        //엔티티에 직접 접근하는 것은 좋지 않다.
/*
        Book book = new Book();

        book.setId(form.getId()); //ID 조작은 보안에 취약 : 뒷단에서 권한 검증 처리 필요 or 세션계층(요즘잘안씀)
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
*/
        // 변경감지(dirty checking) 파라미터로 필요한 것만 받아서 처리하도록 구현
//        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());

        // 넘길 값이 많으면 DTO를 따로 만들어서 넣는편이 좋다.
        UpdateItemDto itemDto = new UpdateItemDto();
        itemDto.setName(form.getName());
        itemDto.setPrice(form.getPrice());
        itemDto.setStockQuantity(form.getStockQuantity());

        itemService.updateItem(itemId, itemDto);
        return "redirect:/items";
    }
}
